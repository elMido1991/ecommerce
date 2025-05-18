package com.alten.business.services;

import com.alten.business.db.entities.Basket;
import com.alten.business.db.entities.InventoryStatus;
import com.alten.business.db.entities.Product;
import com.alten.business.db.repositories.BasketRepository;
import com.alten.business.exceptions.ExceptionCodes;
import com.alten.business.exceptions.ProductOutOfStockException;
import com.alten.business.mappers.ProductMapper;
import com.alten.controllers.dtos.BasketItemDto;
import com.alten.controllers.dtos.ProductDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class BasketService {

    private final BasketRepository basketRepository;
    private final ProductService productService;
    private final ProductMapper productMapper;

    /**
     * add Product To Basket
     */
    @Transactional
    public void addProductToBasket(BasketItemDto basketItem) {
        Basket basket = basketRepository.findBasketByUserId(basketItem.getUserId()).orElseGet(() -> {
            Basket newBasket = new Basket();
            newBasket.setUserId(basketItem.getUserId());
            return newBasket;
        });
        ProductDto productDto = productService.getProduct(basketItem.getProductId());
        Product product = productMapper.toEntity(productDto);

        if (product.getInventoryStatus() == InventoryStatus.OUT_OF_STOCK || product.getQuantity() < basketItem.getQuantity()) {
            throw new ProductOutOfStockException(ExceptionCodes.PRODUCT_STOCK_BELOW_DEMANDED_QUANTITY);
        }

        basket.addProduct(product, basketItem.getQuantity());
        basketRepository.save(basket);
    }
}
