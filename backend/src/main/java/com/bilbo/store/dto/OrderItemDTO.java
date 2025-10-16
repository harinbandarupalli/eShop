package com.bilbo.store.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderItemDTO {
    private UUID id;
    private UUID productId;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
}
