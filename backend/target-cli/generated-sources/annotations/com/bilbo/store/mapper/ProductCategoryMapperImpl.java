package com.bilbo.store.mapper;

import com.bilbo.store.dto.ProductCategoryDTO;
import com.bilbo.store.entites.ProductCategory;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-09T16:56:19-0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Homebrew)"
)
@Component
public class ProductCategoryMapperImpl implements ProductCategoryMapper {

    @Override
    public ProductCategoryDTO toDto(ProductCategory category) {
        if ( category == null ) {
            return null;
        }

        ProductCategoryDTO productCategoryDTO = new ProductCategoryDTO();

        productCategoryDTO.setId( category.getId() );
        productCategoryDTO.setName( category.getName() );
        productCategoryDTO.setDescription( category.getDescription() );

        return productCategoryDTO;
    }

    @Override
    public ProductCategory toEntity(ProductCategoryDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ProductCategory productCategory = new ProductCategory();

        productCategory.setName( dto.getName() );
        productCategory.setDescription( dto.getDescription() );

        return productCategory;
    }
}
