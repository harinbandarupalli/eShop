package com.bilbo.store.service;

import com.bilbo.store.dto.ShippingTypeDTO;
import com.bilbo.store.entites.ShippingType;
import com.bilbo.store.mapper.ShippingTypeMapper;
import com.bilbo.store.repository.ShippingTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShippingTypeService {

    private final ShippingTypeRepository shippingTypeRepository;
    private final ShippingTypeMapper shippingTypeMapper;

    public List<ShippingTypeDTO> getActiveShippingTypes() {
        log.info("Fetching active shipping types");
        return shippingTypeRepository.findByIsActiveTrue().stream()
                .map(shippingTypeMapper::toDto)
                .toList();
    }

    public List<ShippingTypeDTO> getAllShippingTypes() {
        log.info("Fetching all shipping types");
        return shippingTypeRepository.findAll().stream()
                .map(shippingTypeMapper::toDto)
                .toList();
    }

    public ShippingTypeDTO createShippingType(ShippingTypeDTO shippingTypeDTO) {
        log.info("Creating new shipping type: {}", shippingTypeDTO.getName());
        ShippingType shippingType = shippingTypeMapper.toEntity(shippingTypeDTO);
        ShippingType saved = shippingTypeRepository.save(shippingType);
        return shippingTypeMapper.toDto(saved);
    }

    public ShippingTypeDTO updateShippingType(UUID id, ShippingTypeDTO shippingTypeDTO) {
        log.info("Updating shipping type with ID: {}", id);
        ShippingType existing = shippingTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Shipping type not found"));

        existing.setName(shippingTypeDTO.getName());
        existing.setDescription(shippingTypeDTO.getDescription());
        existing.setCost(shippingTypeDTO.getCost());
        existing.setIsActive(
                shippingTypeDTO.getIsActive() != null ? shippingTypeDTO.getIsActive() : existing.getIsActive());

        ShippingType updated = shippingTypeRepository.save(existing);
        return shippingTypeMapper.toDto(updated);
    }

    public void deleteShippingType(UUID id) {
        log.info("Deleting shipping type with ID: {}", id);
        shippingTypeRepository.deleteById(id);
    }
}
