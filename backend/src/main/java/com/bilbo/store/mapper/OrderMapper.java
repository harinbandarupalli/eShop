package com.bilbo.store.mapper;

import com.bilbo.store.dto.OrderDTO;
import com.bilbo.store.entites.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class, AddressMapper.class,
    PaymentMethodMapper.class})
public interface OrderMapper {

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "shippingAddress.id", target = "shippingAddressId")
    @Mapping(source = "billingAddress.id", target = "billingAddressId")
    @Mapping(source = "shippingType.id", target = "shippingTypeId")
    @Mapping(source = "paymentMethod.id", target = "paymentMethodId")
    OrderDTO toDto(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "shippingAddress", ignore = true)
    @Mapping(target = "billingAddress", ignore = true)
    @Mapping(target = "shippingType", ignore = true)
    @Mapping(target = "paymentMethod", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    @Mapping(target = "createdTimestamp", ignore = true)
    @Mapping(target = "lastUpdatedTimestamp", ignore = true)
    Order toEntity(OrderDTO orderDTO);
}
