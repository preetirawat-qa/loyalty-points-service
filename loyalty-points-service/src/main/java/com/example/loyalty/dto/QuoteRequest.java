package com.example.loyalty.dto;

public class QuoteRequest {

    private double fareAmount;
    private String currency;
    private String cabinClass;
    private String customerTier;
    private String promoCode;

    public QuoteRequest() {}

    public double getFareAmount() {
        return fareAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCabinClass() {
        return cabinClass;
    }

    public String getCustomerTier() {
        return customerTier;
    }

    public String getPromoCode() {
        return promoCode;
    }
}
