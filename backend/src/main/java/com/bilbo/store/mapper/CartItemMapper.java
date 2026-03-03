package com.bilbo.store.mapper;

import com.bilbo.store.dto.CartItemDTO;
import com.bilbo.store.entites.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CartItemMapper {

    @Mapping(source = "product.id", target = "productId")
    CartItemDTO toDto(CartItem cartItem);

    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    @Mapping(target = "createdTimestamp", ignore = true)
    @Mapping(target = "lastUpdatedTimestamp", ignore = true)
    CartItem toEntity(CartItemDTO cartItemDTO);
}
