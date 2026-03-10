package com.bilbo.store.mapper;

import com.bilbo.store.dto.CartDTO;
import com.bilbo.store.dto.CartItemDTO;
import com.bilbo.store.entites.Cart;
import com.bilbo.store.entites.CartItem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-09T16:56:19-0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Homebrew)"
)
@Component
public class CartMapperImpl implements CartMapper {

    @Autowired
    private CartItemMapper cartItemMapper;

    @Override
    public CartDTO toDto(Cart cart) {
        if ( cart == null ) {
            return null;
        }

        CartDTO cartDTO = new CartDTO();

        cartDTO.setId( cart.getId() );
        cartDTO.setEmail( cart.getEmail() );
        cartDTO.setSessionId( cart.getSessionId() );
        cartDTO.setStatus( cart.getStatus() );
        cartDTO.setItems( cartItemListToCartItemDTOList( cart.getItems() ) );

        return cartDTO;
    }

    @Override
    public Cart toEntity(CartDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Cart cart = new Cart();

        cart.setEmail( dto.getEmail() );
        cart.setSessionId( dto.getSessionId() );
        cart.setStatus( dto.getStatus() );

        return cart;
    }

    protected List<CartItemDTO> cartItemListToCartItemDTOList(List<CartItem> list) {
        if ( list == null ) {
            return null;
        }

        List<CartItemDTO> list1 = new ArrayList<CartItemDTO>( list.size() );
        for ( CartItem cartItem : list ) {
            list1.add( cartItemMapper.toDto( cartItem ) );
        }

        return list1;
    }
}
