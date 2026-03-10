package com.bilbo.store.repository;

import com.bilbo.store.entites.Cart;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

    /** Find an active cart for a known email (auth or guest). */
    Optional<Cart> findByEmailAndStatus(String email, String status);

    /** Find an active cart by anonymous session ID (before email is collected). */
    Optional<Cart> findBySessionIdAndStatus(String sessionId, String status);

    /** All carts for an email (to support merge on login). */
    List<Cart> findByEmail(String email);
}
