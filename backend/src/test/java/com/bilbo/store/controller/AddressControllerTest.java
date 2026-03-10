package com.bilbo.store.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bilbo.store.dto.AddressDTO;
import com.bilbo.store.service.AddressService;
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
class AddressControllerTest {

    @Mock
    private AddressService addressService;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private AddressController controller;

    private final String authEmail = "auth@example.com";
    private final String guestEmail = "guest@example.com";

    @BeforeEach
    void setUp() {
        lenient().when(authentication.getPrincipal()).thenReturn(jwt);
    }

    @Test
    void testGetAddresses_Auth() {
        when(jwt.hasClaim("email")).thenReturn(true);
        when(jwt.getClaimAsString("email")).thenReturn(authEmail);

        AddressDTO dto = new AddressDTO();
        when(addressService.getAddressesByEmail(authEmail)).thenReturn(List.of(dto));

        ResponseEntity<List<AddressDTO>> response = controller.getAddresses(authentication, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(addressService, times(1)).getAddressesByEmail(authEmail);
    }

    @Test
    void testGetAddresses_Guest() {
        AddressDTO dto = new AddressDTO();
        when(addressService.getAddressesByEmail(guestEmail)).thenReturn(List.of(dto));

        ResponseEntity<List<AddressDTO>> response = controller.getAddresses(null, guestEmail);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(addressService, times(1)).getAddressesByEmail(guestEmail);
    }

    @Test
    void testCreateAddress() {
        when(jwt.hasClaim("email")).thenReturn(true);
        when(jwt.getClaimAsString("email")).thenReturn(authEmail);

        AddressDTO input = new AddressDTO();
        AddressDTO output = new AddressDTO();
        when(addressService.createAddress(authEmail, input)).thenReturn(output);

        ResponseEntity<AddressDTO> response = controller.createAddress(authentication, null, input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(output, response.getBody());
    }

    @Test
    void testUpdateAddress() {
        UUID id = UUID.randomUUID();
        AddressDTO input = new AddressDTO();
        AddressDTO output = new AddressDTO();
        when(addressService.updateAddress(guestEmail, id, input)).thenReturn(output);

        ResponseEntity<AddressDTO> response = controller.updateAddress(null, guestEmail, id, input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(output, response.getBody());
    }

    @Test
    void testSetDefaultAddress() {
        UUID id = UUID.randomUUID();
        AddressDTO output = new AddressDTO();
        when(addressService.setDefaultAddress(guestEmail, id)).thenReturn(output);

        ResponseEntity<AddressDTO> response = controller.setDefaultAddress(null, guestEmail, id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(output, response.getBody());
    }

    @Test
    void testDeleteAddress() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> response = controller.deleteAddress(null, guestEmail, id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(addressService, times(1)).deleteAddress(guestEmail, id);
    }

    @Test
    void testResolveEmail_NoAuthNoGuest() {
        AddressDTO dto = new AddressDTO();
        assertThrows(IllegalArgumentException.class, () -> controller.createAddress(null, null, dto));
        assertThrows(IllegalArgumentException.class, () -> controller.createAddress(null, "", dto));
    }
}
