package com.alten.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableWebFluxSecurity
public class KeycloakReactiveSecurityConfiguration {

    private final RbacConfig rbacConfig;

    public KeycloakReactiveSecurityConfiguration(RbacConfig rbacConfig) {
        this.rbacConfig = rbacConfig;
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authz -> {
                    //authorize authentication and registration operations
                    authz
                            .pathMatchers(HttpMethod.POST, "/api/v1/auth/token", "/api/v1/auth/register")
                            .permitAll();

                    // extract rules
                    Map<String, List<RbacRule>> byKey = rbacConfig.getRbac().stream()
                            .collect(Collectors.groupingBy(rule ->
                                    rule.method().toUpperCase() + " "
                                            + rule.path().replaceAll("\\{[^/]+}", "*")
                            ));

                    // 2) register interceptor
                    for (Map.Entry<String, List<RbacRule>> entry : byKey.entrySet()) {
                        String[] parts = entry.getKey().split(" ", 2);
                        HttpMethod method   = HttpMethod.valueOf(parts[0]);
                        String     pattern  = parts[1];
                        List<RbacRule> rules = entry.getValue();

                        authz
                                .pathMatchers(method, pattern)
                                .access((authMono, ctx) ->
                                        authMono.map(auth -> {
                                            // Collect the granted authorities once
                                            Set<String> granted = auth.getAuthorities().stream()
                                                    .map(GrantedAuthority::getAuthority)
                                                    .collect(Collectors.toSet());

                                            // If any one rule passes (role+permission), allow
                                            boolean allowed = rules.stream().anyMatch(rule ->
                                                    granted.contains("ROLE_" + rule.role())
                                                            //&&  granted.contains("PERM_" + rule.permission())
                                            );

                                            return new AuthorizationDecision(allowed);
                                        })
                                );
                    }

                    //authenticate any request
                    authz.anyExchange().authenticated();
                })
                //jwt manager to extract roles from token
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    private ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakAuthoritiesConverter());
        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }
}
