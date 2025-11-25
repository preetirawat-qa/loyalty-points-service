package com.example.loyalty.component;

import io.vertx.junit5.VertxTestContext;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

public class QuoteExpiryEdgeCaseTest extends BaseComponentTest {

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
