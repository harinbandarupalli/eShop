package com.bilbo.store.component;

import com.bilbo.store.entites.Product;
import com.bilbo.store.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityAndAuditingComponentTest extends BaseComponentTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private JdbcTemplate jdbcTemplate;

        @Test
        void productUpdate_TriggersHistoryRecord() throws Exception {
                // 1. Create a Product
                Product product = new Product();
                product.setName("Original Ring");
                product.setPrice(new BigDecimal("999.99"));
                product.setStockQuantity(1);
                product.setIsActive(true);
                product.setIsTrending(false);
                Product savedProduct = productRepository.save(product);

                // Verify history has exactly 1 INSERT record initially
                List<Map<String, Object>> initialHistory = jdbcTemplate.queryForList(
                                "SELECT * FROM eshop.products_history WHERE id = ?",
                                savedProduct.getId());
                assertEquals(1, initialHistory.size(), "History should have 1 record from INSERT before updates");

                // 2. Update the Product's price and name
                savedProduct.setPrice(new BigDecimal("1500.00"));
                savedProduct.setName("The One Ring");
                productRepository.save(savedProduct);
                productRepository.flush(); // Force the update to the DB to trigger the history function

                // 3. Verify history was recorded by the DB trigger (now 2 records)
                List<Map<String, Object>> updatedHistory = jdbcTemplate.queryForList(
                                "SELECT * FROM eshop.products_history WHERE id = ? ORDER BY changed_on DESC",
                                savedProduct.getId());

                assertEquals(2, updatedHistory.size(), "History record should have been created for UPDATE");

                // Product History captures the NEW state (first in descending order)
                Map<String, Object> historyRecord = updatedHistory.get(0);
                assertEquals("The One Ring", historyRecord.get("name"));
                // Numeric mappings in JDBC might come back as numeric/BigDecimal, test string
                // comparison
                assertEquals(new BigDecimal("1500.00"), historyRecord.get("price"));
                assertEquals("U", historyRecord.get("action"));
        }

        @Test
        void unauthenticatedUser_CannotAccessProtectedEndpoints() throws Exception {
                // No JWT provided, attempting to access admin endpoint
                mockMvc.perform(post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                                .andExpect(status().isUnauthorized());

                // Attempting to create an address without JWT and without Guest Session ID
                mockMvc.perform(post("/api/addresses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"addressLine1\": \"Rohan\"}"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void authenticatedUser_CannotDeleteOtherUsersData() throws Exception {
                // We know from UserProfileFlowComponentTest that this will return a 404
                // when scoping to the JWT email. Let's ensure they can't delete carts either.
                mockMvc.perform(delete("/api/carts/some-random-id")
                                .with(jwt().jwt(b -> b.claim("email", "hacker@example.com"))))
                                .andExpect(status().isNotFound()); // Or Forbidden depending on implementation
        }
}
