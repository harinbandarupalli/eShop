package com.bilbo.store.service;

import com.bilbo.store.dto.PaymentMethodDTO;
import com.bilbo.store.entites.Address;
import com.bilbo.store.entites.PaymentMethod;
import com.bilbo.store.mapper.PaymentMethodMapper;
import com.bilbo.store.repository.AddressRepository;
import com.bilbo.store.repository.PaymentMethodRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final AddressRepository addressRepository;
    private final PaymentMethodMapper paymentMethodMapper;

    public List<PaymentMethodDTO> getPaymentMethodsByEmail(String email) {
        log.info("Fetching payment methods for email: {}", email);
        return paymentMethodRepository.findByEmail(email).stream()
                .map(paymentMethodMapper::toDto)
                .toList();
    }

    @Transactional
    public PaymentMethodDTO createPaymentMethod(String email, PaymentMethodDTO paymentDTO) {
        log.info("Creating new payment method for email: {}", email);

        PaymentMethod paymentMethod = paymentMethodMapper.toEntity(paymentDTO);
        paymentMethod.setEmail(email);

        if (paymentDTO.getBillingAddressId() != null) {
            Address billingAddress = addressRepository.findById(paymentDTO.getBillingAddressId())
                    .orElseThrow(() -> new EntityNotFoundException("Billing address not found"));

            // Allow linking to ANY address? Probably restrict to same email:
            if (!billingAddress.getEmail().equals(email)) {
                throw new IllegalArgumentException("Billing address ownership mismatch");
            }
            paymentMethod.setBillingAddress(billingAddress);
        }

        paymentMethod.setCreatedBy(email);
        paymentMethod.setCreatedTimestamp(OffsetDateTime.now());

        // Mocking token assignment for this implementation
        paymentMethod.setToken(UUID.randomUUID().toString());

        if (paymentMethod.getIsDefault() != null && paymentMethod.getIsDefault()) {
            clearPreviousDefault(email);
        } else if (paymentMethodRepository.findByEmail(email).isEmpty()) {
            paymentMethod.setIsDefault(true);
        }

        PaymentMethod saved = paymentMethodRepository.save(paymentMethod);
        return paymentMethodMapper.toDto(saved);
    }

    @Transactional
    public void deletePaymentMethod(String email, UUID id) {
        log.info("Deleting payment method {} for email: {}", id, email);
        PaymentMethod pm = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment method not found"));

        if (!pm.getEmail().equals(email)) {
            throw new IllegalArgumentException("Payment method does not belong to the provided email");
        }

        paymentMethodRepository.deleteById(id);

        if (pm.getIsDefault()) {
            List<PaymentMethod> remaining = paymentMethodRepository.findByEmail(email);
            if (!remaining.isEmpty()) {
                PaymentMethod replacement = remaining.get(0);
                replacement.setIsDefault(true);
                replacement.setLastUpdatedTimestamp(OffsetDateTime.now());
                paymentMethodRepository.save(replacement);
            }
        }
    }

    private void clearPreviousDefault(String email) {
        Optional<PaymentMethod> currentDefault = paymentMethodRepository.findByEmailAndIsDefaultTrue(email);
        currentDefault.ifPresent(pm -> {
            pm.setIsDefault(false);
            paymentMethodRepository.save(pm);
        });
    }
}
