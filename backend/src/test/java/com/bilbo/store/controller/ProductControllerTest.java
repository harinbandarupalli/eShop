package com.bilbo.store.controller;

import com.bilbo.store.dto.ProductDTO;
import com.bilbo.store.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
    }

    private ProductDTO createProductDTO() {
        return new ProductDTO(UUID.randomUUID(), "Test Product", "Test Description", new BigDecimal("10.00"), 100, UUID.randomUUID(), false);
    }

    @Test
    void getAllProducts() throws Exception {
        List<ProductDTO> productList = Collections.singletonList(createProductDTO());

        when(productService.getAllProducts()).thenReturn(productList);

        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    @Test
    void getProductById() throws Exception {
        ProductDTO productDTO = createProductDTO();

        when(productService.getProductById(productDTO.id())).thenReturn(productDTO);

        mockMvc.perform(get("/api/products/{id}", productDTO.id())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    @WithMockUser(username = "test-user", roles = {"ADMIN"})
    void createProduct() throws Exception {
        ProductDTO productDTO = createProductDTO();

        when(productService.createProduct(any(ProductDTO.class), eq("test-user"))).thenReturn(productDTO);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    @WithMockUser(username = "test-user", roles = {"ADMIN"})
    void updateProduct() throws Exception {
        ProductDTO productDTO = createProductDTO();

        when(productService.updateProduct(eq(productDTO.id()), any(ProductDTO.class), eq("test-user"))).thenReturn(productDTO);

        mockMvc.perform(put("/api/products/{id}", productDTO.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    @WithMockUser(username = "test-user", roles = {"ADMIN"})
    void patchProduct() throws Exception {
        ProductDTO productDTO = createProductDTO();

        when(productService.patchProduct(eq(productDTO.id()), any(ProductDTO.class), eq("test-user"))).thenReturn(productDTO);

        mockMvc.perform(patch("/api/products/{id}", productDTO.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteProduct() throws Exception {
        UUID productId = UUID.randomUUID();

        mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().isNoContent());
    }
}
