package com.bilbo.store.mapper;

import com.bilbo.store.dto.OrderDTO;
import com.bilbo.store.entites.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "shippingAddress.id", target = "shippingAddressId")
    @Mapping(source = "billingAddress.id", target = "billingAddressId")
    @Mapping(source = "shippingType.id", target = "shippingTypeId")
    @Mapping(source = "paymentMethod.id", target = "paymentMethodId")
    OrderDTO toDto(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "shippingAddress", ignore = true)
    @Mapping(target = "billingAddress", ignore = true)
    @Mapping(target = "shippingType", ignore = true)
    @Mapping(target = "paymentMethod", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    Order toEntity(OrderDTO orderDTO);
}
