package com.bilbo.store.repository;

import com.bilbo.store.entites.PaymentMethod;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {
}
