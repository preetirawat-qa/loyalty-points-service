package com.example.loyalty.component;

import io.vertx.junit5.VertxTestContext;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;
/**
 * QuoteFxRetryTest verifies that the Loyalty Points Service correctly
 * retries FX service calls in case of temporary failures.
 *
 * Extends BaseComponentTest to reuse:
 * - Mock FX and Promo services
 * - Vert.x client setup
 * - Randomized HTTP port for testing
 */
public class QuoteFxRetryTest extends BaseComponentTest {

    @Test
    /**
     * Test that a failed FX call initially returns 500, but
     * the retry logic succeeds and returns the correct FX rate.
     *
     * Steps:
     * 1. Stub FX service scenario:
     *    - First request: 500 error
     *    - Second request: 200 OK with rate 4.0
     * 2. Stub Promo service to return a small multiplier
     * 3. Send POST request to /v1/points/quote
     * 4. Assert that the response contains the retried FX rate (4.0)
     *
     * @param vertx Vertx instance provided by JUnit 5 extension
     * @param ctx   VertxTestContext for async assertions
     */
    void testFxRetrySuccess(Vertx vertx, VertxTestContext ctx) {

        fxMock.stubFor(get("/v1/fx/USD")
                .inScenario("FX_RETRY")
                .whenScenarioStateIs("Started")
                .willReturn(aResponse().withStatus(500))
                .willSetStateTo("RETRY_OK"));

        fxMock.stubFor(get("/v1/fx/USD")
                .inScenario("FX_RETRY")
                .whenScenarioStateIs("RETRY_OK")
                .willReturn(aResponse().withBody("{\"rate\":4.0}")));

        promoMock.stubFor(any(urlMatching(".*"))
                .willReturn(aResponse().withBody("{\"multiplier\":0.1}")));

        var client = client(vertx);

        client.post(port, "localhost", "/v1/points/quote")
                .sendJson("""
                        {
                          "fareAmount": 10,
                          "currency": "USD",
                          "cabinClass": "ECONOMY",
                          "customerTier": "NONE"
                        }
                        """)
                .onSuccess(res -> {
                    assertThat(res.statusCode()).isEqualTo(200);
                    assertThat(res.bodyAsJsonObject().getDouble("effectiveFxRate"))
                            .isEqualTo(4.0);
                    ctx.completeNow();
                })
                .onFailure(ctx::failNow);
    }
}
