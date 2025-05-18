package com.alten.security;

public record RbacRule(String path, String method, String role, String permission) {}


