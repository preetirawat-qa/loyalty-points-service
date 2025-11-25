package com.example.loyalty.domain;

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
