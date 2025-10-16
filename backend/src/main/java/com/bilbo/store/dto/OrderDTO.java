package com.bilbo.store.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class OrderDTO {
    private UUID id;
    private UUID userId;
    private BigDecimal totalAmount;
    private String status;
    private UUID shippingAddressId;
    private UUID billingAddressId;
    private UUID shippingTypeId;
    private UUID paymentMethodId;
    private List<OrderItemDTO> items;
}
