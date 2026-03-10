package com.bilbo.store.controller;

import com.bilbo.store.dto.OrderDTO;
import com.bilbo.store.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDTO>> getMyOrders(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Email", required = false) String guestEmail,
            @RequestHeader(value = "X-Guest-SessionID", required = false) String guestSessionId) {
        String email = resolveEmail(authentication, guestEmail, guestSessionId);
        return ResponseEntity.ok(orderService.getOrdersByEmail(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Email", required = false) String guestEmail,
            @RequestHeader(value = "X-Guest-SessionID", required = false) String guestSessionId,
            @PathVariable UUID id) {
        String email = resolveEmail(authentication, guestEmail, guestSessionId);
        return ResponseEntity.ok(orderService.getOrderById(email, id));
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderDTO> checkout(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Email", required = false) String guestEmail,
            @RequestHeader(value = "X-Guest-SessionID", required = false) String guestSessionId,
            @RequestParam UUID addressId,
            @RequestParam UUID paymentMethodId,
            @RequestParam UUID shippingTypeId) {

        String authEmail = null;
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            if (jwt.hasClaim("email")) {
                authEmail = jwt.getClaimAsString("email");
            }
        }

        String email = authEmail != null ? authEmail : guestEmail;

        OrderDTO created = orderService.checkout(email, guestSessionId, addressId, paymentMethodId, shippingTypeId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    private String resolveEmail(Authentication authentication, String guestEmail, String guestSessionId) {
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            if (jwt.hasClaim("email")) {
                return jwt.getClaimAsString("email");
            }
        }
        if (guestEmail != null && !guestEmail.isBlank()) {
            return guestEmail;
        }
        if (guestSessionId != null && !guestSessionId.isBlank()) {
            return guestSessionId;
        }
        throw new IllegalArgumentException("Identity is required (Auth Token, X-Guest-Email, or X-Guest-SessionID)");
    }
}
