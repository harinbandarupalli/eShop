package com.bilbo.store.repository;

import com.bilbo.store.entites.OrderBagSnapshot;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderBagSnapshotRepository extends JpaRepository<OrderBagSnapshot, UUID> {

    List<OrderBagSnapshot> findByOrderId(UUID orderId);
}
