package com.cartdetox.service;

import com.cartdetox.dto.CartItemRequest;
import com.cartdetox.model.*;
import com.cartdetox.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId).orElseThrow();
            Cart cart = Cart.builder().user(user).build();
            return cartRepository.save(cart);
        });
    }

    @Transactional
    public Cart addItem(Long userId, CartItemRequest req) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem existing = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(req.getProductId())
                        && equalOrNull(i.getSelectedSize(), req.getSelectedSize())
                        && equalOrNull(i.getSelectedColor(), req.getSelectedColor()))
                .findFirst().orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + req.getQuantity());
        } else {
            CartItem item = CartItem.builder()
                    .product(product)
                    .quantity(req.getQuantity())
                    .selectedSize(req.getSelectedSize())
                    .selectedColor(req.getSelectedColor())
                    .build();
            cart.getItems().add(item);
        }
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateItem(Long userId, Long itemId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .ifPresent(i -> {
                    if (quantity <= 0) cart.getItems().remove(i);
                    else i.setQuantity(quantity);
                });
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItem(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().removeIf(i -> i.getId().equals(itemId));
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Transactional
    public Cart mergeGuestCart(Long userId, List<CartItemRequest> guestItems) {
        for (CartItemRequest item : guestItems) {
            addItem(userId, item);
        }
        return getOrCreateCart(userId);
    }

    private boolean equalOrNull(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }
}
