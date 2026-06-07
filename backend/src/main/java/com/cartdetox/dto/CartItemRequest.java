package com.cartdetox.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequest {
    @NotNull
    private Long productId;
    @Min(1)
    private Integer quantity = 1;
    private String selectedSize;
    private String selectedColor;
}
