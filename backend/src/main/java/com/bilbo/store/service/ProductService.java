package com.bilbo.store.service;

import com.bilbo.store.dto.ProductDTO;
import com.bilbo.store.repository.ProductRepository;
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

  public List<ProductDTO> getAllProducts() {
    return null;
  }

  public ProductDTO createProduct(ProductDTO newProduct, String createdBy) {
    return null;
  }

  public ProductDTO getProductById(UUID id) {
    return null;
  }

  public ProductDTO updateProduct(UUID id, ProductDTO productDTO) {
    return null;
  }

  public ProductDTO patchProduct(UUID id, ProductDTO productDTO) {
    return null;
  }

  public void deleteProduct(UUID id) {
    // TODO document why this method is empty
  }
}
