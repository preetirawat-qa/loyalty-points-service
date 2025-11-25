package com.example.loyalty.component;

import io.vertx.junit5.VertxTestContext;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

public class QuoteFxRetryTest extends BaseComponentTest {

    @Test
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
