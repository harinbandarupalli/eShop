package com.bilbo.store.mapper;

import com.bilbo.store.dto.ProductCategoryDTO;
import com.bilbo.store.entites.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {

    ProductCategoryDTO toDto(ProductCategory productCategory);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    @Mapping(target = "createdTimestamp", ignore = true)
    @Mapping(target = "lastUpdatedTimestamp", ignore = true)
    ProductCategory toEntity(ProductCategoryDTO productCategoryDTO);
}
