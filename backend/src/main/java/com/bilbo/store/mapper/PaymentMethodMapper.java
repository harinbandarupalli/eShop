package com.bilbo.store.mapper;

import com.bilbo.store.dto.PaymentMethodDTO;
import com.bilbo.store.entites.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface PaymentMethodMapper {

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "billingAddress.id", target = "billingAddressId")
    PaymentMethodDTO toDto(PaymentMethod paymentMethod);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "billingAddress", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    @Mapping(target = "createdTimestamp", ignore = true)
    @Mapping(target = "lastUpdatedTimestamp", ignore = true)
    PaymentMethod toEntity(PaymentMethodDTO paymentMethodDTO);
}
