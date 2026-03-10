package com.bilbo.store.mapper;

import com.bilbo.store.dto.OrderDTO;
import com.bilbo.store.entites.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { AddressMapper.class, PaymentMethodMapper.class, ShippingTypeMapper.class,
        OrderBagSnapshotMapper.class })
public interface OrderMapper {

    @Mapping(source = "cart.id", target = "cartId")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "paymentMethod", target = "paymentMethod")
    @Mapping(source = "shippingType", target = "shippingType")
    @Mapping(source = "bagSnapshots", target = "bagSnapshots")
    OrderDTO toDto(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "paymentMethod", ignore = true)
    @Mapping(target = "shippingType", ignore = true)
    @Mapping(target = "bagSnapshots", ignore = true)
    @Mapping(target = "createdTimestamp", ignore = true)
    @Mapping(target = "lastUpdatedTimestamp", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    Order toEntity(OrderDTO dto);
}
