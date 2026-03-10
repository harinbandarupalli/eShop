package com.bilbo.store.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class OrderDTO {
    private UUID id;
    private String email;
    private UUID cartId;
    private AddressDTO address;
    private PaymentMethodDTO paymentMethod;
    private ShippingTypeDTO shippingType;
    private String status;
    private BigDecimal totalAmount;
    private String notes;
    private List<OrderBagSnapshotDTO> bagSnapshots;
}
