package com.bilbo.store.mapper;

import com.bilbo.store.dto.ShippingTypeDTO;
import com.bilbo.store.entites.ShippingType;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-09T16:56:19-0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Homebrew)"
)
@Component
public class ShippingTypeMapperImpl implements ShippingTypeMapper {

    @Override
    public ShippingTypeDTO toDto(ShippingType shippingType) {
        if ( shippingType == null ) {
            return null;
        }

        ShippingTypeDTO shippingTypeDTO = new ShippingTypeDTO();

        shippingTypeDTO.setId( shippingType.getId() );
        shippingTypeDTO.setName( shippingType.getName() );
        shippingTypeDTO.setCost( shippingType.getCost() );
        shippingTypeDTO.setDescription( shippingType.getDescription() );
        shippingTypeDTO.setIsActive( shippingType.getIsActive() );

        return shippingTypeDTO;
    }

    @Override
    public ShippingType toEntity(ShippingTypeDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ShippingType shippingType = new ShippingType();

        shippingType.setName( dto.getName() );
        shippingType.setCost( dto.getCost() );
        shippingType.setDescription( dto.getDescription() );
        shippingType.setIsActive( dto.getIsActive() );

        return shippingType;
    }
}
