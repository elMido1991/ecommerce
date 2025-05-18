package com.alten.business.mappers;


import com.alten.business.db.entities.Wishlist;
import com.alten.controllers.dtos.WishListDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WishListMapper {


    Wishlist toEntity(WishListDto dto);
    WishListDto toDto(Wishlist entity);
}