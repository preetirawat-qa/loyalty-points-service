package com.example.loyalty.component;

import io.vertx.junit5.VertxTestContext;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;
/**
 * QuotePromoTimeoutTest verifies the Loyalty Points Service behavior
 * when the promo service is slow or unresponsive.
 *
 * Extends BaseComponentTest to reuse:
 * - Mock FX and Promo services
 * - Vert.x client setup
 * - Randomized HTTP port for testing
 */
public class QuotePromoTimeoutTest extends BaseComponentTest {

    @Test
     /**
     * Test scenario where the promo service takes too long to respond:
     * - FX service returns valid rate
     * - Promo service is intentionally delayed (500ms)
     * - LoyaltyPointsService should fallback to default behavior
     *   - Promo bonus = 0
     *   - Warning "PROMO_UNAVAILABLE" is included
     *
     * Steps:
     * 1. Stub FX service to return 3.0
     * 2. Stub Promo service with fixed delay and a multiplier
     * 3. Send POST request to /v1/points/quote
     * 4. Assert promoBonus is zero and warning is present
     *
     * @param vertx Vertx instance provided by JUnit 5 extension
     * @param ctx   VertxTestContext for async assertions
     */
    void testPromoTimeoutFallback(Vertx vertx, VertxTestContext ctx) {

        fxMock.stubFor(get("/v1/fx/USD")
                .willReturn(aResponse().withBody("{\"rate\":3.0}")));

        promoMock.stubFor(get("/v1/promos/SLOWPROMO")
                .willReturn(aResponse().withFixedDelay(500)
                        .withBody("{\"multiplier\":0.25}")));

        var client = client(vertx);

        client.post(port, "localhost", "/v1/points/quote")
                .sendJson("""
                    {
                      "fareAmount": 100,
                      "currency": "USD",
                      "cabinClass": "ECONOMY",
                      "customerTier": "NONE",
                      "promoCode": "SLOWPROMO"
                    }
                """)
                .onSuccess(res -> {
                    assertThat(res.statusCode()).isEqualTo(200);
                    var body = res.bodyAsJsonObject();
                    assertThat(body.getInteger("promoBonus")).isEqualTo(0);
                    assertThat(body.getJsonArray("warnings")).contains("PROMO_UNAVAILABLE");
                    ctx.completeNow();
                })
                .onFailure(ctx::failNow);
    }
}
