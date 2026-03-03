package com.bilbo.store.repository;

import com.bilbo.store.entites.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findBySub(String sub);
}
