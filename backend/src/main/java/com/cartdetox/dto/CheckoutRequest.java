package com.cartdetox.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private Boolean useTokens = false;
    private Double tokensToUse = 0.0;
    private String recipientName;
    private String address;
    private String city;
    private String state;
    private String zip;
}
