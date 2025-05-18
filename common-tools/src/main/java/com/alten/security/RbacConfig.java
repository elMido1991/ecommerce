package com.alten.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "security")
public class RbacConfig {
    private List<RbacRule> rbac = new ArrayList<>();
    public List<RbacRule> getRbac() { return rbac; }
    public void setRbac(List<RbacRule> rbac) { this.rbac = rbac; }
}
