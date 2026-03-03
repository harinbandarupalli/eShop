package com.bilbo.store.mapper;

import com.bilbo.store.dto.ProductImageDTO;
import com.bilbo.store.entites.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

    ProductImageDTO toDto(ProductImage productImage);

    @Mapping(target = "product", ignore = true) // Ignored to avoid circular dependencies
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    @Mapping(target = "createdTimestamp", ignore = true)
    @Mapping(target = "lastUpdatedTimestamp", ignore = true)
    ProductImage toEntity(ProductImageDTO productImageDTO);
}
