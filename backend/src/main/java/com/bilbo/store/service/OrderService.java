package com.bilbo.store.service;

import com.bilbo.store.dto.OrderDTO;
import com.bilbo.store.entites.*;
import com.bilbo.store.mapper.OrderMapper;
import com.bilbo.store.repository.*;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderBagSnapshotRepository snapshotRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ShippingTypeRepository shippingTypeRepository;
    private final OrderMapper orderMapper;

    public List<OrderDTO> getOrdersByEmail(String email) {
        log.info("Fetching orders for email: {}", email);
        return orderRepository.findByEmailOrderByCreatedTimestampDesc(email).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    public OrderDTO getOrderById(String email, UUID id) {
        log.info("Fetching order {} for email: {}", id, email);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (!order.getEmail().equals(email)) {
            throw new IllegalArgumentException("Order does not belong to the provided email");
        }

        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderDTO checkout(String email, String sessionId, UUID addressId, UUID paymentMethodId,
            UUID shippingTypeId) {
        log.info("Checking out cart for email: {}", email);

        Optional<Cart> cartOpt;
        if (email != null && !email.isBlank()) {
            cartOpt = cartRepository.findByEmailAndStatus(email, "ACTIVE");
        } else {
            cartOpt = cartRepository.findBySessionIdAndStatus(sessionId, "ACTIVE");
        }

        Cart cart = cartOpt.orElseThrow(() -> new IllegalStateException("No active cart found to checkout"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout an empty cart");
        }

        // We verify that the cart hasn't already been converted to an order
        if (orderRepository.existsByCartId(cart.getId())) {
            throw new IllegalStateException("This cart has already been processed into an order");
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Shipping address not found"));

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new EntityNotFoundException("Payment method not found"));

        ShippingType shippingType = shippingTypeRepository.findById(shippingTypeId)
                .orElseThrow(() -> new EntityNotFoundException("Shipping type not found"));

        // If user is authenticated, verify ownership
        if (email != null && !email.isBlank()) {
            if (!address.getEmail().equals(email)) {
                throw new IllegalArgumentException("Address ownership mismatch");
            }
            if (!paymentMethod.getEmail().equals(email)) {
                throw new IllegalArgumentException("Payment method ownership mismatch");
            }
        }

        Order order = new Order();
        order.setEmail(email != null ? email : sessionId);
        order.setCart(cart);
        order.setAddress(address);
        order.setPaymentMethod(paymentMethod);
        order.setShippingType(shippingType);
        order.setStatus("PENDING");
        order.setCreatedBy(order.getEmail());
        order.setCreatedTimestamp(OffsetDateTime.now());

        // Calculate total and create snapshots
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderBagSnapshot> snapshots = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            ProductBag bag = item.getBag();
            BigDecimal price = bag.getDisplayPrice();
            if (price == null) {
                // Derived price strategy (sum of products) or throw error if your business
                // logic requires explicit displayPrice
                // For safety, defaulting to 0 if not set, but ideally it should be set or
                // calculated.
                price = BigDecimal.ZERO;
            }

            OrderBagSnapshot snapshot = new OrderBagSnapshot();
            snapshot.setOrder(order);
            snapshot.setBag(bag);
            snapshot.setBagName(bag.getName());
            snapshot.setQuantity(item.getQuantity());
            snapshot.setPriceAtPurchase(price);
            snapshots.add(snapshot);

            totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // Add shipping cost
        totalAmount = totalAmount.add(shippingType.getCost());
        order.setTotalAmount(totalAmount);

        // Save order first to generate ID for snapshots
        Order savedOrder = orderRepository.save(order);

        // Save snapshots
        for (OrderBagSnapshot snapshot : snapshots) {
            snapshot.setOrder(savedOrder);
            snapshotRepository.save(snapshot);
        }

        savedOrder.setBagSnapshots(snapshots);

        // Update cart status
        cart.setStatus("CHECKED_OUT");
        cart.setLastUpdatedTimestamp(OffsetDateTime.now());
        cart.setLastUpdatedBy(order.getEmail());
        cartRepository.save(cart);

        log.info("Successfully created order with ID: {}", savedOrder.getId());
        return orderMapper.toDto(savedOrder);
    }
}
