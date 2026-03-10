package com.bilbo.store.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class CartItemDTO {
    private UUID id;
    private UUID cartId;
    private ProductBagDTO bag;
    private Integer quantity;
}
