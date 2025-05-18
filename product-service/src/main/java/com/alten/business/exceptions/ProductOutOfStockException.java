package com.alten.business.exceptions;

public class ProductOutOfStockException  extends BusinessException  {
    public ProductOutOfStockException(ExceptionCodes exceptionCodes) {
        super(exceptionCodes);
    }
}