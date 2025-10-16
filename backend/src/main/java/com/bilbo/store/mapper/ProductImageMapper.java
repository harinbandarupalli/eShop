package com.bilbo.store.mapper;

import com.bilbo.store.dto.ProductImageDTO;
import com.bilbo.store.entites.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

  ProductImageMapper INSTANCE = Mappers.getMapper(ProductImageMapper.class);

  ProductImageDTO toDto(ProductImage productImage);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "product", ignore = true) // Ignored to avoid circular dependencies
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "lastUpdatedBy", ignore = true)
  ProductImage toEntity(ProductImageDTO productImageDTO);
}
