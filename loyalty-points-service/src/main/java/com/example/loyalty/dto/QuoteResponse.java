package com.example.loyalty.dto;

import java.util.List;
/**
 * QuoteResponse represents the result of a loyalty points calculation
 * for a given booking request.
 *
 * Fields:
 * - basePoints: points earned from the effective fare before bonuses
 * - tierBonus: points awarded based on customer's loyalty tier
 * - promoBonus: points awarded from an applicable promotion
 * - totalPoints: total points after summing base, tier, and promo bonuses
 * - effectiveFxRate: FX rate used to convert fare to local currency
 * - warnings: list of any warnings (e.g., promo expiring soon, promo unavailable)
 */
public class QuoteResponse {

    private final int basePoints;
    private final int tierBonus;
    private final int promoBonus;
    private final int totalPoints;
    private final double effectiveFxRate;
    private final List<String> warnings;
/**
     * Constructs a QuoteResponse with all details.
     *
     * @param basePoints points from fare
     * @param tierBonus points from tier
     * @param promoBonus points from promotion
     * @param totalPoints total points after calculation
     * @param fxRate FX rate used
     * @param warnings list of warning messages
     */
    public QuoteResponse(
            int basePoints,
            int tierBonus,
            int promoBonus,
            int totalPoints,
            double fxRate,
            List<String> warnings
    ) 
    {
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
