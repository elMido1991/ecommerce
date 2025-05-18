package com.alten.business.db.entities;

public enum InventoryStatus {
    IN_STOCK,
    LOW_STOCK,
    OUT_OF_STOCK;

    public static InventoryStatus getStatus(Integer currentQuantity){
        if (currentQuantity != null) {
            if (currentQuantity > 0 && currentQuantity <= 30) {
                return InventoryStatus.LOW_STOCK;
            } else if ( currentQuantity > 30) {
                return InventoryStatus.IN_STOCK;
            }
        }
        return InventoryStatus.OUT_OF_STOCK;
    }
}