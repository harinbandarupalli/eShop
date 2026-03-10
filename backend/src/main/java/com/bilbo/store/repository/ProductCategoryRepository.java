package com.bilbo.store.repository;

import com.bilbo.store.entites.ProductCategory;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {

    Optional<ProductCategory> findByName(String name);

    boolean existsByName(String name);
}
