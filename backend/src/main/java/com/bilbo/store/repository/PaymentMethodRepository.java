package com.bilbo.store.repository;

import com.bilbo.store.entites.PaymentMethod;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {

    List<PaymentMethod> findByEmail(String email);

    Optional<PaymentMethod> findByEmailAndIsDefaultTrue(String email);
}
