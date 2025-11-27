package com.example.loyalty.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
/**
 * PromoInfo represents a promotional offer that can provide
 * additional loyalty points for a booking.
 *
 * Fields:
 * - multiplier: percentage bonus applied to base points
 * - expiresOn: optional expiry date of the promo
 * - available: whether the promo is currently active
 */

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
  /**
     * Returns a standard unavailable PromoInfo object.
     *
     * Used as a fallback when the promo service is unreachable.
     *
     * @return PromoInfo marked as unavailable
     */
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
 /**
     * Returns whether the promo is currently available.
     *
     * Marked with @JsonIgnore to avoid exposing internal state in JSON responses.
     *
     * @return true if promo is available; false otherwise
     */
    @JsonIgnore
    public boolean isAvailable() {
        return available;
    }
}
