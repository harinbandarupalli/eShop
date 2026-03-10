package com.bilbo.store.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.bilbo.store.dto.ProductBagDTO;
import com.bilbo.store.dto.ProductCategoryDTO;
import com.bilbo.store.dto.ProductDTO;
import com.bilbo.store.entites.Product;
import com.bilbo.store.entites.ProductBag;
import com.bilbo.store.entites.ProductCategory;
import com.bilbo.store.entites.User;
import com.bilbo.store.mapper.ProductBagMapper;
import com.bilbo.store.repository.ProductBagRepository;
import com.bilbo.store.repository.ProductCategoryRepository;
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
class ProductBagServiceTest {

    @Mock
    private ProductBagRepository productBagRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private ProductBagMapper productBagMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProductBagService service;

    private final String testEmail = "test@example.com";
    private final String testSub = "auth0|321";

    private User getMockUser() {
        User user = new User();
        user.setEmail(testEmail);
        user.setSub(testSub);
        return user;
    }

    @Test
    void testGetActiveProductBags() {
        ProductBag entity = new ProductBag();
        entity.setId(UUID.randomUUID());

        ProductBagDTO dto = new ProductBagDTO();
        dto.setId(entity.getId());

        when(productBagRepository.findByIsActiveTrue()).thenReturn(List.of(entity));
        when(productBagMapper.toDto(any(ProductBag.class))).thenReturn(dto);

        List<ProductBagDTO> result = service.getActiveProductBags();

        assertEquals(1, result.size());
        verify(productBagRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    void testGetActiveProductBagsByCategory() {
        UUID categoryId = UUID.randomUUID();
        ProductBag entity = new ProductBag();
        entity.setId(UUID.randomUUID());

        ProductBagDTO dto = new ProductBagDTO();
        dto.setId(entity.getId());

        when(productBagRepository.findActiveByCategoryId(categoryId)).thenReturn(List.of(entity));
        when(productBagMapper.toDto(any(ProductBag.class))).thenReturn(dto);

        List<ProductBagDTO> result = service.getActiveProductBagsByCategory(categoryId);

        assertEquals(1, result.size());
        verify(productBagRepository, times(1)).findActiveByCategoryId(categoryId);
    }

    @Test
    void testGetProductBagById_Success() {
        UUID id = UUID.randomUUID();
        ProductBag entity = new ProductBag();
        entity.setId(id);
        entity.setIsActive(true);

        ProductBagDTO dto = new ProductBagDTO();
        dto.setId(id);

        when(productBagRepository.findById(id)).thenReturn(Optional.of(entity));
        when(productBagMapper.toDto(any(ProductBag.class))).thenReturn(dto);

        ProductBagDTO result = service.getProductBagById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void testGetProductBagById_NotFound() {
        UUID id = UUID.randomUUID();
        when(productBagRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getProductBagById(id));
    }

    @Test
    void testCreateProductBag() {
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        ProductBagDTO dto = new ProductBagDTO();
        dto.setName("New Bag");
        ProductDTO pdto = new ProductDTO();
        pdto.setId(productId);
        dto.setProducts(List.of(pdto));
        ProductCategoryDTO cdto = new ProductCategoryDTO();
        cdto.setId(categoryId);
        dto.setCategories(List.of(cdto));

        ProductBag entity = new ProductBag();
        entity.setName("New Bag");

        ProductBag saved = new ProductBag();
        saved.setId(UUID.randomUUID());

        Product p = new Product();
        p.setId(productId);
        ProductCategory c = new ProductCategory();
        c.setId(categoryId);

        when(userService.processUser(testEmail)).thenReturn(getMockUser());
        when(productBagMapper.toEntity(dto)).thenReturn(entity);
        when(productRepository.findAllById(any())).thenReturn(List.of(p));
        when(productCategoryRepository.findAllById(any())).thenReturn(List.of(c));
        when(productBagRepository.save(entity)).thenReturn(saved);
        when(productBagMapper.toDto(saved)).thenReturn(new ProductBagDTO());

        ProductBagDTO result = service.createProductBag(dto, testEmail);

        assertNotNull(result);
        verify(productRepository, times(1)).findAllById(any());
        verify(productCategoryRepository, times(1)).findAllById(any());
        verify(productBagRepository, times(1)).save(entity);
        assertEquals(testSub, entity.getCreatedBy());
        assertEquals(1, entity.getProducts().size());
        assertEquals(1, entity.getCategories().size());
    }

    @Test
    void testUpdateProductBag_Success() {
        UUID id = UUID.randomUUID();
        ProductBagDTO dto = new ProductBagDTO();
        dto.setName("Updated Bag");
        dto.setDisplayPrice(BigDecimal.TEN);

        ProductBag existing = new ProductBag();
        existing.setId(id);
        existing.setName("Old Bag");

        ProductBag saved = new ProductBag();
        saved.setId(id);

        when(productBagRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userService.processUser(testEmail)).thenReturn(getMockUser());
        when(productBagRepository.save(existing)).thenReturn(saved);
        when(productBagMapper.toDto(saved)).thenReturn(new ProductBagDTO());

        service.updateProductBag(id, dto, testEmail);

        assertEquals("Updated Bag", existing.getName());
        assertEquals(BigDecimal.TEN, existing.getDisplayPrice());
        assertEquals(testSub, existing.getLastUpdatedBy());
        verify(productBagRepository, times(1)).save(existing);
    }

    @Test
    void testUpdateProductBag_NotFound() {
        UUID id = UUID.randomUUID();
        when(productBagRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateProductBag(id, new ProductBagDTO(), testEmail));
        verify(productBagRepository, never()).save(any());
    }

    @Test
    void testDeleteProductBag() {
        UUID id = UUID.randomUUID();

        service.deleteProductBag(id);

        verify(productBagRepository, times(1)).deleteById(id);
    }
}
