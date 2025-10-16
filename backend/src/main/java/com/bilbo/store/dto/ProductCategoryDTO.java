package com.bilbo.store.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ProductCategoryDTO {
    private UUID id;
    private String name;
    private String description;
}
