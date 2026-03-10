package com.bilbo.store.mapper;

import com.bilbo.store.dto.PaymentMethodDTO;
import com.bilbo.store.entites.Address;
import com.bilbo.store.entites.PaymentMethod;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-09T16:56:19-0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Homebrew)"
)
@Component
public class PaymentMethodMapperImpl implements PaymentMethodMapper {

    @Override
    public PaymentMethodDTO toDto(PaymentMethod paymentMethod) {
        if ( paymentMethod == null ) {
            return null;
        }

        PaymentMethodDTO paymentMethodDTO = new PaymentMethodDTO();

        paymentMethodDTO.setBillingAddressId( paymentMethodBillingAddressId( paymentMethod ) );
        paymentMethodDTO.setId( paymentMethod.getId() );
        paymentMethodDTO.setEmail( paymentMethod.getEmail() );
        paymentMethodDTO.setCardholderName( paymentMethod.getCardholderName() );
        paymentMethodDTO.setLast4( paymentMethod.getLast4() );
        paymentMethodDTO.setExpiryMonth( paymentMethod.getExpiryMonth() );
        paymentMethodDTO.setExpiryYear( paymentMethod.getExpiryYear() );
        paymentMethodDTO.setCardType( paymentMethod.getCardType() );
        paymentMethodDTO.setIsDefault( paymentMethod.getIsDefault() );

        return paymentMethodDTO;
    }

    @Override
    public PaymentMethod toEntity(PaymentMethodDTO dto) {
        if ( dto == null ) {
            return null;
        }

        PaymentMethod paymentMethod = new PaymentMethod();

        paymentMethod.setEmail( dto.getEmail() );
        paymentMethod.setCardholderName( dto.getCardholderName() );
        paymentMethod.setLast4( dto.getLast4() );
        paymentMethod.setExpiryMonth( dto.getExpiryMonth() );
        paymentMethod.setExpiryYear( dto.getExpiryYear() );
        paymentMethod.setCardType( dto.getCardType() );
        paymentMethod.setIsDefault( dto.getIsDefault() );

        return paymentMethod;
    }

    private UUID paymentMethodBillingAddressId(PaymentMethod paymentMethod) {
        if ( paymentMethod == null ) {
            return null;
        }
        Address billingAddress = paymentMethod.getBillingAddress();
        if ( billingAddress == null ) {
            return null;
        }
        UUID id = billingAddress.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
