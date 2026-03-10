package com.bilbo.store.mapper;

import com.bilbo.store.dto.OrderBagSnapshotDTO;
import com.bilbo.store.dto.OrderDTO;
import com.bilbo.store.entites.Cart;
import com.bilbo.store.entites.Order;
import com.bilbo.store.entites.OrderBagSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-09T16:56:19-0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Homebrew)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private PaymentMethodMapper paymentMethodMapper;
    @Autowired
    private ShippingTypeMapper shippingTypeMapper;
    @Autowired
    private OrderBagSnapshotMapper orderBagSnapshotMapper;

    @Override
    public OrderDTO toDto(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderDTO orderDTO = new OrderDTO();

        orderDTO.setCartId( orderCartId( order ) );
        orderDTO.setAddress( addressMapper.toDto( order.getAddress() ) );
        orderDTO.setPaymentMethod( paymentMethodMapper.toDto( order.getPaymentMethod() ) );
        orderDTO.setShippingType( shippingTypeMapper.toDto( order.getShippingType() ) );
        orderDTO.setBagSnapshots( orderBagSnapshotListToOrderBagSnapshotDTOList( order.getBagSnapshots() ) );
        orderDTO.setId( order.getId() );
        orderDTO.setEmail( order.getEmail() );
        orderDTO.setStatus( order.getStatus() );
        orderDTO.setTotalAmount( order.getTotalAmount() );
        orderDTO.setNotes( order.getNotes() );

        return orderDTO;
    }

    @Override
    public Order toEntity(OrderDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Order order = new Order();

        order.setEmail( dto.getEmail() );
        order.setStatus( dto.getStatus() );
        order.setTotalAmount( dto.getTotalAmount() );
        order.setNotes( dto.getNotes() );

        return order;
    }

    private UUID orderCartId(Order order) {
        if ( order == null ) {
            return null;
        }
        Cart cart = order.getCart();
        if ( cart == null ) {
            return null;
        }
        UUID id = cart.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected List<OrderBagSnapshotDTO> orderBagSnapshotListToOrderBagSnapshotDTOList(List<OrderBagSnapshot> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderBagSnapshotDTO> list1 = new ArrayList<OrderBagSnapshotDTO>( list.size() );
        for ( OrderBagSnapshot orderBagSnapshot : list ) {
            list1.add( orderBagSnapshotMapper.toDto( orderBagSnapshot ) );
        }

        return list1;
    }
}
