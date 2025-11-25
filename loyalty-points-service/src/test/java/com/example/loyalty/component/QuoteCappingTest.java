package com.example.loyalty.component;

import io.vertx.junit5.VertxTestContext;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

public class QuoteCappingTest extends BaseComponentTest {

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
