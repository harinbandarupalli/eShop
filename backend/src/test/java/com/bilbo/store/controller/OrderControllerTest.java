package com.bilbo.store.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bilbo.store.dto.OrderDTO;
import com.bilbo.store.service.OrderService;
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
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private OrderController controller;

    private final String authEmail = "auth@example.com";
    private final String guestEmail = "guest@example.com";
    private final String sessionId = "session-123";

    @BeforeEach
    void setUp() {
        lenient().when(authentication.getPrincipal()).thenReturn(jwt);
    }

    @Test
    void testGetMyOrders_Auth() {
        when(jwt.hasClaim("email")).thenReturn(true);
        when(jwt.getClaimAsString("email")).thenReturn(authEmail);

        OrderDTO dto = new OrderDTO();
        when(orderService.getOrdersByEmail(authEmail)).thenReturn(List.of(dto));

        ResponseEntity<List<OrderDTO>> response = controller.getMyOrders(authentication, null, sessionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(orderService, times(1)).getOrdersByEmail(authEmail);
    }

    @Test
    void testGetMyOrders_GuestEmail() {
        OrderDTO dto = new OrderDTO();
        when(orderService.getOrdersByEmail(guestEmail)).thenReturn(List.of(dto));

        ResponseEntity<List<OrderDTO>> response = controller.getMyOrders(null, guestEmail, sessionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService, times(1)).getOrdersByEmail(guestEmail);
    }

    @Test
    void testGetMyOrders_SessionId() {
        OrderDTO dto = new OrderDTO();
        when(orderService.getOrdersByEmail(sessionId)).thenReturn(List.of(dto));

        ResponseEntity<List<OrderDTO>> response = controller.getMyOrders(null, null, sessionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService, times(1)).getOrdersByEmail(sessionId);
    }

    @Test
    void testGetOrderById() {
        UUID id = UUID.randomUUID();
        when(jwt.hasClaim("email")).thenReturn(true);
        when(jwt.getClaimAsString("email")).thenReturn(authEmail);

        OrderDTO dto = new OrderDTO();
        when(orderService.getOrderById(authEmail, id)).thenReturn(dto);

        ResponseEntity<OrderDTO> response = controller.getOrderById(authentication, null, sessionId, id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testCheckout_Auth() {
        UUID addressId = UUID.randomUUID();
        UUID paymentMethodId = UUID.randomUUID();
        UUID shippingTypeId = UUID.randomUUID();

        when(jwt.hasClaim("email")).thenReturn(true);
        when(jwt.getClaimAsString("email")).thenReturn(authEmail);

        OrderDTO output = new OrderDTO();
        when(orderService.checkout(authEmail, sessionId, addressId, paymentMethodId, shippingTypeId))
                .thenReturn(output);

        ResponseEntity<OrderDTO> response = controller.checkout(authentication, null, sessionId, addressId,
                paymentMethodId, shippingTypeId);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(output, response.getBody());
    }

    @Test
    void testCheckout_Guest() {
        UUID addressId = UUID.randomUUID();
        UUID paymentMethodId = UUID.randomUUID();
        UUID shippingTypeId = UUID.randomUUID();

        OrderDTO output = new OrderDTO();
        when(orderService.checkout(guestEmail, sessionId, addressId, paymentMethodId, shippingTypeId))
                .thenReturn(output);

        ResponseEntity<OrderDTO> response = controller.checkout(null, guestEmail, sessionId, addressId, paymentMethodId,
                shippingTypeId);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(output, response.getBody());
    }

    @Test
    void testResolveEmail_Exception() {
        assertThrows(IllegalArgumentException.class, () -> controller.getMyOrders(null, null, null));
    }
}
