package com.bilbo.store.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.bilbo.store.dto.CartDTO;
import com.bilbo.store.entites.Cart;
import com.bilbo.store.entites.CartItem;
import com.bilbo.store.entites.ProductBag;
import com.bilbo.store.mapper.CartMapper;
import com.bilbo.store.repository.CartItemRepository;
import com.bilbo.store.repository.CartRepository;
import com.bilbo.store.repository.ProductBagRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductBagRepository productBagRepository;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartService service;

    private final String testEmail = "test@example.com";
    private final String testSessionId = "session123";

    @Test
    void testGetOrCreateCart_Existing() {
        Cart cart = new Cart();
        cart.setId(UUID.randomUUID());
        cart.setEmail(testEmail);

        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());

        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.of(cart));
        when(cartMapper.toDto(cart)).thenReturn(dto);

        CartDTO result = service.getOrCreateCart(testEmail, null);

        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void testGetOrCreateCart_New() {
        Cart saved = new Cart();
        saved.setId(UUID.randomUUID());

        CartDTO dto = new CartDTO();
        dto.setId(saved.getId());

        when(cartRepository.findBySessionIdAndStatus(testSessionId, "ACTIVE")).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(saved);
        when(cartMapper.toDto(saved)).thenReturn(dto);

        CartDTO result = service.getOrCreateCart(null, testSessionId);

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testAddItemToCart_ExistingItem() {
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart();
        cart.setId(cartId);

        UUID bagId = UUID.randomUUID();
        ProductBag bag = new ProductBag();
        bag.setId(bagId);
        bag.setIsActive(true);

        CartItem existingItem = new CartItem();
        existingItem.setId(UUID.randomUUID());
        existingItem.setQuantity(2);

        CartDTO dto = new CartDTO();

        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.of(cart));
        when(productBagRepository.findById(bagId)).thenReturn(Optional.of(bag));
        when(cartItemRepository.findByCartIdAndBagId(cartId, bagId)).thenReturn(Optional.of(existingItem));
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartMapper.toDto(cart)).thenReturn(dto);

        service.addItemToCart(testEmail, null, bagId, 3);

        assertEquals(5, existingItem.getQuantity());
        verify(cartItemRepository, times(1)).save(existingItem);
    }

    @Test
    void testAddItemToCart_NewItem() {
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart();
        cart.setId(cartId);

        UUID bagId = UUID.randomUUID();
        ProductBag bag = new ProductBag();
        bag.setId(bagId);
        bag.setIsActive(true);

        CartDTO dto = new CartDTO();

        when(cartRepository.findBySessionIdAndStatus(testSessionId, "ACTIVE")).thenReturn(Optional.of(cart));
        when(productBagRepository.findById(bagId)).thenReturn(Optional.of(bag));
        when(cartItemRepository.findByCartIdAndBagId(cartId, bagId)).thenReturn(Optional.empty());
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartMapper.toDto(cart)).thenReturn(dto);

        service.addItemToCart(null, testSessionId, bagId, 2);

        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void testAddItemToCart_InactiveBag() {
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart();
        cart.setId(cartId);

        UUID bagId = UUID.randomUUID();
        ProductBag bag = new ProductBag();
        bag.setId(bagId);
        bag.setIsActive(false);

        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.of(cart));
        when(productBagRepository.findById(bagId)).thenReturn(Optional.of(bag));

        assertThrows(IllegalArgumentException.class, () -> service.addItemToCart(testEmail, null, bagId, 1));
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    void testUpdateItemQuantity_Update() {
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart();
        cart.setId(cartId);

        UUID bagId = UUID.randomUUID();
        CartItem item = new CartItem();
        item.setId(UUID.randomUUID());
        item.setQuantity(2);

        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndBagId(cartId, bagId)).thenReturn(Optional.of(item));
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        service.updateItemQuantity(testEmail, null, bagId, 5);

        assertEquals(5, item.getQuantity());
        verify(cartItemRepository, times(1)).save(item);
    }

    @Test
    void testUpdateItemQuantity_Delete() {
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart();
        cart.setId(cartId);

        UUID bagId = UUID.randomUUID();
        CartItem item = new CartItem();
        item.setId(UUID.randomUUID());

        when(cartRepository.findBySessionIdAndStatus(testSessionId, "ACTIVE")).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndBagId(cartId, bagId)).thenReturn(Optional.of(item));
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        service.updateItemQuantity(null, testSessionId, bagId, 0);

        verify(cartItemRepository, times(1)).delete(item);
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    void testRemoveItemFromCart() {
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart();
        cart.setId(cartId);

        UUID bagId = UUID.randomUUID();

        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.of(cart));
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        service.removeItemFromCart(testEmail, null, bagId);

        verify(cartItemRepository, times(1)).deleteByCartIdAndBagId(cartId, bagId);
    }

    @Test
    void testMergeCart_Success() {
        Cart guestCart = new Cart();
        guestCart.setId(UUID.randomUUID());
        guestCart.setSessionId(testSessionId);

        // Add items to guest cart
        CartItem guestItem = new CartItem();
        ProductBag b1 = new ProductBag();
        b1.setId(UUID.randomUUID());
        guestItem.setBag(b1);
        guestItem.setQuantity(2);
        guestCart.setItems(List.of(guestItem));

        Cart userCart = new Cart();
        userCart.setId(UUID.randomUUID());
        userCart.setEmail(testEmail);

        CartItem userItem = new CartItem();
        userItem.setBag(b1);
        userItem.setQuantity(3);
        userCart.setItems(new ArrayList<>(List.of(userItem))); // Note: Need modifiable list normally, but Optional
                                                               // handles this

        when(cartRepository.findBySessionIdAndStatus(testSessionId, "ACTIVE")).thenReturn(Optional.of(guestCart));
        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.of(userCart));
        when(cartItemRepository.findByCartIdAndBagId(userCart.getId(), b1.getId())).thenReturn(Optional.of(userItem));
        when(cartRepository.findById(userCart.getId())).thenReturn(Optional.of(userCart));

        service.mergeCart(testEmail, testSessionId);

        assertEquals(5, userItem.getQuantity());
        assertEquals("MERGED", guestCart.getStatus());
        verify(cartItemRepository, times(1)).save(userItem);
        verify(cartRepository, times(1)).save(guestCart);
    }

    @Test
    void testGetOrCreateCart_EmptyEmailAndSession() {
        Cart saved = new Cart();
        saved.setId(UUID.randomUUID());
        CartDTO dto = new CartDTO();
        dto.setId(saved.getId());

        when(cartRepository.save(any(Cart.class))).thenReturn(saved);
        when(cartMapper.toDto(saved)).thenReturn(dto);

        CartDTO result = service.getOrCreateCart(null, null);

        assertNotNull(result);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testAddItemToCart_BagNotFound() {
        UUID bagId = UUID.randomUUID();
        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.of(new Cart()));
        when(productBagRepository.findById(bagId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.addItemToCart(testEmail, null, bagId, 1));
    }

    @Test
    void testUpdateItemQuantity_CartNotFound() {
        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.empty());

        UUID bagId = UUID.randomUUID();
        assertThrows(EntityNotFoundException.class, () -> service.updateItemQuantity(testEmail, null, bagId, 1));
    }

    @Test
    void testUpdateItemQuantity_ItemNotFound() {
        Cart cart = new Cart();
        cart.setId(UUID.randomUUID());
        UUID bagId = UUID.randomUUID();

        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndBagId(cart.getId(), bagId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateItemQuantity(testEmail, null, bagId, 1));
    }

    @Test
    void testRemoveItemFromCart_CartNotFound() {
        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.empty());

        UUID bagId = UUID.randomUUID();
        assertThrows(EntityNotFoundException.class, () -> service.removeItemFromCart(testEmail, null, bagId));
    }

    @Test
    void testMergeCart_NullEmailOrSessionId() {
        assertThrows(IllegalArgumentException.class, () -> service.mergeCart(null, testSessionId));
        assertThrows(IllegalArgumentException.class, () -> service.mergeCart(testEmail, null));
    }

    @Test
    void testMergeCart_GuestCartEmpty() {
        when(cartRepository.findBySessionIdAndStatus(testSessionId, "ACTIVE")).thenReturn(Optional.empty());

        Cart saved = new Cart();
        saved.setId(UUID.randomUUID());
        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.of(saved)); // For
                                                                                                       // getOrCreateCart
                                                                                                       // fallback
        when(cartMapper.toDto(any())).thenReturn(new CartDTO());

        service.mergeCart(testEmail, testSessionId);

        verify(cartRepository, never()).save(argThat(c -> "MERGED".equals(c.getStatus())));
    }

    @Test
    void testMergeCart_NewItemForUser() {
        Cart guestCart = new Cart();
        guestCart.setId(UUID.randomUUID());
        guestCart.setSessionId(testSessionId);

        CartItem guestItem = new CartItem();
        ProductBag b1 = new ProductBag();
        b1.setId(UUID.randomUUID());
        guestItem.setBag(b1);
        guestItem.setQuantity(2);
        guestCart.setItems(List.of(guestItem));

        Cart userCart = new Cart();
        userCart.setId(UUID.randomUUID());
        userCart.setEmail(testEmail);

        when(cartRepository.findBySessionIdAndStatus(testSessionId, "ACTIVE")).thenReturn(Optional.of(guestCart));
        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.of(userCart));
        when(cartItemRepository.findByCartIdAndBagId(userCart.getId(), b1.getId())).thenReturn(Optional.empty());
        when(cartRepository.findById(userCart.getId())).thenReturn(Optional.of(userCart));

        service.mergeCart(testEmail, testSessionId);

        assertEquals(userCart, guestItem.getCart());
        assertEquals("MERGED", guestCart.getStatus());
        verify(cartItemRepository, times(1)).save(guestItem);
        verify(cartRepository, times(1)).save(guestCart);
    }

    @Test
    void testGetOrCreateCart_BlankEmail() {
        Cart saved = new Cart();
        saved.setId(UUID.randomUUID());
        CartDTO dto = new CartDTO();
        when(cartRepository.save(any(Cart.class))).thenReturn(saved);
        when(cartMapper.toDto(saved)).thenReturn(dto);

        service.getOrCreateCart("   ", null);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testGetOrCreateCart_BlankSessionId() {
        Cart saved = new Cart();
        saved.setId(UUID.randomUUID());
        CartDTO dto = new CartDTO();
        when(cartRepository.save(any(Cart.class))).thenReturn(saved);
        when(cartMapper.toDto(saved)).thenReturn(dto);

        service.getOrCreateCart(null, "   ");
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testGetOrCreateCart_NewUser() {
        Cart saved = new Cart();
        saved.setId(UUID.randomUUID());
        CartDTO dto = new CartDTO();
        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(saved);
        when(cartMapper.toDto(saved)).thenReturn(dto);

        service.getOrCreateCart(testEmail, null);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testAddItemToCart_NewCartUser() {
        UUID bagId = UUID.randomUUID();
        ProductBag bag = new ProductBag();
        bag.setId(bagId);
        bag.setIsActive(true);

        Cart cart = new Cart();
        cart.setId(UUID.randomUUID());

        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(productBagRepository.findById(bagId)).thenReturn(Optional.of(bag));
        when(cartItemRepository.findByCartIdAndBagId(cart.getId(), bagId)).thenReturn(Optional.empty());
        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(cartMapper.toDto(cart)).thenReturn(new CartDTO());

        service.addItemToCart(testEmail, null, bagId, 1);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void testAddItemToCart_ExistingItem_Guest() {
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart();
        cart.setId(cartId);

        UUID bagId = UUID.randomUUID();
        ProductBag bag = new ProductBag();
        bag.setId(bagId);
        bag.setIsActive(true);

        CartItem existingItem = new CartItem();
        existingItem.setId(UUID.randomUUID());
        existingItem.setQuantity(2);

        when(cartRepository.findBySessionIdAndStatus(testSessionId, "ACTIVE")).thenReturn(Optional.of(cart));
        when(productBagRepository.findById(bagId)).thenReturn(Optional.of(bag));
        when(cartItemRepository.findByCartIdAndBagId(cartId, bagId)).thenReturn(Optional.of(existingItem));
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartMapper.toDto(cart)).thenReturn(new CartDTO());

        service.addItemToCart(null, testSessionId, bagId, 3);
        verify(cartItemRepository, times(1)).save(existingItem);
    }

    @Test
    void testAddItemToCart_NewItem_User() {
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart();
        cart.setId(cartId);

        UUID bagId = UUID.randomUUID();
        ProductBag bag = new ProductBag();
        bag.setId(bagId);
        bag.setIsActive(true);

        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.of(cart));
        when(productBagRepository.findById(bagId)).thenReturn(Optional.of(bag));
        when(cartItemRepository.findByCartIdAndBagId(cartId, bagId)).thenReturn(Optional.empty());
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartMapper.toDto(cart)).thenReturn(new CartDTO());

        service.addItemToCart(testEmail, null, bagId, 2);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void testUpdateItemQuantity_Update_Guest() {
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart();
        cart.setId(cartId);

        UUID bagId = UUID.randomUUID();
        CartItem item = new CartItem();
        item.setId(UUID.randomUUID());
        item.setQuantity(2);

        when(cartRepository.findBySessionIdAndStatus(testSessionId, "ACTIVE")).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndBagId(cartId, bagId)).thenReturn(Optional.of(item));
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        service.updateItemQuantity(null, testSessionId, bagId, 5);
        verify(cartItemRepository, times(1)).save(item);
    }

    @Test
    void testMergeCart_GuestCartExistsButEmpty() {
        Cart guestCart = new Cart();
        guestCart.setId(UUID.randomUUID());
        guestCart.setItems(new ArrayList<>());

        when(cartRepository.findBySessionIdAndStatus(testSessionId, "ACTIVE")).thenReturn(Optional.of(guestCart));

        Cart saved = new Cart();
        saved.setId(UUID.randomUUID());
        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.of(saved));
        when(cartMapper.toDto(any())).thenReturn(new CartDTO());

        service.mergeCart(testEmail, testSessionId);

        verify(cartRepository, never()).save(argThat(c -> "MERGED".equals(c.getStatus())));
    }

    @Test
    void testMergeCart_UserCartNotFound() {
        Cart guestCart = new Cart();
        guestCart.setId(UUID.randomUUID());
        guestCart.setSessionId(testSessionId);

        CartItem guestItem = new CartItem();
        ProductBag b1 = new ProductBag();
        b1.setId(UUID.randomUUID());
        guestItem.setBag(b1);
        guestItem.setQuantity(2);
        guestCart.setItems(List.of(guestItem));

        Cart userCart = new Cart();
        userCart.setId(UUID.randomUUID());

        when(cartRepository.findBySessionIdAndStatus(testSessionId, "ACTIVE")).thenReturn(Optional.of(guestCart));
        when(cartRepository.findByEmailAndStatus(testEmail, "ACTIVE")).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(userCart); // Mock save to return a user cart
        when(cartItemRepository.findByCartIdAndBagId(userCart.getId(), b1.getId())).thenReturn(Optional.empty());
        when(cartRepository.findById(userCart.getId())).thenReturn(Optional.of(userCart));

        service.mergeCart(testEmail, testSessionId);

        verify(cartRepository, times(1)).save(argThat(c -> testEmail.equals(c.getEmail()))); // Verifies createNewCart
                                                                                             // save
        verify(cartItemRepository, times(1)).save(guestItem);
        verify(cartRepository, times(1)).save(guestCart);
    }
}
