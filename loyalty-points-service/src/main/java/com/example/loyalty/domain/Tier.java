package com.example.loyalty.domain;
/**
 * Tier represents customer loyalty tiers, each with an associated multiplier
 * for calculating bonus loyalty points.
 *
 * Tiers:
 * - NONE: no tier, multiplier = 0.0
 * - SILVER: 15% bonus points
 * - GOLD: 30% bonus points
 * - PLATINUM: 50% bonus points
 */
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
