package com.bilbo.store.mapper;

import com.bilbo.store.dto.CartItemDTO;
import com.bilbo.store.entites.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    CartItemMapper INSTANCE = Mappers.getMapper(CartItemMapper.class);

    @Mapping(source = "product.id", target = "productId")
    CartItemDTO toDto(CartItem cartItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    CartItem toEntity(CartItemDTO cartItemDTO);
}
