package com.bilbo.store.repository;

import com.bilbo.store.entites.Wishlist;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {
}
