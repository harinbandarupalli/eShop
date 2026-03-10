package com.bilbo.store.mapper;

import com.bilbo.store.dto.ProductBagDTO;
import com.bilbo.store.dto.ProductCategoryDTO;
import com.bilbo.store.dto.ProductDTO;
import com.bilbo.store.entites.Product;
import com.bilbo.store.entites.ProductBag;
import com.bilbo.store.entites.ProductCategory;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-09T16:56:19-0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Homebrew)"
)
@Component
public class ProductBagMapperImpl implements ProductBagMapper {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Override
    public ProductBagDTO toDto(ProductBag bag) {
        if ( bag == null ) {
            return null;
        }

        ProductBagDTO productBagDTO = new ProductBagDTO();

        productBagDTO.setId( bag.getId() );
        productBagDTO.setName( bag.getName() );
        productBagDTO.setDescription( bag.getDescription() );
        productBagDTO.setDisplayPrice( bag.getDisplayPrice() );
        productBagDTO.setIsActive( bag.getIsActive() );
        productBagDTO.setProducts( productListToProductDTOList( bag.getProducts() ) );
        productBagDTO.setCategories( productCategoryListToProductCategoryDTOList( bag.getCategories() ) );

        return productBagDTO;
    }

    @Override
    public ProductBag toEntity(ProductBagDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ProductBag productBag = new ProductBag();

        productBag.setName( dto.getName() );
        productBag.setDescription( dto.getDescription() );
        productBag.setDisplayPrice( dto.getDisplayPrice() );
        productBag.setIsActive( dto.getIsActive() );

        return productBag;
    }

    protected List<ProductDTO> productListToProductDTOList(List<Product> list) {
        if ( list == null ) {
            return null;
        }

        List<ProductDTO> list1 = new ArrayList<ProductDTO>( list.size() );
        for ( Product product : list ) {
            list1.add( productMapper.toDto( product ) );
        }

        return list1;
    }

    protected List<ProductCategoryDTO> productCategoryListToProductCategoryDTOList(List<ProductCategory> list) {
        if ( list == null ) {
            return null;
        }

        List<ProductCategoryDTO> list1 = new ArrayList<ProductCategoryDTO>( list.size() );
        for ( ProductCategory productCategory : list ) {
            list1.add( productCategoryMapper.toDto( productCategory ) );
        }

        return list1;
    }
}
