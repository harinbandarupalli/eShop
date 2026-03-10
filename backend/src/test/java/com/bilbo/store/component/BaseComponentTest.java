package com.bilbo.store.component;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
                "spring.flyway.placeholders.eshop.app.password=testpassword123"
})
public abstract class BaseComponentTest {

        @MockBean
        private JwtDecoder jwtDecoder;

        @MockBean
        private ClientRegistrationRepository clientRegistrationRepository;

        @Autowired
        private JdbcTemplate jdbcTemplate;

        @AfterEach
        void cleanDatabase() {
                // Truncate all non-seeded data perfectly across all component tests to prevent
                // state leakage
                jdbcTemplate.execute(
                                "TRUNCATE TABLE eshop.product_categories, eshop.products, eshop.product_bags, eshop.product_bag_categories, eshop.product_bag_products, eshop.addresses, eshop.payment_methods, eshop.carts, eshop.cart_items, eshop.orders, eshop.order_bag_snapshots, eshop.users CASCADE");
        }

        @ServiceConnection
        static final PostgreSQLContainer<?> postgres;

        static {
                postgres = new PostgreSQLContainer<>("postgres:15-alpine")
                                .withDatabaseName("eshop")
                                .withEnv("ESHOP_APP_PASSWORD", "testpassword")
                                .withUsername("test")
                                .withPassword("test");
                postgres.start();
        }

        @DynamicPropertySource
        static void configureProperties(DynamicPropertyRegistry registry) {
                // Configure PostgreSQL
                registry.add("spring.datasource.url", postgres::getJdbcUrl);
                registry.add("spring.datasource.username", postgres::getUsername);
                registry.add("spring.datasource.password", postgres::getPassword);
                // Explicitly set hibernate dialect to avoid warnings
                registry.add("spring.jpa.properties.hibernate.dialect",
                                () -> "org.hibernate.dialect.PostgreSQLDialect");
                // Use Flyway exclusively for schema creation, pointing to the DB module
                registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
                registry.add("spring.flyway.enabled", () -> "true");
                registry.add("spring.flyway.locations",
                                () -> "filesystem:" + System.getProperty("user.dir")
                                                + "/../DB/src/main/resources/db/migration");
                registry.add("spring.flyway.default-schema", () -> "eshop");
                registry.add("spring.flyway.placeholders.eshop-app-password", postgres::getPassword);

                // Mock Auth0 Environment Variables
                registry.add("AUTH0_ISSUER_URI", () -> "https://mock.us.auth0.com/");
                registry.add("AUTH0_CLIENT_ID", () -> "mockId");
                registry.add("AUTH0_CLIENT_SECRET", () -> "mockSecret");
                registry.add("AUTH0_AUDIENCE", () -> "https://mock.api.com/");
        }
}
