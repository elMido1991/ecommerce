package com.alten.business.exceptions;

import lombok.Getter;

@Getter
public abstract class BusinessException extends RuntimeException {
    private final String code;

    protected BusinessException(ExceptionCodes exceptionCodes) {
        super(exceptionCodes.getMessage());
        this.code = exceptionCodes.getCode();
    }

}