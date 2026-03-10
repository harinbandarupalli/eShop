package com.bilbo.store.mapper;

import com.bilbo.store.dto.ProductDTO;
import com.bilbo.store.dto.ProductImageDTO;
import com.bilbo.store.entites.Product;
import com.bilbo.store.entites.ProductImage;
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
public class ProductMapperImpl implements ProductMapper {

    @Autowired
    private ProductImageMapper productImageMapper;

    @Override
    public ProductDTO toDto(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductDTO productDTO = new ProductDTO();

        productDTO.setId( product.getId() );
        productDTO.setName( product.getName() );
        productDTO.setDescription( product.getDescription() );
        productDTO.setPrice( product.getPrice() );
        productDTO.setStockQuantity( product.getStockQuantity() );
        productDTO.setIsActive( product.getIsActive() );
        productDTO.setIsTrending( product.getIsTrending() );
        productDTO.setImages( productImageListToProductImageDTOList( product.getImages() ) );

        return productDTO;
    }

    @Override
    public Product toEntity(ProductDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Product product = new Product();

        product.setName( dto.getName() );
        product.setDescription( dto.getDescription() );
        product.setPrice( dto.getPrice() );
        product.setStockQuantity( dto.getStockQuantity() );
        product.setIsActive( dto.getIsActive() );
        product.setIsTrending( dto.getIsTrending() );

        return product;
    }

    protected List<ProductImageDTO> productImageListToProductImageDTOList(List<ProductImage> list) {
        if ( list == null ) {
            return null;
        }

        List<ProductImageDTO> list1 = new ArrayList<ProductImageDTO>( list.size() );
        for ( ProductImage productImage : list ) {
            list1.add( productImageMapper.toDto( productImage ) );
        }

        return list1;
    }
}
