package com.bilbo.store.repository;

import com.bilbo.store.entites.Order;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    /** All orders for a given email (guest or auth user). */
    List<Order> findByEmailOrderByCreatedTimestampDesc(String email);

    List<Order> findByEmailAndStatus(String email, String status);

    /** Check if a cart has already been converted to an order. */
    boolean existsByCartId(UUID cartId);
}
