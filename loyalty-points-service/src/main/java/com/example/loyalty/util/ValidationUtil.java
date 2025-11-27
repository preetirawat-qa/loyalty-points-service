package com.example.loyalty.util;

import com.example.loyalty.domain.CabinClass;
import com.example.loyalty.domain.Tier;
import com.example.loyalty.dto.QuoteRequest;
import com.example.loyalty.exceptions.ValidationException;

import java.util.Set;
/**
 * ValidationUtil provides static helper methods to validate
 * QuoteRequest objects before processing.
 *
 * Throws ValidationException if any validation rule is violated.
 */
public class ValidationUtil {

    private static final Set<String> VALID_CURRENCIES =
            Set.of("USD", "EUR", "GBP", "SGD", "AED");
/**
     * Validates the given QuoteRequest.
     *
     * Checks:
     * - fareAmount must be greater than 0
     * - currency must be in the allowed set
     * - cabinClass must match a valid CabinClass enum
     * - customerTier must match a valid Tier enum
     *
     * @param req the QuoteRequest to validate
     * @throws ValidationException if any validation rule fails
     */
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
