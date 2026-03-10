package com.bilbo.store.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bilbo.store.dto.ShippingTypeDTO;
import com.bilbo.store.service.ShippingTypeService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ShippingTypeControllerTest {

    @Mock
    private ShippingTypeService shippingTypeService;

    @InjectMocks
    private ShippingTypeController controller;

    @Test
    void testGetActiveShippingTypes() {
        ShippingTypeDTO dto = new ShippingTypeDTO();
        when(shippingTypeService.getActiveShippingTypes()).thenReturn(List.of(dto));

        ResponseEntity<List<ShippingTypeDTO>> response = controller.getActiveShippingTypes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(shippingTypeService, times(1)).getActiveShippingTypes();
    }

    @Test
    void testGetAllShippingTypes() {
        ShippingTypeDTO dto = new ShippingTypeDTO();
        when(shippingTypeService.getAllShippingTypes()).thenReturn(List.of(dto));

        ResponseEntity<List<ShippingTypeDTO>> response = controller.getAllShippingTypes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(shippingTypeService, times(1)).getAllShippingTypes();
    }

    @Test
    void testCreateShippingType() {
        ShippingTypeDTO input = new ShippingTypeDTO();
        ShippingTypeDTO output = new ShippingTypeDTO();
        when(shippingTypeService.createShippingType(input)).thenReturn(output);

        ResponseEntity<ShippingTypeDTO> response = controller.createShippingType(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(output, response.getBody());
        verify(shippingTypeService, times(1)).createShippingType(input);
    }

    @Test
    void testUpdateShippingType() {
        UUID id = UUID.randomUUID();
        ShippingTypeDTO input = new ShippingTypeDTO();
        ShippingTypeDTO output = new ShippingTypeDTO();
        when(shippingTypeService.updateShippingType(id, input)).thenReturn(output);

        ResponseEntity<ShippingTypeDTO> response = controller.updateShippingType(id, input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(output, response.getBody());
        verify(shippingTypeService, times(1)).updateShippingType(id, input);
    }

    @Test
    void testDeleteShippingType() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> response = controller.deleteShippingType(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(shippingTypeService, times(1)).deleteShippingType(id);
    }
}
