package com.bilbo.store.mapper;

import com.bilbo.store.dto.PaymentMethodDTO;
import com.bilbo.store.entites.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {

    @Mapping(source = "billingAddress.id", target = "billingAddressId")
    PaymentMethodDTO toDto(PaymentMethod paymentMethod);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "token", ignore = true) // token is never exposed in DTO
    @Mapping(target = "billingAddress", ignore = true)
    @Mapping(target = "createdTimestamp", ignore = true)
    @Mapping(target = "lastUpdatedTimestamp", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    PaymentMethod toEntity(PaymentMethodDTO dto);
}
