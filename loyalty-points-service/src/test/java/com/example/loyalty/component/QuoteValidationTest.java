package com.example.loyalty.component;

import io.vertx.junit5.VertxTestContext;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
/**
 * QuoteValidationTest verifies that the Loyalty Points Service
 * correctly validates incoming request payloads and returns
 * appropriate error responses when invalid data is provided.
 *
 * Extends BaseComponentTest to reuse:
 * - Mock FX and Promo services
 * - Vert.x client setup
 * - Randomized HTTP port for testing
 */
public class QuoteValidationTest extends BaseComponentTest {

    @Test
    /**
     * Test scenario where the currency provided in the request
     * is invalid (not in the allowed set of currencies).
     *
     * Steps:
     * 1. Send a POST request to /v1/points/quote with currency "XYZ"
     * 2. Expect HTTP status 400 (Bad Request)
     * 3. Assert error message contains "Invalid currency"
     *
     * @param vertx Vertx instance provided by JUnit 5 extension
     * @param ctx   VertxTestContext for async assertions
     */
    void testInvalidCurrency(Vertx vertx, VertxTestContext ctx) {

        var client = client(vertx);

        client.post(port, "localhost", "/v1/points/quote")
                .sendJson("""
                    {
                      "fareAmount": 100,
                      "currency": "XYZ",
                      "cabinClass": "ECONOMY",
                      "customerTier": "NONE"
                    }
                """)
                .onSuccess(res -> {
                    assertThat(res.statusCode()).isEqualTo(400);
                    assertThat(res.bodyAsJsonObject().getString("error"))
                            .contains("Invalid currency");
                    ctx.completeNow();
                })
                .onFailure(ctx::failNow);
    }
}
