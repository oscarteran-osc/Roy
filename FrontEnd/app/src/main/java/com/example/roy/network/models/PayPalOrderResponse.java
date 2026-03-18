package com.example.roy.network.models;

public class PayPalOrderResponse {
    private String orderId;
    private String status;
    private String approvalUrl;

    public String getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public String getApprovalUrl() {
        return approvalUrl;
    }
}