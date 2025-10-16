package com.bilbo.store.mapper;

import com.bilbo.store.dto.WishlistDTO;
import com.bilbo.store.entites.Wishlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {WishlistItemMapper.class})
public interface WishlistMapper {

    WishlistMapper INSTANCE = Mappers.getMapper(WishlistMapper.class);

    @Mapping(source = "user.id", target = "userId")
    WishlistDTO toDto(Wishlist wishlist);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    Wishlist toEntity(WishlistDTO wishlistDTO);
}
