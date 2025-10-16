package com.bilbo.store.mapper;

import com.bilbo.store.dto.ProductCategoryDTO;
import com.bilbo.store.entites.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {

    ProductCategoryMapper INSTANCE = Mappers.getMapper(ProductCategoryMapper.class);

    ProductCategoryDTO toDto(ProductCategory productCategory);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    ProductCategory toEntity(ProductCategoryDTO productCategoryDTO);
}
