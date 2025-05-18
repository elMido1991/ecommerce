package com.alten.business.exceptions;

public class WishListNotBelongToUserException extends BusinessException {
    public WishListNotBelongToUserException(ExceptionCodes exceptionCodes) {
        super(exceptionCodes);
    }
}