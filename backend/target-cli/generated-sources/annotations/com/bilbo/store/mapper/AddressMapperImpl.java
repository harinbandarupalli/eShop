package com.bilbo.store.mapper;

import com.bilbo.store.dto.AddressDTO;
import com.bilbo.store.entites.Address;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-09T16:56:19-0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Homebrew)"
)
@Component
public class AddressMapperImpl implements AddressMapper {

    @Override
    public AddressDTO toDto(Address address) {
        if ( address == null ) {
            return null;
        }

        AddressDTO addressDTO = new AddressDTO();

        addressDTO.setId( address.getId() );
        addressDTO.setEmail( address.getEmail() );
        addressDTO.setAddressLine1( address.getAddressLine1() );
        addressDTO.setAddressLine2( address.getAddressLine2() );
        addressDTO.setCity( address.getCity() );
        addressDTO.setStateProvinceRegion( address.getStateProvinceRegion() );
        addressDTO.setPostalCode( address.getPostalCode() );
        addressDTO.setCountry( address.getCountry() );
        addressDTO.setIsDefault( address.getIsDefault() );

        return addressDTO;
    }

    @Override
    public Address toEntity(AddressDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Address address = new Address();

        address.setEmail( dto.getEmail() );
        address.setAddressLine1( dto.getAddressLine1() );
        address.setAddressLine2( dto.getAddressLine2() );
        address.setCity( dto.getCity() );
        address.setStateProvinceRegion( dto.getStateProvinceRegion() );
        address.setPostalCode( dto.getPostalCode() );
        address.setCountry( dto.getCountry() );
        address.setIsDefault( dto.getIsDefault() );

        return address;
    }
}
