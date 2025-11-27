package com.example.loyalty.component;

import io.vertx.junit5.VertxTestContext;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;
/**
 * QuoteHappyPathTest verifies the normal end-to-end behavior
 * of the Loyalty Points Service when all inputs and services are valid.
 *
 * Extends BaseComponentTest to reuse:
 * - Mock FX and Promo services
 * - Vert.x client setup
 * - Randomized HTTP port for testing
 */
public class QuoteHappyPathTest extends BaseComponentTest {

    @Test
    /**
     * Test the happy path scenario:
     * - FX service returns valid rate
     * - Promo service returns a valid multiplier and expiry date
     * - Quote endpoint calculates base points, tier bonus, promo bonus correctly
     * - Promo warning is included if promo is expiring soon
     *
     * Steps:
     * 1. Stub FX service to return 3.67
     * 2. Stub Promo service with 25% multiplier and expiry in 2 days
     * 3. Send a POST request to /v1/points/quote
     * 4. Assert correct base points, tier bonus, promo bonus, and warnings
     *
     * @param vertx Vertx instance provided by JUnit 5 extension
     * @param ctx   VertxTestContext for async assertions
     */
    void testHappyPath(Vertx vertx, VertxTestContext ctx) {

        fxMock.stubFor(get(urlEqualTo("/v1/fx/USD"))
                .willReturn(aResponse().withBody("{\"rate\":3.67}")));

        promoMock.stubFor(get(urlEqualTo("/v1/promos/SUMMER25"))
                .willReturn(aResponse()
                        .withBody("{\"multiplier\":0.25,\"expiresOn\":\""
                                + java.time.LocalDate.now().plusDays(2) + "\"}")));

        var client = client(vertx);

        client.post(port, "localhost", "/v1/points/quote")
                .sendJson("""
                         {
                           "fareAmount": 100,
                           "currency": "USD",
                           "cabinClass": "ECONOMY",
                           "customerTier": "SILVER",
                           "promoCode": "SUMMER25"
                         }
                        """)
                .onSuccess(res -> {
                    assertThat(res.statusCode()).isEqualTo(200);
                    var body = res.bodyAsJsonObject();

                    assertThat(body.getInteger("basePoints")).isEqualTo((int)(100 * 3.67));
                    assertThat(body.getInteger("tierBonus")).isGreaterThan(0);
                    assertThat(body.getInteger("promoBonus")).isGreaterThan(0);
                    assertThat(body.getJsonArray("warnings")).contains("PROMO_EXPIRES_SOON");
                    ctx.completeNow();
                })
                .onFailure(ctx::failNow);
    }
}
