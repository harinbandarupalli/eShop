package com.bilbo.store.repository;

import com.bilbo.store.entites.Address;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    List<Address> findByEmail(String email);

    Optional<Address> findByEmailAndIsDefaultTrue(String email);
}
