package com.bilbo.store.mapper;

import com.bilbo.store.dto.OrderItemDTO;
import com.bilbo.store.entites.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface OrderItemMapper {

    @Mapping(source = "product.id", target = "productId")
    OrderItemDTO toDto(OrderItem orderItem);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    @Mapping(target = "createdTimestamp", ignore = true)
    @Mapping(target = "lastUpdatedTimestamp", ignore = true)
    OrderItem toEntity(OrderItemDTO orderItemDTO);
}
