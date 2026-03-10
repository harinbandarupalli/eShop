package com.bilbo.store.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bilbo.store.dto.ProductCategoryDTO;
import com.bilbo.store.service.ProductCategoryService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ProductCategoryControllerTest {

    @Mock
    private ProductCategoryService categoryService;

    @InjectMocks
    private ProductCategoryController controller;

    @Test
    void testGetAllCategories() {
        ProductCategoryDTO dto = new ProductCategoryDTO();
        when(categoryService.getAllCategories()).thenReturn(List.of(dto));

        ResponseEntity<List<ProductCategoryDTO>> response = controller.getAllCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    void testGetCategoryById() {
        UUID id = UUID.randomUUID();
        ProductCategoryDTO dto = new ProductCategoryDTO();
        when(categoryService.getCategoryById(id)).thenReturn(dto);

        ResponseEntity<ProductCategoryDTO> response = controller.getCategoryById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(categoryService, times(1)).getCategoryById(id);
    }

    @Test
    void testCreateCategory() {
        ProductCategoryDTO input = new ProductCategoryDTO();
        ProductCategoryDTO output = new ProductCategoryDTO();
        when(categoryService.createCategory(input)).thenReturn(output);

        ResponseEntity<ProductCategoryDTO> response = controller.createCategory(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(output, response.getBody());
        verify(categoryService, times(1)).createCategory(input);
    }

    @Test
    void testUpdateCategory() {
        UUID id = UUID.randomUUID();
        ProductCategoryDTO input = new ProductCategoryDTO();
        ProductCategoryDTO output = new ProductCategoryDTO();
        when(categoryService.updateCategory(id, input)).thenReturn(output);

        ResponseEntity<ProductCategoryDTO> response = controller.updateCategory(id, input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(output, response.getBody());
        verify(categoryService, times(1)).updateCategory(id, input);
    }

    @Test
    void testDeleteCategory() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> response = controller.deleteCategory(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(categoryService, times(1)).deleteCategory(id);
    }
}
