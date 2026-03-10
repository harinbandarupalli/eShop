package com.bilbo.store.mapper;

import com.bilbo.store.dto.OrderBagSnapshotDTO;
import com.bilbo.store.entites.OrderBagSnapshot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderBagSnapshotMapper {

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "bag.id", target = "bagId")
    OrderBagSnapshotDTO toDto(OrderBagSnapshot snapshot);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "bag", ignore = true)
    @Mapping(target = "createdTimestamp", ignore = true)
    OrderBagSnapshot toEntity(OrderBagSnapshotDTO dto);
}
