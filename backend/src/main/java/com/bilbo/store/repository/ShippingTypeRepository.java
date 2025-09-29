package com.bilbo.store.repository;

import com.bilbo.store.entites.ShippingType;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingTypeRepository extends JpaRepository<ShippingType, UUID> {
}
