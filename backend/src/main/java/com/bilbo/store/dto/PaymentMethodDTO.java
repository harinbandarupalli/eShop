package com.bilbo.store.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class PaymentMethodDTO {
    private UUID id;
    private String token;
    private String userId;
    private String cardholderName;
    private String last4;
    private Integer expiryMonth;
    private Integer expiryYear;
    private String cardType;
    private UUID billingAddressId;
    private String billingZipcode;
}
