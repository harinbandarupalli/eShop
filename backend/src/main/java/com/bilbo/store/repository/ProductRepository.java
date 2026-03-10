package com.bilbo.store.repository;

import com.bilbo.store.entites.Product;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByIsActiveTrue();

    List<Product> findByIsTrendingTrueAndIsActiveTrue();

    List<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
}
