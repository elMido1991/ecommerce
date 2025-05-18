package com.alten.controllers.dtos;

import com.alten.business.db.entities.InventoryStatus;
import com.alten.controllers.dtos.validations.EnumValidation;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductDto {
    private Long id;
    @NotNull
    private String code;
    @NotNull
    private String name;
    private String description;
    private String image;
    private String category;

    @Positive(message = "Price must be positive")
    @NotNull
    private Double price;

    @Positive(message = "Quantity must be positive")
    @NotNull
    private Integer quantity;

    private String internalReference;
    private Long shellId;
    @EnumValidation(
            enumClass = InventoryStatus.class,
            message = "Inventory status must be one of: IN_STOCK, LOW_STOCK, or OUT_OF_STOCK"
    )
    private InventoryStatus inventoryStatus;
    private Short rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}