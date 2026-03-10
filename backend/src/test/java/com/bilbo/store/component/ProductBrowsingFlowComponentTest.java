package com.bilbo.store.component;

import com.bilbo.store.entites.Product;
import com.bilbo.store.entites.ProductBag;
import com.bilbo.store.entites.ProductCategory;
import com.bilbo.store.repository.ProductBagRepository;
import com.bilbo.store.repository.ProductCategoryRepository;
import com.bilbo.store.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductBrowsingFlowComponentTest extends BaseComponentTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductCategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductBagRepository bagRepository;

    private ProductCategory testCategory;
    private Product activeProduct;
    private Product inactiveProduct;
    private ProductBag activeBag;
    private ProductBag inactiveBag;

    @BeforeEach
    void setupCatalog() {
        // 1. Create a Category
        ProductCategory category = new ProductCategory();
        category.setName("Outerwear");
        category.setDescription("Coats and Jackets");
        testCategory = categoryRepository.save(category);

        // 2. Create Products
        Product p1 = new Product();
        p1.setName("Winter Coat");
        p1.setPrice(new BigDecimal("120.00"));
        p1.setStockQuantity(50);
        p1.setIsActive(true);
        p1.setIsTrending(true);
        activeProduct = productRepository.save(p1);

        Product p2 = new Product();
        p2.setName("Discontinued Jacket");
        p2.setPrice(new BigDecimal("45.00"));
        p2.setStockQuantity(0);
        p2.setIsActive(false);
        p2.setIsTrending(false);
        inactiveProduct = productRepository.save(p2);

        // 3. Create Product Bags
        ProductBag bag1 = new ProductBag();
        bag1.setName("Cozy Winter Bundle");
        bag1.setDescription("Perfect for the snow");
        bag1.setDisplayPrice(new BigDecimal("120.00"));
        bag1.setIsActive(true);
        bag1.setCategories(List.of(testCategory));
        bag1.setProducts(List.of(activeProduct));
        activeBag = bagRepository.save(bag1);

        ProductBag bag2 = new ProductBag();
        bag2.setName("Old Fall Bundle");
        bag2.setDisplayPrice(new BigDecimal("45.00"));
        bag2.setIsActive(false);
        bag2.setCategories(List.of(testCategory));
        bag2.setProducts(List.of(inactiveProduct));
        inactiveBag = bagRepository.save(bag2);
    }

    @Test
    void browseCategoriesAndBags() throws Exception {
        // 1. Fetch all Categories ensures they exist
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                // Ensure at least our test category exists
                .andExpect(jsonPath("$.[?(@.id == '" + testCategory.getId() + "')]").exists())
                .andExpect(jsonPath("$.[?(@.id == '" + testCategory.getId() + "')].name").value("Outerwear"));

        // 2. Fetch Bags by Category
        // Note: Currently ProductBagController might not have a direct byCategory
        // method
        // Assume there is a general /api/product-bags endpoint that allows
        // filtering/pagination
        // OR we directly fetch the bag to ensure it handles related entities correctly.

        // Let's test standard GET endpoints mapped in the app:

        mockMvc.perform(get("/api/product-bags/" + activeBag.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cozy Winter Bundle"))
                .andExpect(jsonPath("$.displayPrice").value(120.00))
                .andExpect(jsonPath("$.categories", hasSize(1)))
                .andExpect(jsonPath("$.categories[0].name").value("Outerwear"))
                .andExpect(jsonPath("$.products", hasSize(1)))
                .andExpect(jsonPath("$.products[0].name").value("Winter Coat"));
    }

    @Test
    void fetchInactiveBag_ReturnsNotFoundOrFiltered() throws Exception {
        // Fetching a specific inactive bag by ID should either return a 404 or
        // specifically omit it.
        // Assuming your service throws EntityNotFoundException or filters by active
        // status:
        mockMvc.perform(get("/api/product-bags/" + inactiveBag.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void fetchProduct_VerifiesCorrectPricingAndMapping() throws Exception {
        // Ensure standalone products can also be fetched correctly via /api/products
        mockMvc.perform(get("/api/products/" + activeProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Winter Coat"))
                .andExpect(jsonPath("$.price").value(120.00))
                .andExpect(jsonPath("$.stockQuantity").value(50))
                .andExpect(jsonPath("$.isActive").value(true));
    }
}
