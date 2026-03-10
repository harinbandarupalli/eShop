package com.bilbo.store.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bilbo.store.dto.CartDTO;
import com.bilbo.store.service.CartService;
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
class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private CartController controller;

    private final String authEmail = "auth@example.com";
    private final String guestEmail = "guest@example.com";
    private final String sessionId = "session-123";
    private final UUID bagId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        lenient().when(authentication.getPrincipal()).thenReturn(jwt);
    }

    @Test
    void testGetMyCart_Auth() {
        when(jwt.hasClaim("email")).thenReturn(true);
        when(jwt.getClaimAsString("email")).thenReturn(authEmail);

        CartDTO dto = new CartDTO();
        when(cartService.getOrCreateCart(authEmail, sessionId)).thenReturn(dto);

        ResponseEntity<CartDTO> response = controller.getMyCart(authentication, null, sessionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(cartService, times(1)).getOrCreateCart(authEmail, sessionId);
    }

    @Test
    void testGetMyCart_Guest() {
        CartDTO dto = new CartDTO();
        when(cartService.getOrCreateCart(guestEmail, sessionId)).thenReturn(dto);

        ResponseEntity<CartDTO> response = controller.getMyCart(null, guestEmail, sessionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(cartService, times(1)).getOrCreateCart(guestEmail, sessionId);
    }

    @Test
    void testAddItemToCart() {
        when(jwt.hasClaim("email")).thenReturn(true);
        when(jwt.getClaimAsString("email")).thenReturn(authEmail);

        CartDTO dto = new CartDTO();
        when(cartService.addItemToCart(authEmail, sessionId, bagId, 2)).thenReturn(dto);

        ResponseEntity<CartDTO> response = controller.addItemToCart(authentication, null, sessionId, bagId, 2);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testUpdateItemQuantity() {
        CartDTO dto = new CartDTO();
        when(cartService.updateItemQuantity(guestEmail, sessionId, bagId, 5)).thenReturn(dto);

        ResponseEntity<CartDTO> response = controller.updateItemQuantity(null, guestEmail, sessionId, bagId, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testRemoveItemFromCart() {
        CartDTO dto = new CartDTO();
        when(cartService.removeItemFromCart(guestEmail, sessionId, bagId)).thenReturn(dto);

        ResponseEntity<CartDTO> response = controller.removeItemFromCart(null, guestEmail, sessionId, bagId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testMergeCart_Success() {
        when(jwt.hasClaim("email")).thenReturn(true);
        when(jwt.getClaimAsString("email")).thenReturn(authEmail);

        CartDTO dto = new CartDTO();
        when(cartService.mergeCart(authEmail, sessionId)).thenReturn(dto);

        ResponseEntity<CartDTO> response = controller.mergeCart(authentication, sessionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testMergeCart_Unauthenticated() {
        ResponseEntity<CartDTO> response = controller.mergeCart(null, sessionId);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testCalculateEmailFromJwtOrThrow_MissingEmail() {
        when(jwt.hasClaim("email")).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> controller.getMyCart(authentication, null, sessionId));
    }
}
