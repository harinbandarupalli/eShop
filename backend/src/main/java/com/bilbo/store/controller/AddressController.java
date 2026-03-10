package com.bilbo.store.controller;

import com.bilbo.store.dto.AddressDTO;
import com.bilbo.store.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<List<AddressDTO>> getAddresses(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Email", required = false) String guestEmail) {
        String email = resolveEmail(authentication, guestEmail);
        return ResponseEntity.ok(addressService.getAddressesByEmail(email));
    }

    @PostMapping
    public ResponseEntity<AddressDTO> createAddress(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Email", required = false) String guestEmail,
            @RequestBody AddressDTO addressDTO) {
        String email = resolveEmail(authentication, guestEmail);
        AddressDTO created = addressService.createAddress(email, addressDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressDTO> updateAddress(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Email", required = false) String guestEmail,
            @PathVariable UUID id,
            @RequestBody AddressDTO addressDTO) {
        String email = resolveEmail(authentication, guestEmail);
        return ResponseEntity.ok(addressService.updateAddress(email, id, addressDTO));
    }

    @PutMapping("/{id}/default")
    public ResponseEntity<AddressDTO> setDefaultAddress(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Email", required = false) String guestEmail,
            @PathVariable UUID id) {
        String email = resolveEmail(authentication, guestEmail);
        return ResponseEntity.ok(addressService.setDefaultAddress(email, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Email", required = false) String guestEmail,
            @PathVariable UUID id) {
        String email = resolveEmail(authentication, guestEmail);
        addressService.deleteAddress(email, id);
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
