package com.bilbo.store.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.bilbo.store.dto.ProductDTO;
import com.bilbo.store.service.ProductService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private ProductController controller;

    private final String testUserId = "auth0|123";

    @BeforeEach
    void setUp() {
        lenient().when(authentication.getPrincipal()).thenReturn(jwt);
        lenient().when(jwt.getSubject()).thenReturn(testUserId);
    }

    @Test
    void testGetAllProducts() {
        ProductDTO dto = new ProductDTO();
        when(productService.getAllProducts()).thenReturn(List.of(dto));

        ResponseEntity<List<ProductDTO>> response = controller.getAllProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetProductById() {
        UUID id = UUID.randomUUID();
        ProductDTO dto = new ProductDTO();
        when(productService.getProductById(id)).thenReturn(dto);

        ResponseEntity<ProductDTO> response = controller.getProductById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(productService, times(1)).getProductById(id);
    }

    @Test
    void testCreateProduct() {
        ProductDTO inputDto = new ProductDTO();
        ProductDTO outputDto = new ProductDTO();
        when(productService.createProduct(inputDto, testUserId)).thenReturn(outputDto);

        ResponseEntity<ProductDTO> response = controller.createProduct(inputDto, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(outputDto, response.getBody());
        verify(productService, times(1)).createProduct(inputDto, testUserId);
    }

    @Test
    void testUpdateProduct() {
        UUID id = UUID.randomUUID();
        ProductDTO inputDto = new ProductDTO();
        ProductDTO outputDto = new ProductDTO();
        when(productService.updateProduct(id, inputDto, testUserId)).thenReturn(outputDto);

        ResponseEntity<ProductDTO> response = controller.updateProduct(id, inputDto, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(outputDto, response.getBody());
        verify(productService, times(1)).updateProduct(id, inputDto, testUserId);
    }

    @Test
    void testPatchProduct() {
        UUID id = UUID.randomUUID();
        ProductDTO inputDto = new ProductDTO();
        ProductDTO outputDto = new ProductDTO();
        when(productService.patchProduct(id, inputDto, testUserId)).thenReturn(outputDto);

        ResponseEntity<ProductDTO> response = controller.patchProduct(id, inputDto, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(outputDto, response.getBody());
        verify(productService, times(1)).patchProduct(id, inputDto, testUserId);
    }

    @Test
    void testDeleteProduct() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> response = controller.deleteProduct(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(productService, times(1)).deleteProduct(id);
    }

    @Test
    void testGetUserId_InvalidPrincipal() {
        when(authentication.getPrincipal()).thenReturn("Not a JWT");

        ProductDTO inputDto = new ProductDTO();
        assertThrows(IllegalArgumentException.class, () -> controller.createProduct(inputDto, authentication));
    }
}
