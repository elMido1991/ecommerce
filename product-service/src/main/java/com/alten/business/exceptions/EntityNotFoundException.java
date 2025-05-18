package com.alten.business.exceptions;

public class EntityNotFoundException extends BusinessException {
    public EntityNotFoundException(ExceptionCodes exceptionCodes) {
        super(exceptionCodes);
    }
}