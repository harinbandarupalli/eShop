package com.bilbo.store.mapper;

import com.bilbo.store.dto.RoleDTO;
import com.bilbo.store.entites.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleDTO toDto(Role role);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    Role toEntity(RoleDTO roleDTO);
}
