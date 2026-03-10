package com.bilbo.store.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.bilbo.store.dto.AddressDTO;
import com.bilbo.store.entites.Address;
import com.bilbo.store.mapper.AddressMapper;
import com.bilbo.store.repository.AddressRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressService service;

    private final String testEmail = "test@example.com";

    @Test
    void testGetAddressesByEmail() {
        Address entity = new Address();
        entity.setId(UUID.randomUUID());
        entity.setEmail(testEmail);

        AddressDTO dto = new AddressDTO();
        dto.setId(entity.getId());
        dto.setEmail(testEmail);

        when(addressRepository.findByEmail(testEmail)).thenReturn(List.of(entity));
        when(addressMapper.toDto(any(Address.class))).thenReturn(dto);

        List<AddressDTO> result = service.getAddressesByEmail(testEmail);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testEmail, result.get(0).getEmail());
        verify(addressRepository, times(1)).findByEmail(testEmail);
    }

    @Test
    void testCreateAddress_FirstAddressBecomesDefault() {
        AddressDTO dto = new AddressDTO();
        dto.setCity("Seattle");

        Address entity = new Address();
        entity.setCity("Seattle");

        Address saved = new Address();
        saved.setId(UUID.randomUUID());

        when(addressMapper.toEntity(dto)).thenReturn(entity);
        when(addressRepository.findByEmail(testEmail)).thenReturn(List.of());
        when(addressRepository.save(entity)).thenReturn(saved);
        when(addressMapper.toDto(saved)).thenReturn(dto);

        service.createAddress(testEmail, dto);

        assertTrue(entity.getIsDefault());
        verify(addressRepository, times(1)).save(entity);
    }

    @Test
    void testCreateAddress_SetsDefaultAndClearsPrevious() {
        AddressDTO dto = new AddressDTO();
        dto.setIsDefault(true);

        Address entity = new Address();
        entity.setIsDefault(true);

        Address prevDefault = new Address();
        prevDefault.setId(UUID.randomUUID());
        prevDefault.setIsDefault(true);

        Address saved = new Address();
        saved.setId(UUID.randomUUID());

        when(addressMapper.toEntity(dto)).thenReturn(entity);
        when(addressRepository.findByEmailAndIsDefaultTrue(testEmail)).thenReturn(Optional.of(prevDefault));
        when(addressRepository.save(entity)).thenReturn(saved);
        when(addressRepository.save(prevDefault)).thenReturn(prevDefault);
        when(addressMapper.toDto(saved)).thenReturn(dto);

        service.createAddress(testEmail, dto);

        assertFalse(prevDefault.getIsDefault());
        verify(addressRepository, times(1)).save(prevDefault);
        verify(addressRepository, times(1)).save(entity);
    }

    @Test
    void testUpdateAddress_Success() {
        UUID id = UUID.randomUUID();
        AddressDTO dto = new AddressDTO();
        dto.setCity("Portland");

        Address existing = new Address();
        existing.setId(id);
        existing.setEmail(testEmail);
        existing.setCity("Seattle");
        existing.setIsDefault(false);

        Address saved = new Address();

        when(addressRepository.findById(id)).thenReturn(Optional.of(existing));
        when(addressRepository.save(existing)).thenReturn(saved);
        when(addressMapper.toDto(saved)).thenReturn(dto);

        service.updateAddress(testEmail, id, dto);

        assertEquals("Portland", existing.getCity());
        verify(addressRepository, times(1)).save(existing);
    }

    @Test
    void testUpdateAddress_OwnershipMismatch() {
        UUID id = UUID.randomUUID();
        AddressDTO dto = new AddressDTO();

        Address existing = new Address();
        existing.setId(id);
        existing.setEmail("other@example.com");

        when(addressRepository.findById(id)).thenReturn(Optional.of(existing));

        assertThrows(EntityNotFoundException.class, () -> service.updateAddress(testEmail, id, dto));
        verify(addressRepository, never()).save(any());
    }

    @Test
    void testSetDefaultAddress_Success() {
        UUID id = UUID.randomUUID();
        Address target = new Address();
        target.setId(id);
        target.setEmail(testEmail);
        target.setIsDefault(false);

        Address prevDefault = new Address();
        prevDefault.setId(UUID.randomUUID());
        prevDefault.setIsDefault(true);

        AddressDTO dto = new AddressDTO();

        when(addressRepository.findById(id)).thenReturn(Optional.of(target));
        when(addressRepository.findByEmailAndIsDefaultTrue(testEmail)).thenReturn(Optional.of(prevDefault));
        when(addressRepository.save(prevDefault)).thenReturn(prevDefault);
        when(addressRepository.save(target)).thenReturn(target);
        when(addressMapper.toDto(target)).thenReturn(dto);

        service.setDefaultAddress(testEmail, id);

        assertTrue(target.getIsDefault());
        assertFalse(prevDefault.getIsDefault());
        verify(addressRepository, times(1)).save(prevDefault);
        verify(addressRepository, times(1)).save(target);
    }

    @Test
    void testDeleteAddress_SuccessNotDefault() {
        UUID id = UUID.randomUUID();
        Address address = new Address();
        address.setId(id);
        address.setEmail(testEmail);
        address.setIsDefault(false);

        when(addressRepository.findById(id)).thenReturn(Optional.of(address));

        service.deleteAddress(testEmail, id);

        verify(addressRepository, times(1)).deleteById(id);
        verify(addressRepository, never()).findByEmail(testEmail);
    }

    @Test
    void testDeleteAddress_SuccessIsDefaultReassigns() {
        UUID id = UUID.randomUUID();
        Address address = new Address();
        address.setId(id);
        address.setEmail(testEmail);
        address.setIsDefault(true);

        Address replacement = new Address();
        replacement.setId(UUID.randomUUID());
        replacement.setIsDefault(false);

        when(addressRepository.findById(id)).thenReturn(Optional.of(address));
        when(addressRepository.findByEmail(testEmail)).thenReturn(List.of(replacement));

        service.deleteAddress(testEmail, id);

        verify(addressRepository, times(1)).deleteById(id);
        assertTrue(replacement.getIsDefault());
        verify(addressRepository, times(1)).save(replacement);
    }
}
