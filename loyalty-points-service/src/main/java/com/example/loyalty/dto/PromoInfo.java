package com.example.loyalty.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;

public class PromoInfo {

    private double multiplier;
    private LocalDate expiresOn;
    private boolean available = true;

    public PromoInfo() {}

    public PromoInfo(double multiplier, LocalDate expiresOn, boolean available) {
        this.multiplier = multiplier;
        this.expiresOn = expiresOn;
        this.available = available;
    }

    public static PromoInfo unavailable() {
        return new PromoInfo(0.0, null, false);
    }

    public static PromoInfo none() {
        return new PromoInfo(0.0, null, true);
    }

    public double getMultiplier() {
        return multiplier;
    }

    public LocalDate getExpiresOn() {
        return expiresOn;
    }

    @JsonIgnore
    public boolean isAvailable() {
        return available;
    }
}
