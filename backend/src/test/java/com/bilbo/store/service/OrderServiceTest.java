package com.bilbo.store.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.bilbo.store.dto.OrderDTO;
import com.bilbo.store.entites.*;
import com.bilbo.store.mapper.OrderMapper;
import com.bilbo.store.repository.*;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderBagSnapshotRepository snapshotRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private ShippingTypeRepository shippingTypeRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService service;

    private final String testEmail = "test@example.com";

    @Test
    void testGetOrdersByEmail() {
        Order entity = new Order();
        entity.setId(UUID.randomUUID());

        OrderDTO dto = new OrderDTO();
        dto.setId(entity.getId());

        when(orderRepository.findByEmailOrderByCreatedTimestampDesc(testEmail)).thenReturn(List.of(entity));
        when(orderMapper.toDto(any(Order.class))).thenReturn(dto);

        List<OrderDTO> result = service.getOrdersByEmail(testEmail);

        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findByEmailOrderByCreatedTimestampDesc(testEmail);
    }

    @Test
    void testGetOrderById_Success() {
        UUID id = UUID.randomUUID();
        Order entity = new Order();
        entity.setId(id);
        entity.setEmail(testEmail);

        OrderDTO dto = new OrderDTO();
        dto.setId(id);

        when(orderRepository.findById(id)).thenReturn(Optional.of(entity));
        when(orderMapper.toDto(any(Order.class))).thenReturn(dto);

        OrderDTO result = service.getOrderById(testEmail, id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void testGetOrderById_OwnershipMismatch() {
        UUID id = UUID.randomUUID();
        Order entity = new Order();
        entity.setId(id);
        entity.setEmail("other@example.com");

        when(orderRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(IllegalArgumentException.class, () -> service.getOrderById(testEmail, id));
    }

    @Test
    void testCheckout_SuccessWithEmail() {
        UUID addressId = UUID.randomUUID();
        UUID paymentMethodId = UUID.randomUUID();
        UUID shippingTypeId = UUID.randomUUID();

        Cart cart = new Cart();
        cart.setId(UUID.randomUUID());
        CartItem item = new CartItem();
        ProductBag bag = new ProductBag();
        bag.setId(UUID.randomUUID());
        bag.setDisplayPrice(BigDecimal.TEN);
        item.setBag(bag);
        item.setQuantity(2); // 2 * 10 = 20
        cart.setItems(List.of(item));

        Address address = new Address();
        address.setId(addressId);
        address.setEmail(testEmail);

        PaymentMethod payment = new PaymentMethod();
        payment.setId(paymentMethodId);
        payment.setEmail(testEmail);

        ShippingType shipping = new ShippingType();
        shipping.setId(shippingTypeId);
        shipping.setCost(BigDecimal.valueOf(5.0)); // 20 + 5 = 25

        Order savedOrder = new Order();
        savedOrder.setId(UUID.randomUUID());

        OrderDTO dto = new OrderDTO();
        dto.setId(savedOrder.getId());

        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.of(cart));
        when(orderRepository.existsByCartId(cart.getId())).thenReturn(false);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));
        when(paymentMethodRepository.findById(paymentMethodId)).thenReturn(Optional.of(payment));
        when(shippingTypeRepository.findById(shippingTypeId)).thenReturn(Optional.of(shipping));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toDto(savedOrder)).thenReturn(dto);

        OrderDTO result = service.checkout(testEmail, null, addressId, paymentMethodId, shippingTypeId);

        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(snapshotRepository, times(1)).save(any(OrderBagSnapshot.class)); // 1 item
        assertEquals("CHECKED_OUT", cart.getStatus());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testCheckout_SuccessWithSessionId() {
        String sessionId = "sess123";
        UUID addressId = UUID.randomUUID();
        UUID paymentMethodId = UUID.randomUUID();
        UUID shippingTypeId = UUID.randomUUID();

        Cart cart = new Cart();
        cart.setId(UUID.randomUUID());
        CartItem item = new CartItem();
        ProductBag bag = new ProductBag();
        bag.setId(UUID.randomUUID());
        item.setBag(bag); // no display price -> 0
        item.setQuantity(1);
        cart.setItems(List.of(item));

        Address address = new Address();
        PaymentMethod payment = new PaymentMethod();
        ShippingType shipping = new ShippingType();
        shipping.setCost(BigDecimal.ZERO);

        Order savedOrder = new Order();
        savedOrder.setId(UUID.randomUUID());

        when(cartRepository.findBySessionIdAndStatus(sessionId, "ACTIVE")).thenReturn(Optional.of(cart));
        when(orderRepository.existsByCartId(cart.getId())).thenReturn(false);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));
        when(paymentMethodRepository.findById(paymentMethodId)).thenReturn(Optional.of(payment));
        when(shippingTypeRepository.findById(shippingTypeId)).thenReturn(Optional.of(shipping));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toDto(savedOrder)).thenReturn(new OrderDTO());

        service.checkout(null, sessionId, addressId, paymentMethodId, shippingTypeId);

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCheckout_NoActiveCart() {
        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> service.checkout(testEmail, null, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    void testCheckout_EmptyCart() {
        Cart cart = new Cart();
        cart.setItems(List.of()); // empty
        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.of(cart));

        assertThrows(IllegalStateException.class,
                () -> service.checkout(testEmail, null, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    void testCheckout_CartAlreadyProcessed() {
        Cart cart = new Cart();
        cart.setId(UUID.randomUUID());
        CartItem item = new CartItem();
        cart.setItems(List.of(item));

        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.of(cart));
        when(orderRepository.existsByCartId(cart.getId())).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> service.checkout(testEmail, null, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    void testCheckout_AddressOwnershipMismatch() {
        Cart cart = new Cart();
        cart.setId(UUID.randomUUID());
        cart.setItems(List.of(new CartItem()));

        UUID addressId = UUID.randomUUID();
        Address address = new Address();
        address.setEmail("other@example.com");

        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.of(cart));
        when(orderRepository.existsByCartId(cart.getId())).thenReturn(false);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        // Mock payment and shipping so it doesn't fail on them before validation
        when(paymentMethodRepository.findById(any())).thenReturn(Optional.of(new PaymentMethod()));
        when(shippingTypeRepository.findById(any())).thenReturn(Optional.of(new ShippingType()));

        assertThrows(IllegalArgumentException.class,
                () -> service.checkout(testEmail, null, addressId, UUID.randomUUID(), UUID.randomUUID()));
    }
}
