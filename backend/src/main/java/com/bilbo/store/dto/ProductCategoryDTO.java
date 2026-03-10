package com.bilbo.store.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class ProductCategoryDTO {
    private UUID id;
    private String name;
    private String description;
}
