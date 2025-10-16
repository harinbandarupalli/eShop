package com.bilbo.store.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ProductImageDTO {
    private UUID id;
    private String imageUrl;
    private String altText;
}
