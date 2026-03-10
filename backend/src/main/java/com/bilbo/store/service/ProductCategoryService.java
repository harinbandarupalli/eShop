package com.bilbo.store.service;

import com.bilbo.store.dto.ProductCategoryDTO;
import com.bilbo.store.entites.ProductCategory;
import com.bilbo.store.mapper.ProductCategoryMapper;
import com.bilbo.store.repository.ProductCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository categoryRepository;
    private final ProductCategoryMapper categoryMapper;

    public List<ProductCategoryDTO> getAllCategories() {
        log.info("Fetching all product categories");
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    public ProductCategoryDTO getCategoryById(UUID id) {
        log.info("Fetching product category with ID: {}", id);
        return categoryRepository.findById(id)
                .map(categoryMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    public ProductCategoryDTO createCategory(ProductCategoryDTO categoryDTO) {
        log.info("Creating new product category: {}", categoryDTO.getName());
        ProductCategory category = categoryMapper.toEntity(categoryDTO);
        ProductCategory saved = categoryRepository.save(category);
        return categoryMapper.toDto(saved);
    }

    public ProductCategoryDTO updateCategory(UUID id, ProductCategoryDTO categoryDTO) {
        log.info("Updating product category with ID: {}", id);
        ProductCategory existing = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        existing.setName(categoryDTO.getName());
        ProductCategory updated = categoryRepository.save(existing);
        return categoryMapper.toDto(updated);
    }

    public void deleteCategory(UUID id) {
        log.info("Deleting product category with ID: {}", id);
        categoryRepository.deleteById(id);
    }
}
