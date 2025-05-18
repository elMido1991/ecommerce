package com.alten.business.services;


import com.alten.business.db.entities.Product;
import com.alten.business.db.entities.Wishlist;
import com.alten.business.db.repositories.ProductRepository;
import com.alten.business.db.repositories.WishlistRepository;
import com.alten.business.exceptions.EntityNotFoundException;
import com.alten.business.exceptions.ExceptionCodes;
import com.alten.business.exceptions.WishListNotBelongToUserException;
import com.alten.controllers.dtos.WishListItem;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@AllArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    /**
     * add Product From Wishlist
     */
    @Transactional(rollbackFor = {EntityNotFoundException.class, WishListNotBelongToUserException.class})
    public void addProductToWishlist(WishListItem wishListItem) {
        Wishlist wishlist = wishlistRepository.findById(wishListItem.getWishListId())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionCodes.WISHLIST_NOT_FOUND));
        if (!Objects.equals(wishlist.getUserId(), wishListItem.getUserId())) {
            throw new WishListNotBelongToUserException(ExceptionCodes.USER_DOESNT_HAVE_WISH_LIST);
        }

        Product product = productRepository.findById(wishListItem.getProductId())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionCodes.PRODUCT_NOT_FOUND));
        wishlist.getProducts().add(product);
        wishlistRepository.save(wishlist);
    }

    /**
     * remove Product From Wishlist
     */
    @Transactional(rollbackFor = {EntityNotFoundException.class, WishListNotBelongToUserException.class})
    public void removeProductFromWishlist(WishListItem wishListItem) {
        Wishlist wishlist = wishlistRepository.findById(wishListItem.getWishListId())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionCodes.WISHLIST_NOT_FOUND));
        if (!wishlist.getUserId().equals(wishListItem.getUserId())) {
            throw new WishListNotBelongToUserException(ExceptionCodes.USER_DOESNT_HAVE_WISH_LIST);
        }

        Product product = productRepository
                .findById(wishListItem.getProductId())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionCodes.PRODUCT_NOT_FOUND));
        wishlist.getProducts().remove(product);
        wishlistRepository.save(wishlist);
    }

    @Transactional
    public Wishlist createWishlist(Wishlist wishlist) {
        return wishlistRepository.save(wishlist);
    }
}
