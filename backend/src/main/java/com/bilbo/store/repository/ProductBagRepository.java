package com.bilbo.store.repository;

import com.bilbo.store.entites.ProductBag;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductBagRepository extends JpaRepository<ProductBag, UUID> {

    List<ProductBag> findByIsActiveTrue();

    /** All active bags belonging to a specific category. */
    @Query("""
            SELECT pb FROM ProductBag pb
            JOIN pb.categories c
            WHERE c.id = :categoryId AND pb.isActive = true
            """)
    List<ProductBag> findActiveByCategoryId(@Param("categoryId") UUID categoryId);

    /** All active bags that contain a specific product. */
    @Query("""
            SELECT pb FROM ProductBag pb
            JOIN pb.products p
            WHERE p.id = :productId AND pb.isActive = true
            """)
    List<ProductBag> findActiveByProductId(@Param("productId") UUID productId);

    List<ProductBag> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
}
