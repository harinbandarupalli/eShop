package com.bilbo.store.mapper;

import com.bilbo.store.dto.ShippingTypeDTO;
import com.bilbo.store.entites.ShippingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShippingTypeMapper {

    ShippingTypeDTO toDto(ShippingType shippingType);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdTimestamp", ignore = true)
    @Mapping(target = "lastUpdatedTimestamp", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    ShippingType toEntity(ShippingTypeDTO dto);
}
