package com.bilbo.store.mapper;

import com.bilbo.store.dto.WishlistItemDTO;
import com.bilbo.store.entites.WishlistItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface WishlistItemMapper {

    WishlistItemMapper INSTANCE = Mappers.getMapper(WishlistItemMapper.class);

    @Mapping(source = "product.id", target = "productId")
    WishlistItemDTO toDto(WishlistItem wishlistItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "wishlist", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    WishlistItem toEntity(WishlistItemDTO wishlistItemDTO);
}
