package com.bilbo.store.repository;

import com.bilbo.store.entites.WishlistItem;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, UUID> {
}
