package com.example.loyalty.domain;

import com.example.loyalty.dto.PromoInfo;
import com.example.loyalty.dto.QuoteRequest;
import com.example.loyalty.dto.QuoteResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoyaltyCalculator {

    private static final int MAX_POINTS = 50_000;

    public QuoteResponse calculate(
            QuoteRequest req,
            double fxRate,
            PromoInfo promo
    ) {

        double effectiveFare = req.getFareAmount() * fxRate;
        int basePoints = (int) Math.floor(effectiveFare);

        Tier tier = Tier.fromString(req.getCustomerTier());
        double tierMult = tier != null ? tier.mult() : 0.0;
        int tierBonus = (int) Math.round(basePoints * tierMult);

        int promoBonus = 0;
        List<String> warnings = new ArrayList<>();

        if (promo != null && promo.isAvailable()) {
            promoBonus = (int) Math.round(basePoints * promo.getMultiplier());

            // Expiry warning if ≤ 7 days
            LocalDate expires = promo.getExpiresOn();
            if (expires != null) {
                long days = LocalDate.now().until(expires).getDays();
                if (days >= 0 && days <= 7) {
                    warnings.add("PROMO_EXPIRES_SOON");
                }
            }
        } else {
            warnings.add("PROMO_UNAVAILABLE");
        }

        int total = basePoints + tierBonus + promoBonus;
        if (total > MAX_POINTS) {
            total = MAX_POINTS;
        }

        return new QuoteResponse(
                basePoints,
                tierBonus,
                promoBonus,
                total,
                fxRate,
                warnings
        );
    }
}
