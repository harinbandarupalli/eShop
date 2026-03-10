package com.bilbo.store.service;

import com.bilbo.store.dto.AddressDTO;
import com.bilbo.store.entites.Address;
import com.bilbo.store.mapper.AddressMapper;
import com.bilbo.store.repository.AddressRepository;
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
public class AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    public List<AddressDTO> getAddressesByEmail(String email) {
        log.info("Fetching addresses for email: {}", email);
        return addressRepository.findByEmail(email).stream()
                .map(addressMapper::toDto)
                .toList();
    }

    @Transactional
    public AddressDTO createAddress(String email, AddressDTO addressDTO) {
        log.info("Creating new address for email: {}", email);
        Address address = addressMapper.toEntity(addressDTO);
        address.setEmail(email);
        address.setCreatedBy(email);
        address.setCreatedTimestamp(OffsetDateTime.now());

        if (address.getIsDefault() != null && address.getIsDefault()) {
            clearPreviousDefault(email);
        } else if (addressRepository.findByEmail(email).isEmpty()) {
            // Auto default if first address
            address.setIsDefault(true);
        }

        Address savedAddress = addressRepository.save(address);
        return addressMapper.toDto(savedAddress);
    }

    @Transactional
    public AddressDTO updateAddress(String email, UUID id, AddressDTO addressDTO) {
        log.info("Updating address {} for email: {}", id, email);
        Address existingAddress = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        if (!existingAddress.getEmail().equals(email)) {
            throw new EntityNotFoundException("Address not found");
        }

        existingAddress.setAddressLine1(addressDTO.getAddressLine1());
        existingAddress.setAddressLine2(addressDTO.getAddressLine2());
        existingAddress.setCity(addressDTO.getCity());
        existingAddress.setStateProvinceRegion(addressDTO.getStateProvinceRegion());
        existingAddress.setPostalCode(addressDTO.getPostalCode());
        existingAddress.setCountry(addressDTO.getCountry());

        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault() && !existingAddress.getIsDefault()) {
            clearPreviousDefault(email);
            existingAddress.setIsDefault(true);
        }

        existingAddress.setLastUpdatedBy(email);
        existingAddress.setLastUpdatedTimestamp(OffsetDateTime.now());

        Address savedAddress = addressRepository.save(existingAddress);
        return addressMapper.toDto(savedAddress);
    }

    @Transactional
    public AddressDTO setDefaultAddress(String email, UUID id) {
        log.info("Setting address {} as default for email: {}", id, email);
        Address newDefault = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        if (!newDefault.getEmail().equals(email)) {
            throw new EntityNotFoundException("Address not found");
        }

        clearPreviousDefault(email);

        newDefault.setIsDefault(true);
        newDefault.setLastUpdatedBy(email);
        newDefault.setLastUpdatedTimestamp(OffsetDateTime.now());

        return addressMapper.toDto(addressRepository.save(newDefault));
    }

    @Transactional
    public void deleteAddress(String email, UUID id) {
        log.info("Deleting address {} for email: {}", id, email);
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        if (!address.getEmail().equals(email)) {
            throw new EntityNotFoundException("Address not found");
        }

        addressRepository.deleteById(id);

        // If we deleted the default, set another one to default
        if (address.getIsDefault()) {
            List<Address> remaining = addressRepository.findByEmail(email);
            if (!remaining.isEmpty()) {
                Address replacement = remaining.get(0);
                replacement.setIsDefault(true);
                replacement.setLastUpdatedTimestamp(OffsetDateTime.now());
                addressRepository.save(replacement);
            }
        }
    }

    private void clearPreviousDefault(String email) {
        Optional<Address> currentDefault = addressRepository.findByEmailAndIsDefaultTrue(email);
        currentDefault.ifPresent(addr -> {
            addr.setIsDefault(false);
            addressRepository.save(addr);
        });
    }
}
