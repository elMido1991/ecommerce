package com.alten.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
@EnableWebSecurity
public class KeycloakServletSecurityConfiguration {

    private final RbacConfig rbacConfig;

    public KeycloakServletSecurityConfiguration(RbacConfig rbacConfig) {
        this.rbacConfig = rbacConfig;
    }

    @Bean
    public SecurityFilterChain servletSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> {


                    authz
                            .requestMatchers(HttpMethod.POST, "/api/v1/auth/token", "/api/v1/auth/register")
                            .permitAll();

                    // Group rules by "METHOD pattern" key
                    Map<String, List<RbacRule>> byKey = rbacConfig.getRbac().stream()
                            .collect(Collectors.groupingBy(rule ->
                                    rule.method().toUpperCase() + " " +
                                            rule.path().replaceAll("\\{[^/]+}", "*")
                            ));

                    byKey.forEach((key, rules) -> {
                        String[] parts = key.split(" ", 2);
                        HttpMethod method = HttpMethod.valueOf(parts[0]);
                        String pattern = parts[1];

                        authz.requestMatchers(method, pattern)
                                .access((authentication, context) -> {
                                    // Extract granted authorities from the Authentication
                                    Set<String> granted = authentication.get().getAuthorities().stream()
                                            .map(GrantedAuthority::getAuthority)
                                            .collect(Collectors.toSet());

                                    // Allow if any rule in this group matches both role and permission
                                    boolean allowed = rules.stream().anyMatch(rule ->
                                            granted.contains("ROLE_" + rule.role())
                                    );
                                    return new AuthorizationDecision(allowed);
                                });
                    });

                    authz.anyRequest().authenticated();
                })
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakAuthoritiesConverter());
        return converter;
    }
}
