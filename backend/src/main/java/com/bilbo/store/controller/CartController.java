package com.bilbo.store.controller;

import com.bilbo.store.dto.CartDTO;
import com.bilbo.store.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/my-cart")
    public ResponseEntity<CartDTO> getMyCart(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Email", required = false) String guestEmail,
            @RequestHeader(value = "X-Guest-SessionID", required = false) String guestSessionId) {

        String email = resolveEmail(authentication, guestEmail);
        return ResponseEntity.ok(cartService.getOrCreateCart(email, guestSessionId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItemToCart(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Email", required = false) String guestEmail,
            @RequestHeader(value = "X-Guest-SessionID", required = false) String guestSessionId,
            @RequestParam UUID bagId,
            @RequestParam(defaultValue = "1") Integer quantity) {

        String email = resolveEmail(authentication, guestEmail);
        return ResponseEntity.ok(cartService.addItemToCart(email, guestSessionId, bagId, quantity));
    }

    @PutMapping("/items/{bagId}")
    public ResponseEntity<CartDTO> updateItemQuantity(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Email", required = false) String guestEmail,
            @RequestHeader(value = "X-Guest-SessionID", required = false) String guestSessionId,
            @PathVariable UUID bagId,
            @RequestParam Integer quantity) {

        String email = resolveEmail(authentication, guestEmail);
        return ResponseEntity.ok(cartService.updateItemQuantity(email, guestSessionId, bagId, quantity));
    }

    @DeleteMapping("/items/{bagId}")
    public ResponseEntity<CartDTO> removeItemFromCart(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Email", required = false) String guestEmail,
            @RequestHeader(value = "X-Guest-SessionID", required = false) String guestSessionId,
            @PathVariable UUID bagId) {

        String email = resolveEmail(authentication, guestEmail);
        return ResponseEntity.ok(cartService.removeItemFromCart(email, guestSessionId, bagId));
    }

    @PostMapping("/merge")
    public ResponseEntity<CartDTO> mergeCart(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-SessionID") String guestSessionId) {

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            return ResponseEntity.status(401).build(); // Only authenticated users can merge into their account
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        // Assuming the JWT contains the email claim. If not, it needs to be fetched
        // from DB via user sub.
        String email = calculateEmailFromJwtOrThrow(jwt);
        return ResponseEntity.ok(cartService.mergeCart(email, guestSessionId));
    }

    private String resolveEmail(Authentication authentication, String guestEmail) {
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            return calculateEmailFromJwtOrThrow((Jwt) authentication.getPrincipal());
        }
        return guestEmail; // Fall back to guestEmail if not authenticated
    }

    // Simplification: In a real app we might query the user table by 'sub' to get
    // the email,
    // but JWT usually contains email claim.
    private String calculateEmailFromJwtOrThrow(Jwt jwt) {
        if (jwt.hasClaim("email")) {
            return jwt.getClaimAsString("email");
        }
        // If your keycloak config requires it, fetch by sub through user service.
        throw new IllegalArgumentException("JWT missing email claim");
    }
}
