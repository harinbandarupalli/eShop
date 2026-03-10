package com.bilbo.store.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.bilbo.store.dto.ShippingTypeDTO;
import com.bilbo.store.entites.ShippingType;
import com.bilbo.store.mapper.ShippingTypeMapper;
import com.bilbo.store.repository.ShippingTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShippingTypeServiceTest {

    @Mock
    private ShippingTypeRepository shippingTypeRepository;

    @Mock
    private ShippingTypeMapper shippingTypeMapper;

    @InjectMocks
    private ShippingTypeService service;

    @Test
    void testGetActiveShippingTypes() {
        ShippingType entity = new ShippingType();
        entity.setId(UUID.randomUUID());
        entity.setName("Standard");

        ShippingTypeDTO dto = new ShippingTypeDTO();
        dto.setId(entity.getId());
        dto.setName("Standard");

        when(shippingTypeRepository.findByIsActiveTrue()).thenReturn(List.of(entity));
        when(shippingTypeMapper.toDto(any(ShippingType.class))).thenReturn(dto);

        List<ShippingTypeDTO> result = service.getActiveShippingTypes();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Standard", result.get(0).getName());
        verify(shippingTypeRepository, times(1)).findByIsActiveTrue();
        verify(shippingTypeMapper, times(1)).toDto(any(ShippingType.class));
    }

    @Test
    void testGetAllShippingTypes() {
        ShippingType entity = new ShippingType();
        entity.setId(UUID.randomUUID());
        entity.setName("Standard");

        ShippingTypeDTO dto = new ShippingTypeDTO();
        dto.setId(entity.getId());
        dto.setName("Standard");

        when(shippingTypeRepository.findAll()).thenReturn(List.of(entity));
        when(shippingTypeMapper.toDto(any(ShippingType.class))).thenReturn(dto);

        List<ShippingTypeDTO> result = service.getAllShippingTypes();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Standard", result.get(0).getName());
        verify(shippingTypeRepository, times(1)).findAll();
        verify(shippingTypeMapper, times(1)).toDto(any(ShippingType.class));
    }

    @Test
    void testCreateShippingType() {
        ShippingTypeDTO dto = new ShippingTypeDTO();
        dto.setName("Standard");

        ShippingType entity = new ShippingType();
        entity.setName("Standard");

        ShippingType saved = new ShippingType();
        saved.setId(UUID.randomUUID());
        saved.setName("Standard");

        ShippingTypeDTO savedDto = new ShippingTypeDTO();
        savedDto.setId(saved.getId());
        savedDto.setName("Standard");

        when(shippingTypeMapper.toEntity(dto)).thenReturn(entity);
        when(shippingTypeRepository.save(entity)).thenReturn(saved);
        when(shippingTypeMapper.toDto(saved)).thenReturn(savedDto);

        ShippingTypeDTO result = service.createShippingType(dto);

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals("Standard", result.getName());
        verify(shippingTypeRepository, times(1)).save(entity);
    }

    @Test
    void testUpdateShippingType_Success() {
        UUID id = UUID.randomUUID();
        ShippingTypeDTO dto = new ShippingTypeDTO();
        dto.setName("Express");
        dto.setDescription("Next day delivery");
        dto.setCost(BigDecimal.valueOf(15.00));
        dto.setIsActive(false);

        ShippingType existing = new ShippingType();
        existing.setId(id);
        existing.setName("Standard");
        existing.setDescription("3-5 days");
        existing.setCost(BigDecimal.valueOf(5.00));
        existing.setIsActive(true);

        ShippingType updated = new ShippingType();
        updated.setId(id);
        updated.setName("Express");
        updated.setDescription("Next day delivery");
        updated.setCost(BigDecimal.valueOf(15.00));
        updated.setIsActive(false);

        ShippingTypeDTO updatedDto = new ShippingTypeDTO();
        updatedDto.setId(id);
        updatedDto.setName("Express");
        updatedDto.setDescription("Next day delivery");
        updatedDto.setCost(BigDecimal.valueOf(15.00));
        updatedDto.setIsActive(false);

        when(shippingTypeRepository.findById(id)).thenReturn(Optional.of(existing));
        when(shippingTypeRepository.save(existing)).thenReturn(updated);
        when(shippingTypeMapper.toDto(updated)).thenReturn(updatedDto);

        ShippingTypeDTO result = service.updateShippingType(id, dto);

        assertNotNull(result);
        assertEquals("Express", result.getName());
        assertEquals(BigDecimal.valueOf(15.00), result.getCost());
        assertFalse(result.getIsActive());
        verify(shippingTypeRepository, times(1)).findById(id);
        verify(shippingTypeRepository, times(1)).save(existing);
    }

    @Test
    void testUpdateShippingType_NotFound() {
        UUID id = UUID.randomUUID();
        ShippingTypeDTO dto = new ShippingTypeDTO();
        dto.setName("Express");

        when(shippingTypeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateShippingType(id, dto));
        verify(shippingTypeRepository, times(1)).findById(id);
        verify(shippingTypeRepository, never()).save(any(ShippingType.class));
    }

    @Test
    void testDeleteShippingType() {
        UUID id = UUID.randomUUID();

        service.deleteShippingType(id);

        verify(shippingTypeRepository, times(1)).deleteById(id);
    }
}
