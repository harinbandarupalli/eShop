package com.bilbo.store.service;

import com.bilbo.store.dto.ProductDTO;
import com.bilbo.store.entites.Product;
import com.bilbo.store.entites.User;
import com.bilbo.store.mapper.ProductMapper;
import com.bilbo.store.repository.ProductRepository;
import com.bilbo.store.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProductService productService;

    private ProductDTO createProductDTO() {
        return new ProductDTO(UUID.randomUUID(), "Test Product", "Test Description", new BigDecimal("10.00"), 100, UUID.randomUUID(), false);
    }

    @Test
    void getAllProducts() {
        when(productRepository.findAll()).thenReturn(Collections.singletonList(new Product()));
        when(productMapper.toDto(any(Product.class))).thenReturn(createProductDTO());

        var result = productService.getAllProducts();

        assertFalse(result.isEmpty());
    }

    @Test
    void createProduct() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(productMapper.toEntity(any(ProductDTO.class))).thenReturn(new Product());
        when(productRepository.save(any(Product.class))).thenReturn(new Product());
        when(productMapper.toDto(any(Product.class))).thenReturn(createProductDTO());

        var result = productService.createProduct(createProductDTO(), userId.toString());

        assertNotNull(result);
    }

    @Test
    void getProductById() {
        UUID productId = UUID.randomUUID();
        when(productRepository.findById(productId)).thenReturn(Optional.of(new Product()));
        when(productMapper.toDto(any(Product.class))).thenReturn(createProductDTO());

        var result = productService.getProductById(productId);

        assertNotNull(result);
    }

    @Test
    void getProductById_notFound() {
        UUID productId = UUID.randomUUID();
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.getProductById(productId));
    }

    @Test
    void updateProduct() {
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(productRepository.findById(productId)).thenReturn(Optional.of(new Product()));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(productRepository.save(any(Product.class))).thenReturn(new Product());
        when(productMapper.toDto(any(Product.class))).thenReturn(createProductDTO());

        var result = productService.updateProduct(productId, createProductDTO(), userId.toString());

        assertNotNull(result);
    }

    @Test
    void patchProduct() {
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(productRepository.findById(productId)).thenReturn(Optional.of(new Product()));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(productRepository.save(any(Product.class))).thenReturn(new Product());
        when(productMapper.toDto(any(Product.class))).thenReturn(createProductDTO());

        var result = productService.patchProduct(productId, createProductDTO(), userId.toString());

        assertNotNull(result);
    }

    @Test
    void deleteProduct() {
        UUID productId = UUID.randomUUID();
        productService.deleteProduct(productId);
    }
}
