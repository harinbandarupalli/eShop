package com.bilbo.store.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class AddressDTO {

    private UUID id;
    private String userId;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String stateProvinceRegion;
    private String postalCode;
    private String country;
}
