package com.example.loyalty.component;

import io.vertx.junit5.VertxTestContext;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

public class QuotePromoTimeoutTest extends BaseComponentTest {

    @Test
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
