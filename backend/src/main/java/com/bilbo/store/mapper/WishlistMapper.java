package com.bilbo.store.mapper;

import com.bilbo.store.dto.WishlistDTO;
import com.bilbo.store.entites.Wishlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {WishlistItemMapper.class})
public interface WishlistMapper {

    @Mapping(source = "userId", target = "userId")
    WishlistDTO toDto(Wishlist wishlist);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    @Mapping(target = "createdTimestamp", ignore = true)
    @Mapping(target = "lastUpdatedTimestamp", ignore = true)
    Wishlist toEntity(WishlistDTO wishlistDTO);
}
