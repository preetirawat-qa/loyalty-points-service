package com.example.loyalty.component;

import io.vertx.junit5.VertxTestContext;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;
/**
 * QuoteExpiryEdgeCaseTest verifies the behavior of the Loyalty Points Service
 * when a promotion is about to expire within the warning threshold (≤ 7 days).
 *
 * Extends BaseComponentTest to reuse:
 * - Mock FX and Promo services
 * - Vert.x client setup
 * - Randomized HTTP port for testing
 */
public class QuoteExpiryEdgeCaseTest extends BaseComponentTest {
/**
     * Test that when a promo expires exactly 7 days from now,
     * the system includes the "PROMO_EXPIRES_SOON" warning in the response.
     *
     * Steps:
     * 1. Stub FX service to return a standard rate
     * 2. Stub Promo service to return a multiplier with an expiry 7 days ahead
     * 3. Send a POST request to /v1/points/quote with the promo code
     * 4. Assert that the response includes the expected warning
     *
     * @param vertx Vertx instance provided by JUnit 5 extension
     * @param ctx   VertxTestContext to handle async assertions
     */
    @Test
    void testExpiryEdgeCase(Vertx vertx, VertxTestContext ctx) {

        fxMock.stubFor(get("/v1/fx/USD")
                .willReturn(aResponse().withBody("{\"rate\":3.0}")));

        promoMock.stubFor(get("/v1/promos/EDGE")
                .willReturn(aResponse()
                        .withBody("{\"multiplier\":0.1,\"expiresOn\":\""
                                + LocalDate.now().plusDays(7) + "\"}")));

        var client = client(vertx);

        client.post(port, "localhost", "/v1/points/quote")
                .sendJson("""
                    {
                      "fareAmount": 200,
                      "currency": "USD",
                      "cabinClass": "ECONOMY",
                      "customerTier": "NONE",
                      "promoCode": "EDGE"
                    }
                """)
                .onSuccess(res -> {
                    assertThat(res.statusCode()).isEqualTo(200);
                    assertThat(res.bodyAsJsonObject()
                            .getJsonArray("warnings"))
                            .contains("PROMO_EXPIRES_SOON");
                    ctx.completeNow();
                })
                .onFailure(ctx::failNow);
    }
}
