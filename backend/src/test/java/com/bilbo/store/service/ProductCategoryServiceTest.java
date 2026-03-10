package com.bilbo.store.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.bilbo.store.dto.ProductCategoryDTO;
import com.bilbo.store.entites.ProductCategory;
import com.bilbo.store.mapper.ProductCategoryMapper;
import com.bilbo.store.repository.ProductCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceTest {

    @Mock
    private ProductCategoryRepository categoryRepository;

    @Mock
    private ProductCategoryMapper categoryMapper;

    @InjectMocks
    private ProductCategoryService service;

    @Test
    void testGetAllCategories() {
        ProductCategory category = new ProductCategory();
        category.setId(UUID.randomUUID());
        category.setName("Electronics");

        ProductCategoryDTO dto = new ProductCategoryDTO();
        dto.setId(category.getId());
        dto.setName("Electronics");

        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toDto(any(ProductCategory.class))).thenReturn(dto);

        List<ProductCategoryDTO> result = service.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getName());
        verify(categoryRepository, times(1)).findAll();
        verify(categoryMapper, times(1)).toDto(any(ProductCategory.class));
    }

    @Test
    void testGetCategoryById_Success() {
        UUID id = UUID.randomUUID();
        ProductCategory category = new ProductCategory();
        category.setId(id);
        category.setName("Electronics");

        ProductCategoryDTO dto = new ProductCategoryDTO();
        dto.setId(id);
        dto.setName("Electronics");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(any(ProductCategory.class))).thenReturn(dto);

        ProductCategoryDTO result = service.getCategoryById(id);

        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        verify(categoryRepository, times(1)).findById(id);
    }

    @Test
    void testGetCategoryById_NotFound() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getCategoryById(id));
        verify(categoryRepository, times(1)).findById(id);
    }

    @Test
    void testCreateCategory() {
        ProductCategoryDTO dto = new ProductCategoryDTO();
        dto.setName("Electronics");

        ProductCategory category = new ProductCategory();
        category.setName("Electronics");

        ProductCategory saved = new ProductCategory();
        saved.setId(UUID.randomUUID());
        saved.setName("Electronics");

        ProductCategoryDTO savedDto = new ProductCategoryDTO();
        savedDto.setId(saved.getId());
        savedDto.setName("Electronics");

        when(categoryMapper.toEntity(dto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(saved);
        when(categoryMapper.toDto(saved)).thenReturn(savedDto);

        ProductCategoryDTO result = service.createCategory(dto);

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals("Electronics", result.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void testUpdateCategory_Success() {
        UUID id = UUID.randomUUID();
        ProductCategoryDTO dto = new ProductCategoryDTO();
        dto.setName("New Electronics");

        ProductCategory existing = new ProductCategory();
        existing.setId(id);
        existing.setName("Electronics");

        ProductCategory updated = new ProductCategory();
        updated.setId(id);
        updated.setName("New Electronics");

        ProductCategoryDTO updatedDto = new ProductCategoryDTO();
        updatedDto.setId(id);
        updatedDto.setName("New Electronics");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(updated);
        when(categoryMapper.toDto(updated)).thenReturn(updatedDto);

        ProductCategoryDTO result = service.updateCategory(id, dto);

        assertNotNull(result);
        assertEquals("New Electronics", result.getName());
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, times(1)).save(existing);
    }

    @Test
    void testUpdateCategory_NotFound() {
        UUID id = UUID.randomUUID();
        ProductCategoryDTO dto = new ProductCategoryDTO();
        dto.setName("New Electronics");

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateCategory(id, dto));
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, never()).save(any(ProductCategory.class));
    }

    @Test
    void testDeleteCategory() {
        UUID id = UUID.randomUUID();

        service.deleteCategory(id);

        verify(categoryRepository, times(1)).deleteById(id);
    }
}
