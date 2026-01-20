package com.example.RoyServices.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayPalOrderResponse {
    private String orderId;
    private String status;
    private String approvalUrl;
}