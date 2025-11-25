package com.example.loyalty.dto;

import java.util.List;

public class QuoteResponse {

    private final int basePoints;
    private final int tierBonus;
    private final int promoBonus;
    private final int totalPoints;
    private final double effectiveFxRate;
    private final List<String> warnings;

    public QuoteResponse(
            int basePoints,
            int tierBonus,
            int promoBonus,
            int totalPoints,
            double fxRate,
            List<String> warnings
    ) {
        this.basePoints = basePoints;
        this.tierBonus = tierBonus;
        this.promoBonus = promoBonus;
        this.totalPoints = totalPoints;
        this.effectiveFxRate = fxRate;
        this.warnings = warnings;
    }

    public int getBasePoints() { return basePoints; }
    public int getTierBonus() { return tierBonus; }
    public int getPromoBonus() { return promoBonus; }
    public int getTotalPoints() { return totalPoints; }
    public double getEffectiveFxRate() { return effectiveFxRate; }
    public List<String> getWarnings() { return warnings; }
}
