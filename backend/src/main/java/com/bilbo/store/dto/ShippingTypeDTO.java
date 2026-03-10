package com.bilbo.store.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class ShippingTypeDTO {
    private UUID id;
    private String name;
    private BigDecimal cost;
    private String description;
    private Boolean isActive;
}
