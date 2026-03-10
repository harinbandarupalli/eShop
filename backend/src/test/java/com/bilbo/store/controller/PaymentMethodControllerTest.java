package com.bilbo.store.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.bilbo.store.dto.PaymentMethodDTO;
import com.bilbo.store.service.PaymentMethodService;
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
class PaymentMethodControllerTest {

    @Mock
    private PaymentMethodService paymentMethodService;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private PaymentMethodController controller;

    private final String authEmail = "auth@example.com";
    private final String guestEmail = "guest@example.com";

    @BeforeEach
    void setUp() {
        lenient().when(authentication.getPrincipal()).thenReturn(jwt);
    }

    @Test
    void testGetPaymentMethods_Auth() {
        when(jwt.hasClaim("email")).thenReturn(true);
        when(jwt.getClaimAsString("email")).thenReturn(authEmail);

        PaymentMethodDTO dto = new PaymentMethodDTO();
        when(paymentMethodService.getPaymentMethodsByEmail(authEmail)).thenReturn(List.of(dto));

        ResponseEntity<List<PaymentMethodDTO>> response = controller.getPaymentMethods(authentication, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(paymentMethodService, times(1)).getPaymentMethodsByEmail(authEmail);
    }

    @Test
    void testGetPaymentMethods_Guest() {
        PaymentMethodDTO dto = new PaymentMethodDTO();
        when(paymentMethodService.getPaymentMethodsByEmail(guestEmail)).thenReturn(List.of(dto));

        ResponseEntity<List<PaymentMethodDTO>> response = controller.getPaymentMethods(null, guestEmail);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(paymentMethodService, times(1)).getPaymentMethodsByEmail(guestEmail);
    }

    @Test
    void testCreatePaymentMethod() {
        when(jwt.hasClaim("email")).thenReturn(true);
        when(jwt.getClaimAsString("email")).thenReturn(authEmail);

        PaymentMethodDTO input = new PaymentMethodDTO();
        PaymentMethodDTO output = new PaymentMethodDTO();
        when(paymentMethodService.createPaymentMethod(authEmail, input)).thenReturn(output);

        ResponseEntity<PaymentMethodDTO> response = controller.createPaymentMethod(authentication, null, input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(output, response.getBody());
    }

    @Test
    void testDeletePaymentMethod() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> response = controller.deletePaymentMethod(null, guestEmail, id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(paymentMethodService, times(1)).deletePaymentMethod(guestEmail, id);
    }

    @Test
    void testResolveEmail_NoAuthNoGuest() {
        PaymentMethodDTO dto = new PaymentMethodDTO();
        assertThrows(IllegalArgumentException.class, () -> controller.createPaymentMethod(null, null, dto));
        assertThrows(IllegalArgumentException.class, () -> controller.createPaymentMethod(null, "", dto));
    }
}
