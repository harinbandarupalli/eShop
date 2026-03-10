package com.bilbo.store.mapper;

import com.bilbo.store.dto.ProductImageDTO;
import com.bilbo.store.entites.Product;
import com.bilbo.store.entites.ProductImage;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-09T16:56:19-0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Homebrew)"
)
@Component
public class ProductImageMapperImpl implements ProductImageMapper {

    @Override
    public ProductImageDTO toDto(ProductImage image) {
        if ( image == null ) {
            return null;
        }

        ProductImageDTO productImageDTO = new ProductImageDTO();

        productImageDTO.setProductId( imageProductId( image ) );
        productImageDTO.setId( image.getId() );
        productImageDTO.setImageUrl( image.getImageUrl() );
        productImageDTO.setAltText( image.getAltText() );
        productImageDTO.setDisplayOrder( image.getDisplayOrder() );

        return productImageDTO;
    }

    @Override
    public ProductImage toEntity(ProductImageDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ProductImage productImage = new ProductImage();

        productImage.setImageUrl( dto.getImageUrl() );
        productImage.setAltText( dto.getAltText() );
        productImage.setDisplayOrder( dto.getDisplayOrder() );

        return productImage;
    }

    private UUID imageProductId(ProductImage productImage) {
        if ( productImage == null ) {
            return null;
        }
        Product product = productImage.getProduct();
        if ( product == null ) {
            return null;
        }
        UUID id = product.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
