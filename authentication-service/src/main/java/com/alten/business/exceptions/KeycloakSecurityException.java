package com.alten.business.exceptions;

public class KeycloakSecurityException extends RuntimeException{
    public KeycloakSecurityException(String msg){
        super(msg);
    }
}
