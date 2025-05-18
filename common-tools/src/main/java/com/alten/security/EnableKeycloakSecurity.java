package com.alten.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableConfigurationProperties(RbacConfig.class)
@Import({
        KeycloakReactiveSecurityConfiguration.class,
        KeycloakServletSecurityConfiguration.class
})
public @interface EnableKeycloakSecurity {
}
