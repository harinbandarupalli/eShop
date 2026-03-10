package com.bilbo.store.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class ProductImageDTO {
    private UUID id;
    private UUID productId;
    private String imageUrl;
    private String altText;
    private Integer displayOrder;
}
