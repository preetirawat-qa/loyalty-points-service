package com.example.loyalty.domain;

public enum Tier {
    NONE(0.0),
    SILVER(0.15),
    GOLD(0.30),
    PLATINUM(0.50);

    private final double multiplier;

    Tier(double multiplier) {
        this.multiplier = multiplier;
    }

    public double mult() {
        return multiplier;
    }

    public static Tier fromString(String s) {
        try {
            return Tier.valueOf(s.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
