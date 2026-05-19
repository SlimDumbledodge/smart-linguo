package com.smartlinguo.dto.request.stripe;

public class CheckoutRequest {

    private String priceId;
    private String email;

    public String getPriceId() { return priceId; }
    public void setPriceId(String priceId) { this.priceId = priceId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}