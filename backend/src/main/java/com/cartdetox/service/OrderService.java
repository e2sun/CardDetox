package com.cartdetox.service;

import com.cartdetox.dto.CheckoutRequest;
import com.cartdetox.model.*;
import com.cartdetox.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    @Transactional
    public Order checkout(Long userId, CheckoutRequest req) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart is empty"));
        if (cart.getItems().isEmpty()) throw new RuntimeException("Cart is empty");

        User user = userRepository.findById(userId).orElseThrow();

        double subtotal = cart.getItems().stream()
                .mapToDouble(i -> {
                    double price = Boolean.TRUE.equals(i.getProduct().getIsClearance())
                            && i.getProduct().getClearancePrice() != null
                            ? i.getProduct().getClearancePrice()
                            : i.getProduct().getPrice();
                    return price * i.getQuantity();
                }).sum();

        double tokensUsed = 0;
        if (Boolean.TRUE.equals(req.getUseTokens()) && req.getTokensToUse() != null) {
            tokensUsed = Math.min(req.getTokensToUse(), Math.min(user.getDetoxTokens(), subtotal));
        }
        double total = Math.max(0, subtotal - tokensUsed);

        if (tokensUsed > 0) {
            user.setDetoxTokens(user.getDetoxTokens() - tokensUsed);
        }
        user.setTotalOrders(user.getTotalOrders() + 1);
        user.setTotalSaved(user.getTotalSaved() + subtotal);
        userRepository.save(user);

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(ci -> OrderItem.builder()
                        .product(ci.getProduct())
                        .quantity(ci.getQuantity())
                        .selectedSize(ci.getSelectedSize())
                        .selectedColor(ci.getSelectedColor())
                        .priceAtPurchase(Boolean.TRUE.equals(ci.getProduct().getIsClearance())
                                && ci.getProduct().getClearancePrice() != null
                                ? ci.getProduct().getClearancePrice()
                                : ci.getProduct().getPrice())
                        .build())
                .collect(Collectors.toList());

        Order order = Order.builder()
                .user(user)
                .items(orderItems)
                .subtotal(subtotal)
                .tokensUsed(tokensUsed)
                .totalTokens(total)
                .fakeTrackingNumber("CD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .build();
        Order saved = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        return saved;
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Order getOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getUser().getId().equals(userId)) throw new RuntimeException("Forbidden");
        return order;
    }
}
