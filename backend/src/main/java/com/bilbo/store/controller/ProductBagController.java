package com.bilbo.store.controller;

import com.bilbo.store.dto.ProductBagDTO;
import com.bilbo.store.service.ProductBagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product-bags")
@RequiredArgsConstructor
public class ProductBagController {

    private final ProductBagService productBagService;

    @GetMapping
    public ResponseEntity<List<ProductBagDTO>> getActiveProductBags() {
        return ResponseEntity.ok(productBagService.getActiveProductBags());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductBagDTO>> getProductBagsByCategory(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(productBagService.getActiveProductBagsByCategory(categoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductBagDTO> getProductBagById(@PathVariable UUID id) {
        return ResponseEntity.ok(productBagService.getProductBagById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductBagDTO> createProductBag(@RequestBody ProductBagDTO productBagDTO,
            Authentication authentication) {
        String userId = getUserId(authentication);
        ProductBagDTO createdBag = productBagService.createProductBag(productBagDTO, userId);
        return new ResponseEntity<>(createdBag, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductBagDTO> updateProductBag(@PathVariable UUID id,
            @RequestBody ProductBagDTO productBagDTO, Authentication authentication) {
        String userId = getUserId(authentication);
        ProductBagDTO updatedBag = productBagService.updateProductBag(id, productBagDTO, userId);
        return ResponseEntity.ok(updatedBag);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProductBag(@PathVariable UUID id) {
        productBagService.deleteProductBag(id);
        return ResponseEntity.noContent().build();
    }

    private String getUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            throw new IllegalArgumentException("Authentication principal is not a valid JWT");
        }
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getSubject();
    }
}
