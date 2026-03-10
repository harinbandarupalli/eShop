package com.bilbo.store.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.bilbo.store.dto.ProductDTO;
import com.bilbo.store.entites.Product;
import com.bilbo.store.entites.User;
import com.bilbo.store.mapper.ProductMapper;
import com.bilbo.store.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProductService service;

    private final String testUserEmail = "test@example.com";
    private final String testUserSub = "auth0|123";

    private User getMockUser() {
        User user = new User();
        user.setEmail(testUserEmail);
        user.setSub(testUserSub);
        return user;
    }

    @Test
    void testGetAllProducts() {
        Product entity = new Product();
        entity.setId(UUID.randomUUID());
        entity.setName("Laptop");

        ProductDTO dto = new ProductDTO();
        dto.setId(entity.getId());
        dto.setName("Laptop");

        when(productRepository.findAll()).thenReturn(List.of(entity));
        when(productMapper.toDto(any(Product.class))).thenReturn(dto);

        List<ProductDTO> result = service.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(1)).toDto(any(Product.class));
    }

    @Test
    void testCreateProduct() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Laptop");

        Product entity = new Product();
        entity.setName("Laptop");

        Product saved = new Product();
        saved.setId(UUID.randomUUID());
        saved.setName("Laptop");
        saved.setCreatedBy(testUserSub);

        ProductDTO savedDto = new ProductDTO();
        savedDto.setId(saved.getId());
        savedDto.setName("Laptop");

        when(userService.processUser(testUserEmail)).thenReturn(getMockUser());
        when(productMapper.toEntity(dto)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(saved);
        when(productMapper.toDto(saved)).thenReturn(savedDto);

        ProductDTO result = service.createProduct(dto, testUserEmail);

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals("Laptop", result.getName());
        verify(userService, times(1)).processUser(testUserEmail);
        verify(productRepository, times(1)).save(entity);
        assertEquals(testUserSub, entity.getCreatedBy());
    }

    @Test
    void testGetProductById_Success() {
        UUID id = UUID.randomUUID();
        Product entity = new Product();
        entity.setId(id);
        entity.setName("Laptop");

        ProductDTO dto = new ProductDTO();
        dto.setId(entity.getId());
        dto.setName("Laptop");

        when(productRepository.findById(id)).thenReturn(Optional.of(entity));
        when(productMapper.toDto(any(Product.class))).thenReturn(dto);

        ProductDTO result = service.getProductById(id);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    void testGetProductById_NotFound() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getProductById(id));
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    void testUpdateProduct_Success() {
        UUID id = UUID.randomUUID();
        ProductDTO dto = new ProductDTO();
        dto.setName("Gaming Laptop");
        dto.setPrice(BigDecimal.valueOf(1500.00));
        dto.setStockQuantity(10);
        dto.setIsTrending(true);
        dto.setIsActive(true);

        Product existing = new Product();
        existing.setId(id);
        existing.setName("Laptop");
        existing.setPrice(BigDecimal.valueOf(1000.00));
        existing.setStockQuantity(5);
        existing.setIsTrending(false);
        existing.setIsActive(false);

        Product saved = new Product();
        saved.setId(id);

        ProductDTO savedDto = new ProductDTO();
        savedDto.setId(id);

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userService.processUser(testUserEmail)).thenReturn(getMockUser());
        when(productRepository.save(existing)).thenReturn(saved);
        when(productMapper.toDto(saved)).thenReturn(savedDto);

        service.updateProduct(id, dto, testUserEmail);

        assertEquals("Gaming Laptop", existing.getName());
        assertEquals(BigDecimal.valueOf(1500.00), existing.getPrice());
        assertEquals(10, existing.getStockQuantity());
        assertTrue(existing.getIsTrending());
        assertTrue(existing.getIsActive());
        assertEquals(testUserSub, existing.getLastUpdatedBy());
        verify(productRepository, times(1)).save(existing);
    }

    @Test
    void testUpdateProduct_NotFound() {
        UUID id = UUID.randomUUID();
        ProductDTO dto = new ProductDTO();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateProduct(id, dto, testUserEmail));
        verify(productRepository, never()).save(any());
    }

    @Test
    void testPatchProduct_Success_PartialUpdate() {
        UUID id = UUID.randomUUID();
        ProductDTO dto = new ProductDTO();
        dto.setPrice(BigDecimal.valueOf(1500.00)); // Only updating price

        Product existing = new Product();
        existing.setId(id);
        existing.setName("Laptop");
        existing.setPrice(BigDecimal.valueOf(1000.00));
        existing.setStockQuantity(5);
        existing.setIsTrending(false);
        existing.setIsActive(false);

        Product saved = new Product();
        saved.setId(id);

        ProductDTO savedDto = new ProductDTO();
        savedDto.setId(id);

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userService.processUser(testUserEmail)).thenReturn(getMockUser());
        when(productRepository.save(existing)).thenReturn(saved);
        when(productMapper.toDto(saved)).thenReturn(savedDto);

        service.patchProduct(id, dto, testUserEmail);

        assertEquals("Laptop", existing.getName()); // Should remain unchanged
        assertEquals(BigDecimal.valueOf(1500.00), existing.getPrice()); // Should be updated
        assertEquals(5, existing.getStockQuantity()); // Should remain unchanged
        assertEquals(testUserSub, existing.getLastUpdatedBy());
        verify(productRepository, times(1)).save(existing);
    }

    @Test
    void testPatchProduct_NotFound() {
        UUID id = UUID.randomUUID();
        ProductDTO dto = new ProductDTO();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.patchProduct(id, dto, testUserEmail));
        verify(productRepository, never()).save(any());
    }

    @Test
    void testDeleteProduct() {
        UUID id = UUID.randomUUID();

        service.deleteProduct(id);

        verify(productRepository, times(1)).deleteById(id);
    }
}
