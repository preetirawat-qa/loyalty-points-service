package com.example.loyalty.component;

import io.vertx.junit5.VertxTestContext;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;
/**
 * QuoteCappingTest verifies that the Loyalty Points Service
 * correctly caps the total points at the maximum allowed value.
 *
 * Extends BaseComponentTest to reuse:
 * - Mock FX and Promo services
 * - Vert.x client setup
 * - Randomized HTTP port for testing
 */
public class QuoteCappingTest extends BaseComponentTest {
/**
     * Test that a high-value booking results in total points
     * being capped at 50,000 points (MAX_POINTS).
     *
     * Steps:
     * 1. Stub FX service to return a high exchange rate
     * 2. Stub Promo service to provide a high bonus multiplier
     * 3. Send a POST request to /v1/points/quote with large fare
     * 4. Assert that totalPoints in response equals the cap (50,000)
     *
     * @param vertx Vertx instance provided by JUnit 5 extension
     * @param ctx   VertxTestContext to handle async assertions
     */
    @Test
    void testCapping(Vertx vertx, VertxTestContext ctx) {

        fxMock.stubFor(get("/v1/fx/USD")
                .willReturn(aResponse().withBody("{\"rate\":10.0}")));

        promoMock.stubFor(get(anyUrl())
                .willReturn(aResponse().withBody("{\"multiplier\":0.5}")));

        var client = client(vertx);

        client.post(port, "localhost", "/v1/points/quote")
                .sendJson("""
                    {
                      "fareAmount": 1000,
                      "currency": "USD",
                      "cabinClass": "ECONOMY",
                      "customerTier": "PLATINUM",
                      "promoCode": "BIG"
                    }
                """)
                .onSuccess(res -> {
                    assertThat(res.statusCode()).isEqualTo(200);
                    assertThat(res.bodyAsJsonObject().getInteger("totalPoints"))
                            .isEqualTo(50000);
                    ctx.completeNow();
                })
                .onFailure(ctx::failNow);
    }
}
