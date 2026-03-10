package com.bilbo.store.controller;

import com.bilbo.store.dto.ShippingTypeDTO;
import com.bilbo.store.service.ShippingTypeService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipping-types")
@RequiredArgsConstructor
public class ShippingTypeController {

    private final ShippingTypeService shippingTypeService;

    @GetMapping
    public ResponseEntity<List<ShippingTypeDTO>> getActiveShippingTypes() {
        return ResponseEntity.ok(shippingTypeService.getActiveShippingTypes());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ShippingTypeDTO>> getAllShippingTypes() {
        return ResponseEntity.ok(shippingTypeService.getAllShippingTypes());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShippingTypeDTO> createShippingType(@RequestBody ShippingTypeDTO shippingTypeDTO) {
        return new ResponseEntity<>(shippingTypeService.createShippingType(shippingTypeDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShippingTypeDTO> updateShippingType(@PathVariable UUID id,
            @RequestBody ShippingTypeDTO shippingTypeDTO) {
        return ResponseEntity.ok(shippingTypeService.updateShippingType(id, shippingTypeDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteShippingType(@PathVariable UUID id) {
        shippingTypeService.deleteShippingType(id);
        return ResponseEntity.noContent().build();
    }
}
