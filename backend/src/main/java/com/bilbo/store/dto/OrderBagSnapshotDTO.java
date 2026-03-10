package com.bilbo.store.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class OrderBagSnapshotDTO {
    private UUID id;
    private UUID orderId;
    private UUID bagId;
    private String bagName;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
}
