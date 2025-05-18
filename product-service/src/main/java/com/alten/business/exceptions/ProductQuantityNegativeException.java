package com.alten.business.exceptions;

public class ProductQuantityNegativeException extends BusinessException {
    public ProductQuantityNegativeException(ExceptionCodes exceptionCodes) {
        super(exceptionCodes);
    }
}