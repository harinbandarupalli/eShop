package com.bilbo.store.repository;

import com.bilbo.store.entites.UserRole;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
}
