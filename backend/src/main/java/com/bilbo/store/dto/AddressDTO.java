package com.bilbo.store.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AddressDTO {

    private UUID id;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String stateProvinceRegion;
    private String postalCode;
    private String country;
}
