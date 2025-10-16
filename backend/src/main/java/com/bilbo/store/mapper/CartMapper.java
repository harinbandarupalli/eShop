package com.bilbo.store.mapper;

import com.bilbo.store.dto.CartDTO;
import com.bilbo.store.entites.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {

    CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);

    @Mapping(source = "user.id", target = "userId")
    CartDTO toDto(Cart cart);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    Cart toEntity(CartDTO cartDTO);
}
