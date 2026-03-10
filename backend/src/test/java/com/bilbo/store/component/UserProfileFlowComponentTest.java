package com.bilbo.store.component;

import com.bilbo.store.entites.Address;
import com.bilbo.store.entites.PaymentMethod;
import com.bilbo.store.repository.AddressRepository;
import com.bilbo.store.repository.PaymentMethodRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserProfileFlowComponentTest extends BaseComponentTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private AddressRepository addressRepository;

        @Autowired
        private PaymentMethodRepository paymentMethodRepository;

        @Autowired
        private ObjectMapper objectMapper;

        private static final String TEST_EMAIL = "profileuser@example.com";

        @Test
        void userCanManageAddresses() throws Exception {
                // 1. Create First Address (Should automatically become default)
                String addressJson1 = """
                                {
                                    "email": "%s",
                                    "addressLine1": "123 Main St",
                                    "city": "Springfield",
                                    "stateProvinceRegion": "IL",
                                    "postalCode": "62701",
                                    "country": "USA",
                                    "isDefault": false
                                }
                                """.formatted(TEST_EMAIL);

                mockMvc.perform(post("/api/addresses")
                                .with(jwt().jwt(builder -> builder.claim("email", TEST_EMAIL)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(addressJson1))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.addressLine1").value("123 Main St"))
                                // Business Logic: First address might be forced to default = true
                                .andExpect(jsonPath("$.isDefault").value(true));

                // Wait to capture DB state
                List<Address> addresses = addressRepository.findByEmail(TEST_EMAIL);
                assertEquals(1, addresses.size());
                assertTrue(addresses.get(0).getIsDefault());

                // 2. Create Second Address (Explicitly set as default)
                String addressJson2 = """
                                {
                                    "email": "%s",
                                    "addressLine1": "456 Oak Ave",
                                    "city": "Springfield",
                                    "stateProvinceRegion": "IL",
                                    "postalCode": "62702",
                                    "country": "USA",
                                    "isDefault": true
                                }
                                """.formatted(TEST_EMAIL);

                mockMvc.perform(post("/api/addresses")
                                .with(jwt().jwt(builder -> builder.claim("email", TEST_EMAIL)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(addressJson2))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.addressLine1").value("456 Oak Ave"))
                                .andExpect(jsonPath("$.isDefault").value(true));

                // 3. Verify Business Logic (Old default should be turned off)
                List<Address> updatedAddresses = addressRepository.findByEmail(TEST_EMAIL);
                assertEquals(2, updatedAddresses.size());

                Address newDefault = updatedAddresses.stream().filter(a -> a.getAddressLine1().equals("456 Oak Ave"))
                                .findFirst().get();
                Address oldAddress = updatedAddresses.stream().filter(a -> a.getAddressLine1().equals("123 Main St"))
                                .findFirst().get();

                assertTrue(newDefault.getIsDefault());
                assertFalse(oldAddress.getIsDefault(), "Old address should no longer be default");

                // 4. Delete an Address
                mockMvc.perform(delete("/api/addresses/" + oldAddress.getId())
                                .with(jwt().jwt(builder -> builder.claim("email", TEST_EMAIL))))
                                .andExpect(status().isNoContent());

                assertEquals(1, addressRepository.findByEmail(TEST_EMAIL).size());
        }

        @Test
        void userCanManagePaymentMethods() throws Exception {
                // 1. Create a Payment Method
                String paymentJson = """
                                {
                                    "email": "%s",
                                    "cardholderName": "Frodo Baggins",
                                    "last4": "4242",
                                    "expiryMonth": 12,
                                    "expiryYear": 2028,
                                    "cardType": "Visa",
                                    "isDefault": true
                                }
                                """.formatted(TEST_EMAIL);

                mockMvc.perform(post("/api/payments")
                                .with(jwt().jwt(builder -> builder.claim("email", TEST_EMAIL)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(paymentJson))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.cardholderName").value("Frodo Baggins"))
                                .andExpect(jsonPath("$.isDefault").value(true));

                List<PaymentMethod> payments = paymentMethodRepository.findByEmail(TEST_EMAIL);
                assertEquals(1, payments.size());
                assertEquals("4242", payments.get(0).getLast4());

                // 2. Fetch Payment Methods
                mockMvc.perform(get("/api/payments")
                                .with(jwt().jwt(builder -> builder.claim("email", TEST_EMAIL))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].cardholderName").value("Frodo Baggins"));
        }

        @Test
        void unauthorizedAccess_isBlocked() throws Exception {
                // Attempting to hit an address endpoint for a DIFFERENT user
                mockMvc.perform(get("/api/addresses")
                                .with(jwt().jwt(builder -> builder.claim("email", "hacker@example.com"))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(0))); // Return empty list, not exception, because they
                                                                       // own NO
                                                                       // addresses

                // But if they try to delete an address they don't own:
                Address address = new Address();
                address.setEmail(TEST_EMAIL);
                address.setAddressLine1("Safe House");
                address.setCity("Rivendell");
                address.setCountry("Middle Earth");
                address.setPostalCode("1234");
                address.setStateProvinceRegion("ME");
                address.setIsDefault(true);
                Address savedAddress = addressRepository.save(address);

                // Security logic might throw 403 Forbidden or 404 Not Found since the query
                // scoping might look for (id AND email)
                mockMvc.perform(delete("/api/addresses/" + savedAddress.getId())
                                .with(jwt().jwt(builder -> builder.claim("email", "hacker@example.com"))))
                                .andExpect(status().isNotFound()); // The AddressService filters deletions by email too
        }
}
