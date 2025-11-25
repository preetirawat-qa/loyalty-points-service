package com.example.loyalty.component;

import io.vertx.junit5.VertxTestContext;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

public class QuoteHappyPathTest extends BaseComponentTest {

    @Test
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
