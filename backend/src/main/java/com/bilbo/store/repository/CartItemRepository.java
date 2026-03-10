package com.bilbo.store.repository;

import com.bilbo.store.entites.CartItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    List<CartItem> findByCartId(UUID cartId);

    /** Check if a specific bag is already in the cart. */
    Optional<CartItem> findByCartIdAndBagId(UUID cartId, UUID bagId);

    void deleteByCartIdAndBagId(UUID cartId, UUID bagId);
}
