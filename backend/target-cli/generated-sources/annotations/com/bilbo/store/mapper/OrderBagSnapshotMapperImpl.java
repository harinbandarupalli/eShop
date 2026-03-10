package com.bilbo.store.mapper;

import com.bilbo.store.dto.OrderBagSnapshotDTO;
import com.bilbo.store.entites.Order;
import com.bilbo.store.entites.OrderBagSnapshot;
import com.bilbo.store.entites.ProductBag;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-09T16:56:19-0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Homebrew)"
)
@Component
public class OrderBagSnapshotMapperImpl implements OrderBagSnapshotMapper {

    @Override
    public OrderBagSnapshotDTO toDto(OrderBagSnapshot snapshot) {
        if ( snapshot == null ) {
            return null;
        }

        OrderBagSnapshotDTO orderBagSnapshotDTO = new OrderBagSnapshotDTO();

        orderBagSnapshotDTO.setOrderId( snapshotOrderId( snapshot ) );
        orderBagSnapshotDTO.setBagId( snapshotBagId( snapshot ) );
        orderBagSnapshotDTO.setId( snapshot.getId() );
        orderBagSnapshotDTO.setBagName( snapshot.getBagName() );
        orderBagSnapshotDTO.setQuantity( snapshot.getQuantity() );
        orderBagSnapshotDTO.setPriceAtPurchase( snapshot.getPriceAtPurchase() );

        return orderBagSnapshotDTO;
    }

    @Override
    public OrderBagSnapshot toEntity(OrderBagSnapshotDTO dto) {
        if ( dto == null ) {
            return null;
        }

        OrderBagSnapshot orderBagSnapshot = new OrderBagSnapshot();

        orderBagSnapshot.setBagName( dto.getBagName() );
        orderBagSnapshot.setQuantity( dto.getQuantity() );
        orderBagSnapshot.setPriceAtPurchase( dto.getPriceAtPurchase() );

        return orderBagSnapshot;
    }

    private UUID snapshotOrderId(OrderBagSnapshot orderBagSnapshot) {
        if ( orderBagSnapshot == null ) {
            return null;
        }
        Order order = orderBagSnapshot.getOrder();
        if ( order == null ) {
            return null;
        }
        UUID id = order.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID snapshotBagId(OrderBagSnapshot orderBagSnapshot) {
        if ( orderBagSnapshot == null ) {
            return null;
        }
        ProductBag bag = orderBagSnapshot.getBag();
        if ( bag == null ) {
            return null;
        }
        UUID id = bag.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
