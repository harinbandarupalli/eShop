package com.bilbo.store.mapper;

import com.bilbo.store.dto.WishlistItemDTO;
import com.bilbo.store.entites.WishlistItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface WishlistItemMapper {

    @Mapping(source = "product.id", target = "productId")
    WishlistItemDTO toDto(WishlistItem wishlistItem);

    @Mapping(target = "wishlist", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    @Mapping(target = "createdTimestamp", ignore = true)
    @Mapping(target = "lastUpdatedTimestamp", ignore = true)
    WishlistItem toEntity(WishlistItemDTO wishlistItemDTO);
}
