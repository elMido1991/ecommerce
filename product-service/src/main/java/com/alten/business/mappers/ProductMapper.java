package com.alten.business.mappers;


import com.alten.business.db.entities.InventoryStatus;
import com.alten.business.db.entities.Product;
import com.alten.controllers.dtos.ProductDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {


    Product toEntity(ProductDto dto);
    ProductDto toDto(Product product);
    List<ProductDto> toDtos(List<Product> products);

    void refreshEntity(ProductDto dto, @MappingTarget Product product);

    @AfterMapping
    default void refreshInventoryStatus(@MappingTarget Product product) {
        product.setInventoryStatus(InventoryStatus.getStatus(product.getQuantity()));
    }
}