package com.bilbo.store.service;

import com.bilbo.store.dto.ProductDTO;
import com.bilbo.store.entites.Product;
import com.bilbo.store.entites.User;
import com.bilbo.store.mapper.ProductMapper;
import com.bilbo.store.repository.ProductRepository;
import com.bilbo.store.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final UserRepository userRepository;

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    public ProductDTO createProduct(ProductDTO newProduct, String createdBy) {
        User user = userRepository.findById(UUID.fromString(createdBy))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Product product = productMapper.toEntity(newProduct);
        product.setCreatedBy(user);
        product.setCreatedTimestamp(OffsetDateTime.now());

        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    public ProductDTO getProductById(UUID id) {
        return productRepository.findById(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    public ProductDTO updateProduct(UUID id, ProductDTO productDTO, String updatedBy) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        User user = userRepository.findById(UUID.fromString(updatedBy))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        existingProduct.setName(productDTO.name());
        existingProduct.setDescription(productDTO.description());
        existingProduct.setPrice(productDTO.price());
        existingProduct.setStockQuantity(productDTO.stockQuantity());
        existingProduct.setIsTrending(productDTO.isTrending());
        existingProduct.setLastUpdatedTimestamp(OffsetDateTime.now());
        existingProduct.setLastUpdatedBy(user);

        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toDto(updatedProduct);
    }

    public ProductDTO patchProduct(UUID id, ProductDTO productDTO, String updatedBy) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        User user = userRepository.findById(UUID.fromString(updatedBy))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (productDTO.name() != null) {
            existingProduct.setName(productDTO.name());
        }
        if (productDTO.description() != null) {
            existingProduct.setDescription(productDTO.description());
        }
        if (productDTO.price() != null) {
            existingProduct.setPrice(productDTO.price());
        }
        if (productDTO.stockQuantity() != null) {
            existingProduct.setStockQuantity(productDTO.stockQuantity());
        }
        if (productDTO.isTrending() != null) {
            existingProduct.setIsTrending(productDTO.isTrending());
        }
        existingProduct.setLastUpdatedTimestamp(OffsetDateTime.now());
        existingProduct.setLastUpdatedBy(user);

        Product patchedProduct = productRepository.save(existingProduct);
        return productMapper.toDto(patchedProduct);
    }

    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }
}
