package com.bilbo.store.controller;

import com.bilbo.store.dto.ProductDTO;
import com.bilbo.store.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * A public endpoint to get all products.
     * No authentication is required here because we will add this path to the white-list.
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * A public endpoint to get a product by its ID.
     * No authentication is required here.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    /**
     * A protected endpoint to create a new product.
     * Only users with the 'ADMIN' role can access this.
     * The role is extracted from the JWT by your JwtAuthConverter.
     *
     * @param authentication The Authentication object, injected by Spring Security.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO, Authentication authentication) {
        // You can get the user's ID (the 'sub' claim) from the Authentication principal
        String userId = authentication.getName();
        ProductDTO createdProduct = productService.createProduct(productDTO, userId);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    /**
     * A protected endpoint to update an existing product.
     * Only users with the 'ADMIN' role can access this.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable UUID id, @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * A protected endpoint to partially update an existing product.
     * Only users with the 'ADMIN' role can access this.
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> patchProduct(@PathVariable UUID id, @RequestBody ProductDTO productDTO) {
        ProductDTO patchedProduct = productService.patchProduct(id, productDTO);
        return ResponseEntity.ok(patchedProduct);
    }

    /**
     * A protected endpoint to delete a product.
     * Only users with the 'ADMIN' role can access this.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
