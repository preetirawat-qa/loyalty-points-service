package com.example.loyalty.util;

import com.example.loyalty.domain.CabinClass;
import com.example.loyalty.domain.Tier;
import com.example.loyalty.dto.QuoteRequest;
import com.example.loyalty.exceptions.ValidationException;

import java.util.Set;

public class ValidationUtil {

    private static final Set<String> VALID_CURRENCIES =
            Set.of("USD", "EUR", "GBP", "SGD", "AED");

    public static void validate(QuoteRequest req) {

        if (req.getFareAmount() <= 0) {
            throw new ValidationException("fareAmount must be > 0");
        }

        if (!VALID_CURRENCIES.contains(req.getCurrency())) {
            throw new ValidationException("Invalid currency");
        }

        if (CabinClass.fromString(req.getCabinClass()) == null) {
            throw new ValidationException("Invalid cabinClass");
        }

        if (Tier.fromString(req.getCustomerTier()) == null) {
            throw new ValidationException("Invalid customerTier");
        }
    }
}
