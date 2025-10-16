package com.bilbo.store.mapper;

import com.bilbo.store.dto.PaymentMethodDTO;
import com.bilbo.store.entites.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {

    PaymentMethodMapper INSTANCE = Mappers.getMapper(PaymentMethodMapper.class);

    @Mapping(source = "billingAddress.id", target = "billingAddressId")
    PaymentMethodDTO toDto(PaymentMethod paymentMethod);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "billingAddress", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    PaymentMethod toEntity(PaymentMethodDTO paymentMethodDTO);
}
