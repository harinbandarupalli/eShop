package com.bilbo.store.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CartDTO {
    private UUID id;
    private UUID userId;
    private List<CartItemDTO> items;
}
