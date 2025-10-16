package com.bilbo.store.mapper;

import com.bilbo.store.dto.AddressDTO;
import com.bilbo.store.entites.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AddressMapper {

  AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

  AddressDTO toDto(Address address);

  @Mapping(target = "id", ignore = true)
  Address toEntity(AddressDTO addressDTO);
}
