package com.bilbo.store.dto;

import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class WishlistDTO {
    private UUID id;
    private String userId;
    private List<WishlistItemDTO> items;
}
