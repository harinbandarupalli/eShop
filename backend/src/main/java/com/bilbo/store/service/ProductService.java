package com.bilbo.store.service;

import com.bilbo.store.dto.ProductDTO;
import com.bilbo.store.entites.Product;
import com.bilbo.store.entites.User;
import com.bilbo.store.mapper.ProductMapper;
import com.bilbo.store.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final UserService userService;

    public List<ProductDTO> getAllProducts() {
        log.info("Fetching all products");
        List<Product> products = productRepository.findAll();
        log.info("Found {} products", products.size());
        return products.stream()
                .map(productMapper::toDto)
                .toList();
    }

    public ProductDTO createProduct(ProductDTO newProduct, String createdBy) {
        log.info("Creating new product: {}", newProduct.name());
        User user = userService.processUser(createdBy);
        Product product = productMapper.toEntity(newProduct);
        product.setCreatedBy(user.getSub());
        product.setCreatedTimestamp(OffsetDateTime.now());

        Product savedProduct = productRepository.save(product);
        log.info("Successfully created product with ID: {}", savedProduct.getId());
        return productMapper.toDto(savedProduct);
    }

    public ProductDTO getProductById(UUID id) {
        log.info("Fetching product with ID: {}", id);
        return productRepository.findById(id)
                .map(product -> {
                    log.info("Found product: {}", product.getName());
                    return productMapper.toDto(product);
                })
                .orElseThrow(() -> {
                    log.warn("Product with ID: {} not found", id);
                    return new EntityNotFoundException("Product not found");
                });
    }

    public ProductDTO updateProduct(UUID id, ProductDTO productDTO, String updatedBy) {
        log.info("Updating product with ID: {}", id);
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product with ID: {} not found for update", id);
                    return new EntityNotFoundException("Product not found");
                });
        User user = userService.processUser(updatedBy);

        existingProduct.setName(productDTO.name());
        existingProduct.setDescription(productDTO.description());
        existingProduct.setPrice(productDTO.price());
        existingProduct.setStockQuantity(productDTO.stockQuantity());
        existingProduct.setIsTrending(productDTO.isTrending());
        existingProduct.setLastUpdatedTimestamp(OffsetDateTime.now());
        existingProduct.setLastUpdatedBy(user.getSub());

        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Successfully updated product with ID: {}", updatedProduct.getId());
        return productMapper.toDto(updatedProduct);
    }

    public ProductDTO patchProduct(UUID id, ProductDTO productDTO, String updatedBy) {
        log.info("Patching product with ID: {}", id);
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product with ID: {} not found for patch", id);
                    return new EntityNotFoundException("Product not found");
                });
        User user = userService.processUser(updatedBy);

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
        existingProduct.setLastUpdatedBy(user.getSub());

        Product patchedProduct = productRepository.save(existingProduct);
        log.info("Successfully patched product with ID: {}", patchedProduct.getId());
        return productMapper.toDto(patchedProduct);
    }

    public void deleteProduct(UUID id) {
        log.info("Deleting product with ID: {}", id);
        productRepository.deleteById(id);
        log.info("Successfully deleted product with ID: {}", id);
    }
}
