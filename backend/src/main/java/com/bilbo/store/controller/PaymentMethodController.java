package com.bilbo.store.controller;

import com.bilbo.store.dto.PaymentMethodDTO;
import com.bilbo.store.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @GetMapping
    public ResponseEntity<List<PaymentMethodDTO>> getPaymentMethods(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Email", required = false) String guestEmail) {
        String email = resolveEmail(authentication, guestEmail);
        return ResponseEntity.ok(paymentMethodService.getPaymentMethodsByEmail(email));
    }

    @PostMapping
    public ResponseEntity<PaymentMethodDTO> createPaymentMethod(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Email", required = false) String guestEmail,
            @RequestBody PaymentMethodDTO paymentMethodDTO) {
        String email = resolveEmail(authentication, guestEmail);
        PaymentMethodDTO created = paymentMethodService.createPaymentMethod(email, paymentMethodDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentMethod(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Email", required = false) String guestEmail,
            @PathVariable UUID id) {
        String email = resolveEmail(authentication, guestEmail);
        paymentMethodService.deletePaymentMethod(email, id);
        return ResponseEntity.noContent().build();
    }

    private String resolveEmail(Authentication authentication, String guestEmail) {
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            if (jwt.hasClaim("email")) {
                return jwt.getClaimAsString("email");
            }
        }
        if (guestEmail == null || guestEmail.isBlank()) {
            throw new IllegalArgumentException("Email is required (either via Auth Token or X-Guest-Email header)");
        }
        return guestEmail;
    }
}
