package com.alten.business.exceptions;

public class ProductRatingOutOfRangeException extends BusinessException {
    public ProductRatingOutOfRangeException(ExceptionCodes exceptionCodes) {
        super(exceptionCodes);
    }
}