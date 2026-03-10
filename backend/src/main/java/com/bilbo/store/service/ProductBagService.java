package com.bilbo.store.service;

import com.bilbo.store.dto.ProductBagDTO;
import com.bilbo.store.entites.Product;
import com.bilbo.store.entites.ProductBag;
import com.bilbo.store.entites.ProductCategory;
import com.bilbo.store.entites.User;
import com.bilbo.store.mapper.ProductBagMapper;
import com.bilbo.store.repository.ProductBagRepository;
import com.bilbo.store.repository.ProductCategoryRepository;
import com.bilbo.store.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductBagService {

    private final ProductBagRepository productBagRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductBagMapper productBagMapper;
    private final UserService userService;

    public List<ProductBagDTO> getActiveProductBags() {
        log.info("Fetching all active product bags");
        List<ProductBag> bags = productBagRepository.findByIsActiveTrue();
        log.info("Found {} active bags", bags.size());
        return bags.stream()
                .map(productBagMapper::toDto)
                .toList();
    }

    public List<ProductBagDTO> getActiveProductBagsByCategory(UUID categoryId) {
        log.info("Fetching active product bags for category: {}", categoryId);
        List<ProductBag> bags = productBagRepository.findActiveByCategoryId(categoryId);
        return bags.stream()
                .map(productBagMapper::toDto)
                .toList();
    }

    public ProductBagDTO getProductBagById(UUID id) {
        log.info("Fetching product bag with ID: {}", id);
        return productBagRepository.findById(id)
                .filter(b -> Boolean.TRUE.equals(b.getIsActive()))
                .map(productBagMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("ProductBag not found"));
    }

    @Transactional
    public ProductBagDTO createProductBag(ProductBagDTO productBagDTO, String createdBy) {
        log.info("Creating new product bag: {}", productBagDTO.getName());
        User user = userService.processUser(createdBy);

        ProductBag bag = productBagMapper.toEntity(productBagDTO);
        bag.setCreatedBy(user.getSub());
        bag.setCreatedTimestamp(OffsetDateTime.now());

        bag = resolveRelations(bag, productBagDTO);

        ProductBag savedBag = productBagRepository.save(bag);
        log.info("Successfully created product bag with ID: {}", savedBag.getId());
        return productBagMapper.toDto(savedBag);
    }

    @Transactional
    public ProductBagDTO updateProductBag(UUID id, ProductBagDTO productBagDTO, String updatedBy) {
        log.info("Updating product bag with ID: {}", id);
        ProductBag existingBag = productBagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProductBag not found"));
        User user = userService.processUser(updatedBy);

        existingBag.setName(productBagDTO.getName());
        existingBag.setDescription(productBagDTO.getDescription());
        existingBag.setDisplayPrice(productBagDTO.getDisplayPrice());
        existingBag.setIsActive(
                productBagDTO.getIsActive() != null ? productBagDTO.getIsActive() : existingBag.getIsActive());

        existingBag.setLastUpdatedTimestamp(OffsetDateTime.now());
        existingBag.setLastUpdatedBy(user.getSub());

        existingBag = resolveRelations(existingBag, productBagDTO);

        ProductBag updatedBag = productBagRepository.save(existingBag);
        log.info("Successfully updated product bag with ID: {}", updatedBag.getId());
        return productBagMapper.toDto(updatedBag);
    }

    public void deleteProductBag(UUID id) {
        log.info("Deleting product bag with ID: {}", id);
        productBagRepository.deleteById(id);
        log.info("Successfully deleted product bag with ID: {}", id);
    }

    private ProductBag resolveRelations(ProductBag bag, ProductBagDTO dto) {
        if (dto.getProducts() != null) {
            List<UUID> productIds = dto.getProducts().stream()
                    .map(p -> p.getId())
                    .toList();
            List<Product> products = productRepository.findAllById(productIds);
            bag.setProducts(products);
        }

        if (dto.getCategories() != null) {
            List<UUID> categoryIds = dto.getCategories().stream()
                    .map(c -> c.getId())
                    .toList();
            List<ProductCategory> categories = productCategoryRepository.findAllById(categoryIds);
            bag.setCategories(categories);
        }

        return bag;
    }
}
