package com.alten.controllers;

import com.alten.business.db.entities.Wishlist;
import com.alten.business.mappers.WishListMapper;
import com.alten.business.services.WishlistService;
import com.alten.controllers.dtos.WishListDto;
import com.alten.controllers.dtos.WishListItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wishlists")
@RequiredArgsConstructor
@Tag(name = "WishList", description = "WishList management APIs")
public class WishListController {

    private final WishlistService wishlistService;
    private final WishListMapper wishListMapper;

    @Operation(
            summary = "Create a new wishList",
            description = "Creates a new wishList with the provided information"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "WishList successfully created",
                    content = @Content(schema = @Schema(implementation = WishListDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<WishListDto> createWishList(
            @RequestBody
            @Parameter(description = "WishList to create", required = true)
            WishListDto wishListDto) {
        Wishlist wishlist = wishListMapper.toEntity(wishListDto);
        wishlist = wishlistService.createWishlist(wishlist);
        return ResponseEntity.ok(wishListMapper.toDto(wishlist));
    }

    /**
     * Add a product to a wishlist.
     */
    @Operation(
            summary = "Add a WishList to a Wishlist",
            description = "Allows a user to add a specific product to their wishlist by providing user ID, wishlist ID, and product ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "WishList successfully added to the wishlist."),
            @ApiResponse(responseCode = "400", description = "Failed to add product due to invalid input or other errors.")
    })
    @PostMapping("/products")
    public ResponseEntity<String> addProductToWishlist(@RequestBody WishListItem wishListItem) {
        try {
            wishlistService.addProductToWishlist(wishListItem);
            return ResponseEntity.ok("product added to wishlist.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Remove a product from a wishlist.
     */
    @Operation(
            summary = "Remove a WishList from a Wishlist",
            description = "Allows a user to remove a specific product from their wishlist by providing user ID, wishlist ID, and product ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "WishList successfully removed from the wishlist."),
            @ApiResponse(responseCode = "400", description = "Failed to remove product due to invalid input or other errors.")
    })
    @DeleteMapping("/products")
    public ResponseEntity<String> removeProductFromWishlist(@RequestBody WishListItem wishListItem) {
        try {
            wishlistService.removeProductFromWishlist(wishListItem);
            return ResponseEntity.ok("product removed from wishlist.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}