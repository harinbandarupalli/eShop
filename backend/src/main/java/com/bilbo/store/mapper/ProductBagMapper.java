package com.bilbo.store.mapper;

import com.bilbo.store.dto.ProductBagDTO;
import com.bilbo.store.entites.ProductBag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { ProductMapper.class, ProductCategoryMapper.class })
public interface ProductBagMapper {

    ProductBagDTO toDto(ProductBag bag);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "createdTimestamp", ignore = true)
    @Mapping(target = "lastUpdatedTimestamp", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    ProductBag toEntity(ProductBagDTO dto);
}
