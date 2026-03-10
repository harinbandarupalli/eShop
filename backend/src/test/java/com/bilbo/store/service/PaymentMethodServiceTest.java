package com.bilbo.store.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.bilbo.store.dto.PaymentMethodDTO;
import com.bilbo.store.entites.Address;
import com.bilbo.store.entites.PaymentMethod;
import com.bilbo.store.mapper.PaymentMethodMapper;
import com.bilbo.store.repository.AddressRepository;
import com.bilbo.store.repository.PaymentMethodRepository;
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
class PaymentMethodServiceTest {

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private PaymentMethodMapper paymentMethodMapper;

    @InjectMocks
    private PaymentMethodService service;

    private final String testEmail = "test@example.com";

    @Test
    void testGetPaymentMethodsByEmail() {
        PaymentMethod entity = new PaymentMethod();
        entity.setId(UUID.randomUUID());
        entity.setEmail(testEmail);

        PaymentMethodDTO dto = new PaymentMethodDTO();
        dto.setId(entity.getId());
        dto.setEmail(testEmail);

        when(paymentMethodRepository.findByEmail(testEmail)).thenReturn(List.of(entity));
        when(paymentMethodMapper.toDto(any(PaymentMethod.class))).thenReturn(dto);

        List<PaymentMethodDTO> result = service.getPaymentMethodsByEmail(testEmail);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testEmail, result.get(0).getEmail());
        verify(paymentMethodRepository, times(1)).findByEmail(testEmail);
    }

    @Test
    void testCreatePaymentMethod_Basic() {
        PaymentMethodDTO dto = new PaymentMethodDTO();
        dto.setCardType("Visa");

        PaymentMethod entity = new PaymentMethod();
        entity.setCardType("Visa");

        PaymentMethod saved = new PaymentMethod();
        saved.setId(UUID.randomUUID());
        saved.setCardType("Visa");
        saved.setEmail(testEmail);

        PaymentMethodDTO savedDto = new PaymentMethodDTO();
        savedDto.setId(saved.getId());
        savedDto.setCardType("Visa");
        savedDto.setEmail(testEmail);

        when(paymentMethodMapper.toEntity(dto)).thenReturn(entity);
        when(paymentMethodRepository.findByEmail(testEmail)).thenReturn(List.of());
        when(paymentMethodRepository.save(entity)).thenReturn(saved);
        when(paymentMethodMapper.toDto(saved)).thenReturn(savedDto);

        PaymentMethodDTO result = service.createPaymentMethod(testEmail, dto);

        assertNotNull(result);
        assertEquals("Visa", result.getCardType());
        assertTrue(entity.getIsDefault()); // Because no existing methods
        assertNotNull(entity.getToken());
        verify(paymentMethodRepository, times(1)).save(entity);
    }

    @Test
    void testCreatePaymentMethod_WithValidBillingAddress() {
        UUID addressId = UUID.randomUUID();
        PaymentMethodDTO dto = new PaymentMethodDTO();
        dto.setBillingAddressId(addressId);

        PaymentMethod entity = new PaymentMethod();

        Address billingAddress = new Address();
        billingAddress.setId(addressId);
        billingAddress.setEmail(testEmail);

        PaymentMethod saved = new PaymentMethod();
        saved.setId(UUID.randomUUID());

        PaymentMethodDTO savedDto = new PaymentMethodDTO();
        savedDto.setId(saved.getId());

        when(paymentMethodMapper.toEntity(dto)).thenReturn(entity);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(billingAddress));
        when(paymentMethodRepository.findByEmail(testEmail)).thenReturn(List.of());
        when(paymentMethodRepository.save(entity)).thenReturn(saved);
        when(paymentMethodMapper.toDto(saved)).thenReturn(savedDto);

        service.createPaymentMethod(testEmail, dto);

        assertEquals(billingAddress, entity.getBillingAddress());
        verify(addressRepository, times(1)).findById(addressId);
    }

    @Test
    void testCreatePaymentMethod_BillingAddressOwnershipMismatch() {
        UUID addressId = UUID.randomUUID();
        PaymentMethodDTO dto = new PaymentMethodDTO();
        dto.setBillingAddressId(addressId);

        PaymentMethod entity = new PaymentMethod();

        Address billingAddress = new Address();
        billingAddress.setId(addressId);
        billingAddress.setEmail("other@example.com");

        when(paymentMethodMapper.toEntity(dto)).thenReturn(entity);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(billingAddress));

        assertThrows(IllegalArgumentException.class, () -> service.createPaymentMethod(testEmail, dto));
        verify(paymentMethodRepository, never()).save(any());
    }

    @Test
    void testCreatePaymentMethod_SetsDefaultAndClearsPrevious() {
        PaymentMethodDTO dto = new PaymentMethodDTO();
        dto.setCardType("Visa");

        PaymentMethod entity = new PaymentMethod();
        entity.setIsDefault(true);

        PaymentMethod prevDefault = new PaymentMethod();
        prevDefault.setId(UUID.randomUUID());
        prevDefault.setIsDefault(true);

        PaymentMethod saved = new PaymentMethod();
        saved.setId(UUID.randomUUID());

        PaymentMethodDTO savedDto = new PaymentMethodDTO();
        savedDto.setId(saved.getId());

        when(paymentMethodMapper.toEntity(dto)).thenReturn(entity);
        when(paymentMethodRepository.findByEmailAndIsDefaultTrue(testEmail)).thenReturn(Optional.of(prevDefault));
        when(paymentMethodRepository.save(entity)).thenReturn(saved);
        when(paymentMethodRepository.save(prevDefault)).thenReturn(prevDefault);
        when(paymentMethodMapper.toDto(saved)).thenReturn(savedDto);

        service.createPaymentMethod(testEmail, dto);

        assertFalse(prevDefault.getIsDefault());
        verify(paymentMethodRepository, times(1)).save(prevDefault);
        verify(paymentMethodRepository, times(1)).save(entity);
    }

    @Test
    void testDeletePaymentMethod_SuccessNotDefault() {
        UUID id = UUID.randomUUID();
        PaymentMethod pm = new PaymentMethod();
        pm.setId(id);
        pm.setEmail(testEmail);
        pm.setIsDefault(false);

        when(paymentMethodRepository.findById(id)).thenReturn(Optional.of(pm));

        service.deletePaymentMethod(testEmail, id);

        verify(paymentMethodRepository, times(1)).deleteById(id);
        verify(paymentMethodRepository, never()).findByEmail(testEmail); // Assuming it doesn't try to reassign default
    }

    @Test
    void testDeletePaymentMethod_SuccessIsDefault() {
        UUID id = UUID.randomUUID();
        PaymentMethod pm = new PaymentMethod();
        pm.setId(id);
        pm.setEmail(testEmail);
        pm.setIsDefault(true);

        PaymentMethod replacement = new PaymentMethod();
        replacement.setId(UUID.randomUUID());
        replacement.setIsDefault(false);

        when(paymentMethodRepository.findById(id)).thenReturn(Optional.of(pm));
        when(paymentMethodRepository.findByEmail(testEmail)).thenReturn(List.of(replacement));

        service.deletePaymentMethod(testEmail, id);

        verify(paymentMethodRepository, times(1)).deleteById(id);
        assertTrue(replacement.getIsDefault());
        verify(paymentMethodRepository, times(1)).save(replacement);
    }

    @Test
    void testDeletePaymentMethod_OwnershipMismatch() {
        UUID id = UUID.randomUUID();
        PaymentMethod pm = new PaymentMethod();
        pm.setId(id);
        pm.setEmail("other@example.com");

        when(paymentMethodRepository.findById(id)).thenReturn(Optional.of(pm));

        assertThrows(IllegalArgumentException.class, () -> service.deletePaymentMethod(testEmail, id));
        verify(paymentMethodRepository, never()).deleteById(any());
    }

    @Test
    void testDeletePaymentMethod_NotFound() {
        UUID id = UUID.randomUUID();
        when(paymentMethodRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.deletePaymentMethod(testEmail, id));
    }
}
