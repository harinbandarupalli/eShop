package com.bilbo.store.mapper;

import com.bilbo.store.dto.CartItemDTO;
import com.bilbo.store.entites.Cart;
import com.bilbo.store.entites.CartItem;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-09T16:56:19-0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Homebrew)"
)
@Component
public class CartItemMapperImpl implements CartItemMapper {

    @Autowired
    private ProductBagMapper productBagMapper;

    @Override
    public CartItemDTO toDto(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }

        CartItemDTO cartItemDTO = new CartItemDTO();

        cartItemDTO.setCartId( cartItemCartId( cartItem ) );
        cartItemDTO.setBag( productBagMapper.toDto( cartItem.getBag() ) );
        cartItemDTO.setId( cartItem.getId() );
        cartItemDTO.setQuantity( cartItem.getQuantity() );

        return cartItemDTO;
    }

    @Override
    public CartItem toEntity(CartItemDTO dto) {
        if ( dto == null ) {
            return null;
        }

        CartItem cartItem = new CartItem();

        cartItem.setQuantity( dto.getQuantity() );

        return cartItem;
    }

    private UUID cartItemCartId(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }
        Cart cart = cartItem.getCart();
        if ( cart == null ) {
            return null;
        }
        UUID id = cart.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
