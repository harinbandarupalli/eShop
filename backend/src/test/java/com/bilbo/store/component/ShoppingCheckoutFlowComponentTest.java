package com.bilbo.store.component;

import com.bilbo.store.entites.*;
import com.bilbo.store.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ShoppingCheckoutFlowComponentTest extends BaseComponentTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ProductCategoryRepository categoryRepository;
        @Autowired
        private ProductRepository productRepository;
        @Autowired
        private ProductBagRepository bagRepository;
        @Autowired
        private AddressRepository addressRepository;
        @Autowired
        private PaymentMethodRepository paymentMethodRepository;
        @Autowired
        private ShippingTypeRepository shippingTypeRepository;
        @Autowired
        private OrderRepository orderRepository;
        @Autowired
        private OrderBagSnapshotRepository snapshotRepository;
        @Autowired
        private CartRepository cartRepository;

        private ProductBag testBag;
        private Address testAddress;
        private PaymentMethod testPaymentMethod;
        private ShippingType testShippingType;

        private final String TEST_EMAIL = "flowuser@example.com";
        private final String TEST_SESSION_ID = "guest123";

        @BeforeEach
        void setupData() {
                // Create Product Catalog Data
                ProductCategory category = new ProductCategory();
                category.setName("T-Shirts");
                category = categoryRepository.save(category);

                Product product = new Product();
                product.setName("Test Shirt");
                product.setStockQuantity(100);
                product.setPrice(new BigDecimal("29.99"));
                product.setIsActive(true);
                product.setIsTrending(true);
                product = productRepository.save(product);

                ProductBag bag = new ProductBag();
                bag.setName("Bag 1");
                bag.setProducts(List.of(product));
                bag.setCategories(List.of(category));
                bag.setDisplayPrice(new BigDecimal("29.99"));
                bag.setIsActive(true);
                testBag = bagRepository.save(bag);

                // Create Checkout Dependencies
                Address address = new Address();
                address.setEmail(TEST_EMAIL);
                address.setAddressLine1("123 Test St");
                address.setCity("Testville");
                address.setStateProvinceRegion("TS");
                address.setCountry("USA");
                address.setPostalCode("12345");
                address.setIsDefault(true);
                testAddress = addressRepository.save(address);

                PaymentMethod pm = new PaymentMethod();
                pm.setEmail(TEST_EMAIL);
                pm.setCardholderName("John Doe");
                pm.setLast4("1234");
                pm.setExpiryMonth(12);
                pm.setExpiryYear(2025);
                pm.setCardType("Visa");
                pm.setIsDefault(true);
                testPaymentMethod = paymentMethodRepository.save(pm);

                // Fetch existing ShippingType created by Flyway V7 script
                testShippingType = shippingTypeRepository.findAll().stream()
                                .filter(st -> "Standard Shipping".equals(st.getName()))
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException(
                                                "Standard Shipping not found from Flyway seed"));
        }

        @Test
        void completeShoppingFlow() throws Exception {
                // 1. Guest Adds Item to Cart
                mockMvc.perform(post("/api/carts/items")
                                .header("X-Guest-SessionID", TEST_SESSION_ID)
                                .param("bagId", testBag.getId().toString())
                                .param("quantity", "2"))
                                .andExpect(status().isOk());

                // 2. Guest Updates Quantity
                mockMvc.perform(put("/api/carts/items/" + testBag.getId())
                                .header("X-Guest-SessionID", TEST_SESSION_ID)
                                .param("quantity", "3"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.items[0].quantity").value(3));

                // 3. Authenticated User Merges Cart
                // Using JWT mutator to simulate logged in user
                mockMvc.perform(post("/api/carts/merge")
                                .with(jwt().jwt(builder -> builder.claim("email", TEST_EMAIL)))
                                .header("X-Guest-SessionID", TEST_SESSION_ID))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value(TEST_EMAIL));

                // Ensure guest cart is marked MERGED
                Cart guestCart = cartRepository.findBySessionIdAndStatus(TEST_SESSION_ID, "MERGED").orElse(null);
                assertNotNull(guestCart, "Guest cart should be marked as MERGED");

                // 4. Authenticated User Checks out
                MvcResult checkoutResult = mockMvc.perform(post("/api/orders/checkout")
                                .with(jwt().jwt(builder -> builder.claim("email", TEST_EMAIL)))
                                .param("addressId", testAddress.getId().toString())
                                .param("paymentMethodId", testPaymentMethod.getId().toString())
                                .param("shippingTypeId", testShippingType.getId().toString()))
                                .andExpect(status().isCreated())
                                .andReturn();

                // 5. Validate Order Database State
                assertFalse(orderRepository.findAll().isEmpty(), "Order should be created");
                Order createdOrder = orderRepository.findAll().get(0);
                assertEquals(TEST_EMAIL, createdOrder.getEmail());
                assertEquals("PENDING", createdOrder.getStatus());
                assertEquals(testAddress.getId(), createdOrder.getAddress().getId());

                // Calculate expected final total (Price * Qty + Shipping)
                BigDecimal bagTotal = testBag.getDisplayPrice().multiply(new BigDecimal("3"));
                BigDecimal finalTotal = bagTotal.add(testShippingType.getCost());
                assertEquals(0, finalTotal.compareTo(createdOrder.getTotalAmount()));

                // 6. Validate OrderBagSnapshots
                var snapshots = snapshotRepository.findByOrderId(createdOrder.getId());
                assertEquals(1, snapshots.size());
                OrderBagSnapshot snapshot = snapshots.get(0);
                assertEquals(testBag.getId(), snapshot.getBag().getId());
                assertEquals(3, snapshot.getQuantity());
                assertEquals(0, testBag.getDisplayPrice().compareTo(snapshot.getPriceAtPurchase()));
                assertEquals("Bag 1", snapshot.getBagName());

                // 7. Validate Cart Cleared
                assertTrue(cartRepository.findByEmailAndStatus(TEST_EMAIL, "ACTIVE").isEmpty(),
                                "User cart should be soft-deleted (status = CHECKED_OUT) after checkout");
        }
}
