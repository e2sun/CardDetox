package com.cartdetox.controller;

import com.cartdetox.dto.CartItemRequest;
import com.cartdetox.repository.UserRepository;
import com.cartdetox.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow().getId();
    }

    @GetMapping
    public ResponseEntity<?> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getOrCreateCart(getUserId(userDetails)));
    }

    @PostMapping("/items")
    public ResponseEntity<?> addItem(@AuthenticationPrincipal UserDetails userDetails,
                                     @Valid @RequestBody CartItemRequest req) {
        try {
            return ResponseEntity.ok(cartService.addItem(getUserId(userDetails), req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<?> updateItem(@AuthenticationPrincipal UserDetails userDetails,
                                        @PathVariable Long itemId,
                                        @RequestBody Map<String, Integer> body) {
        return ResponseEntity.ok(cartService.updateItem(getUserId(userDetails), itemId, body.get("quantity")));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> removeItem(@AuthenticationPrincipal UserDetails userDetails,
                                        @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItem(getUserId(userDetails), itemId));
    }

    @DeleteMapping
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(getUserId(userDetails));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/merge")
    public ResponseEntity<?> mergeCart(@AuthenticationPrincipal UserDetails userDetails,
                                       @RequestBody List<CartItemRequest> guestItems) {
        return ResponseEntity.ok(cartService.mergeGuestCart(getUserId(userDetails), guestItems));
    }
}
