package com.bilbo.store.service;

import com.bilbo.store.dto.CartDTO;
import com.bilbo.store.entites.Cart;
import com.bilbo.store.entites.CartItem;
import com.bilbo.store.entites.ProductBag;
import com.bilbo.store.mapper.CartMapper;
import com.bilbo.store.repository.CartItemRepository;
import com.bilbo.store.repository.CartRepository;
import com.bilbo.store.repository.ProductBagRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductBagRepository productBagRepository;
    private final CartMapper cartMapper;

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_MERGED = "MERGED";

    @Transactional
    public CartDTO getOrCreateCart(String email, String sessionId) {
        Cart cart = findActiveCart(email, sessionId).orElseGet(() -> createNewCart(email, sessionId));
        // We might want to save if it's new, but createNewCart doesn't save yet.
        if (cart.getId() == null) {
            cart = cartRepository.save(cart);
        }
        return cartMapper.toDto(cart);
    }

    private Optional<Cart> findActiveCart(String email, String sessionId) {
        if (email != null && !email.isBlank()) {
            return cartRepository.findByEmailAndStatus(email, STATUS_ACTIVE);
        } else if (sessionId != null && !sessionId.isBlank()) {
            return cartRepository.findBySessionIdAndStatus(sessionId, STATUS_ACTIVE);
        }
        return Optional.empty();
    }

    private Cart createNewCart(String email, String sessionId) {
        log.info("Creating new cart for email: {}, sessionId: {}", email, sessionId);
        Cart cart = new Cart();
        cart.setEmail(email);
        cart.setSessionId(sessionId);
        cart.setStatus(STATUS_ACTIVE);
        cart.setItems(new ArrayList<>());
        cart.setCreatedTimestamp(OffsetDateTime.now());
        cart.setCreatedBy(email != null ? email : sessionId);
        return cart;
    }

    @Transactional
    public CartDTO addItemToCart(String email, String sessionId, UUID bagId, Integer quantity) {
        Cart cart = findActiveCart(email, sessionId)
                .orElseGet(() -> cartRepository.save(createNewCart(email, sessionId)));

        ProductBag bag = productBagRepository.findById(bagId)
                .orElseThrow(() -> new EntityNotFoundException("Product bag not found: " + bagId));

        if (!bag.getIsActive()) {
            throw new IllegalArgumentException("Cannot add inactive product bag to cart");
        }

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndBagId(cart.getId(), bagId);

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setLastUpdatedTimestamp(OffsetDateTime.now());
            existingItem.setLastUpdatedBy(email != null ? email : sessionId);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setBag(bag);
            newItem.setQuantity(quantity);
            newItem.setCreatedTimestamp(OffsetDateTime.now());
            newItem.setCreatedBy(email != null ? email : sessionId);
            cartItemRepository.save(newItem);
        }

        // Fetch refreshed cart
        return cartMapper.toDto(cartRepository.findById(cart.getId()).orElse(cart));
    }

    @Transactional
    public CartDTO updateItemQuantity(String email, String sessionId, UUID bagId, Integer quantity) {
        Cart cart = findActiveCart(email, sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Active cart not found"));

        CartItem item = cartItemRepository.findByCartIdAndBagId(cart.getId(), bagId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found in cart"));

        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            item.setLastUpdatedTimestamp(OffsetDateTime.now());
            item.setLastUpdatedBy(email != null ? email : sessionId);
            cartItemRepository.save(item);
        }

        return cartMapper.toDto(cartRepository.findById(cart.getId()).orElse(cart));
    }

    @Transactional
    public CartDTO removeItemFromCart(String email, String sessionId, UUID bagId) {
        Cart cart = findActiveCart(email, sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Active cart not found"));

        cart.getItems().removeIf(item -> item.getBag().getId().equals(bagId));
        cart.setLastUpdatedTimestamp(OffsetDateTime.now());
        cart.setLastUpdatedBy(email != null ? email : sessionId);
        Cart savedCart = cartRepository.save(cart);

        return cartMapper.toDto(savedCart);
    }

    @Transactional
    public CartDTO mergeCart(String email, String sessionId) {
        log.info("Attempting to merge cart for sessionId: {} into email: {}", sessionId, email);
        if (email == null || sessionId == null) {
            throw new IllegalArgumentException("Both email and sessionId are required to merge carts");
        }

        Optional<Cart> guestCartOpt = cartRepository.findBySessionIdAndStatus(sessionId, STATUS_ACTIVE);
        if (guestCartOpt.isEmpty() || guestCartOpt.get().getItems().isEmpty()) {
            log.info("No active guest cart found or cart is empty for sessionId: {}", sessionId);
            return getOrCreateCart(email, null);
        }

        Cart guestCart = guestCartOpt.get();
        Cart userCart = findActiveCart(email, null).orElseGet(() -> cartRepository.save(createNewCart(email, null)));

        for (CartItem guestItem : guestCart.getItems()) {
            Optional<CartItem> userItemOpt = cartItemRepository.findByCartIdAndBagId(userCart.getId(),
                    guestItem.getBag().getId());
            if (userItemOpt.isPresent()) {
                CartItem userItem = userItemOpt.get();
                userItem.setQuantity(userItem.getQuantity() + guestItem.getQuantity());
                cartItemRepository.save(userItem);
            } else {
                guestItem.setCart(userCart);
                cartItemRepository.save(guestItem);
            }
        }

        guestCart.setStatus(STATUS_MERGED);
        guestCart.setLastUpdatedTimestamp(OffsetDateTime.now());
        cartRepository.save(guestCart);

        log.info("Successfully merged carts");
        return cartMapper.toDto(cartRepository.findById(userCart.getId()).orElse(userCart));
    }
}
