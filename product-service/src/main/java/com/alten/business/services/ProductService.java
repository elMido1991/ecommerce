package com.alten.business.services;



import com.alten.business.db.entities.Product;
import com.alten.business.db.repositories.ProductRepository;
import com.alten.business.exceptions.*;
import com.alten.business.mappers.ProductMapper;
import com.alten.controllers.dtos.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    /**
     * Create a new product
     */
    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        validateProduct(productDto);
        Product product = productMapper.toEntity(productDto);
        return productMapper.toDto(productRepository.save(product));
    }

    /**
     * Retrieve all products
     */
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return productMapper.toDtos(productRepository.findAll());
    }

    /**
     * Get a product by its ID
     */
    @Transactional(readOnly = true, rollbackFor = EntityNotFoundException.class)
    public ProductDto getProduct(Long id) {
        return productMapper.toDto(productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionCodes.PRODUCT_NOT_FOUND)));
    }

    /**
     * Update a product
     */
    @Transactional(rollbackFor = EntityNotFoundException.class)
    public ProductDto updateProduct(Long id,ProductDto productDto) {
        Product productInDB = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionCodes.PRODUCT_NOT_FOUND));
        productMapper.refreshEntity(productDto, productInDB);
        return productMapper.toDto(productRepository.save(productInDB));
    }

    /**
     * Delete a product
     */
    @Transactional(rollbackFor = EntityNotFoundException.class)
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException(ExceptionCodes.PRODUCT_NOT_FOUND);
        }
        productRepository.deleteById(id);
    }

    /**
     * Validate product data
     */
    private void validateProduct(ProductDto product) {
        if (product.getPrice() != null && product.getPrice() < 0) {
            throw new ProductPriceNegativeException(ExceptionCodes.PRICE_CANNOT_BE_NEGATIVE);
        }
        if (product.getQuantity() != null && product.getQuantity() < 0) {
            throw new ProductQuantityNegativeException(ExceptionCodes.QUANTITY_CANNOT_BE_NEGATIVE);
        }
        if (product.getRating() != null && (product.getRating() < 0 || product.getRating() > 5)) {
            throw new ProductRatingOutOfRangeException(ExceptionCodes.RATING_CANNOT_BE_UNDER_0_OR_ABOVE_5);
        }
    }

}