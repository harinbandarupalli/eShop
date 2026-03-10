package com.bilbo.store.mapper;

import com.bilbo.store.dto.CartDTO;
import com.bilbo.store.entites.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { CartItemMapper.class })
public interface CartMapper {

    CartDTO toDto(Cart cart);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "createdTimestamp", ignore = true)
    @Mapping(target = "lastUpdatedTimestamp", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    Cart toEntity(CartDTO dto);
}
