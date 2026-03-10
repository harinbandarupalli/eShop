package com.bilbo.store.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bilbo.store.dto.ProductBagDTO;
import com.bilbo.store.service.ProductBagService;
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
class ProductBagControllerTest {

    @Mock
    private ProductBagService productBagService;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private ProductBagController controller;

    private final String testUserId = "auth0|321";

    @BeforeEach
    void setUp() {
        lenient().when(authentication.getPrincipal()).thenReturn(jwt);
        lenient().when(jwt.getSubject()).thenReturn(testUserId);
    }

    @Test
    void testGetActiveProductBags() {
        ProductBagDTO dto = new ProductBagDTO();
        when(productBagService.getActiveProductBags()).thenReturn(List.of(dto));

        ResponseEntity<List<ProductBagDTO>> response = controller.getActiveProductBags();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(productBagService, times(1)).getActiveProductBags();
    }

    @Test
    void testGetProductBagsByCategory() {
        UUID categoryId = UUID.randomUUID();
        ProductBagDTO dto = new ProductBagDTO();
        when(productBagService.getActiveProductBagsByCategory(categoryId)).thenReturn(List.of(dto));

        ResponseEntity<List<ProductBagDTO>> response = controller.getProductBagsByCategory(categoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(productBagService, times(1)).getActiveProductBagsByCategory(categoryId);
    }

    @Test
    void testGetProductBagById() {
        UUID id = UUID.randomUUID();
        ProductBagDTO dto = new ProductBagDTO();
        when(productBagService.getProductBagById(id)).thenReturn(dto);

        ResponseEntity<ProductBagDTO> response = controller.getProductBagById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(productBagService, times(1)).getProductBagById(id);
    }

    @Test
    void testCreateProductBag() {
        ProductBagDTO input = new ProductBagDTO();
        ProductBagDTO output = new ProductBagDTO();
        when(productBagService.createProductBag(input, testUserId)).thenReturn(output);

        ResponseEntity<ProductBagDTO> response = controller.createProductBag(input, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(output, response.getBody());
        verify(productBagService, times(1)).createProductBag(input, testUserId);
    }

    @Test
    void testUpdateProductBag() {
        UUID id = UUID.randomUUID();
        ProductBagDTO input = new ProductBagDTO();
        ProductBagDTO output = new ProductBagDTO();
        when(productBagService.updateProductBag(id, input, testUserId)).thenReturn(output);

        ResponseEntity<ProductBagDTO> response = controller.updateProductBag(id, input, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(output, response.getBody());
        verify(productBagService, times(1)).updateProductBag(id, input, testUserId);
    }

    @Test
    void testDeleteProductBag() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> response = controller.deleteProductBag(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(productBagService, times(1)).deleteProductBag(id);
    }

    @Test
    void testGetUserId_InvalidPrincipal() {
        when(authentication.getPrincipal()).thenReturn("Not a JWT");

        ProductBagDTO input = new ProductBagDTO();
        assertThrows(IllegalArgumentException.class, () -> controller.createProductBag(input, authentication));
    }
}
