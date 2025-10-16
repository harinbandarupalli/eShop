package com.bilbo.store.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CartItemDTO {
    private UUID id;
    private UUID productId;
    private Integer quantity;
}
