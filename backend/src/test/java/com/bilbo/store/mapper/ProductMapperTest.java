package com.bilbo.store.mapper;

import com.bilbo.store.dto.ProductDTO;
import com.bilbo.store.entites.Product;
import com.bilbo.store.entites.ProductCategory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductMapperTest {

    private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    private ProductDTO createProductDTO() {
        return new ProductDTO(UUID.randomUUID(), "Test Product", "Test Description", new BigDecimal("10.00"), 100, UUID.randomUUID(), false);
    }

    @Test
    void toDto() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("10.00"));
        product.setStockQuantity(100);
        product.setIsTrending(false);

        ProductCategory category = new ProductCategory();
        category.setId(UUID.randomUUID());
        product.setCategory(category);

        ProductDTO productDTO = productMapper.toDto(product);

        assertEquals(product.getId(), productDTO.id());
        assertEquals(product.getName(), productDTO.name());
        assertEquals(product.getDescription(), productDTO.description());
        assertEquals(product.getPrice(), productDTO.price());
        assertEquals(product.getStockQuantity(), productDTO.stockQuantity());
        assertEquals(product.getCategory().getId(), productDTO.categoryId());
        assertEquals(product.getIsTrending(), productDTO.isTrending());
    }

    @Test
    void toEntity() {
        ProductDTO productDTO = createProductDTO();

        Product product = productMapper.toEntity(productDTO);

        assertEquals(productDTO.name(), product.getName());
        assertEquals(productDTO.description(), product.getDescription());
        assertEquals(productDTO.price(), product.getPrice());
        assertEquals(productDTO.stockQuantity(), product.getStockQuantity());
        assertEquals(productDTO.isTrending(), product.getIsTrending());
    }
}
