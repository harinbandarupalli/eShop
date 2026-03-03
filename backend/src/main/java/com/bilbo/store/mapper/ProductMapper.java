package com.bilbo.store.mapper;

import com.bilbo.store.dto.ProductDTO;
import com.bilbo.store.entites.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductImageMapper.class, ProductCategoryMapper.class})
public interface ProductMapper {

    @Mapping(source = "category.id", target = "categoryId")
    ProductDTO toDto(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    @Mapping(target = "createdTimestamp", ignore = true)
    @Mapping(target = "lastUpdatedTimestamp", ignore = true)
    Product toEntity(ProductDTO productDTO);
}
