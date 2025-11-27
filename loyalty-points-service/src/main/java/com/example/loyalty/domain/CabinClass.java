package com.example.loyalty.domain;
/**
 * CabinClass represents different airline seating classes.
 * 
 * Values:
 * - ECONOMY
 * - PREMIUM
 * - BUSINESS
 * - FIRST
 *
 * Provides a helper method to parse a string into a CabinClass enum.
 */
public enum CabinClass {
    ECONOMY,
    PREMIUM,
    BUSINESS,
    FIRST;

    public static CabinClass fromString(String s) {
        try {
            return CabinClass.valueOf(s.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
