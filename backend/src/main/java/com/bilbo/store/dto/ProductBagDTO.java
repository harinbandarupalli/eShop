package com.bilbo.store.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class ProductBagDTO {
    private UUID id;
    private String name;
    private String description;
    /**
     * Admin-set price. Null = application should derive from sum of member product
     * prices.
     */
    private BigDecimal displayPrice;
    private Boolean isActive;
    private List<ProductDTO> products;
    private List<ProductCategoryDTO> categories;
}
