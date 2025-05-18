package com.alten.controllers;

import com.alten.business.services.BasketService;
import com.alten.controllers.dtos.BasketItemDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/baskets")
@RequiredArgsConstructor
@Tag(name = "Basket", description = "baskets management APIs")
public class BasketController {

    private final BasketService basketService;

    /**
     * Add a product to a basket.
     */
    @Operation(
            summary = "Add Product to Basket",
            description = "Adds a product to user basket by their IDs."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product added to basket successfully."),
            @ApiResponse(responseCode = "400", description = "Failed to add product to basket due to invalid input.")
    })
    @PostMapping("/products")
    public ResponseEntity<String> addProductToBasket(@RequestBody BasketItemDto basketItemDto) {
        try {
            basketService.addProductToBasket(basketItemDto);
            return ResponseEntity.ok("Product added to basket successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to add product to basket: " + e.getMessage());
        }
    }


}