package com.alten.business.exceptions;

import lombok.Getter;

@Getter
public enum ExceptionCodes {
    PRODUCT_NOT_FOUND("PD0001", "Product not found."),
    PRICE_CANNOT_BE_NEGATIVE("PD0002", "Price cannot be negative"),
    QUANTITY_CANNOT_BE_NEGATIVE("PD0003", "Quantity cannot be negative"),
    RATING_CANNOT_BE_UNDER_0_OR_ABOVE_5("PD0004", "Rating cannot be under 0 or above 5"),
    PRODUCT_STOCK_BELOW_DEMANDED_QUANTITY("PD0005", "Product stock below demanded quantity."),
    WISHLIST_NOT_FOUND("WL0001", "Wishlist not found."),
    USER_DOESNT_HAVE_WISH_LIST("WL0002", "User doesn't have wish list.")
    ;



    private final String code;
    private final String message;

    ExceptionCodes(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
