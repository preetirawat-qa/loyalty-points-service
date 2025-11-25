package com.example.loyalty.component;

import io.vertx.junit5.VertxTestContext;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class QuoteValidationTest extends BaseComponentTest {

    @Test
    void testInvalidCurrency(Vertx vertx, VertxTestContext ctx) {

        var client = client(vertx);

        client.post(port, "localhost", "/v1/points/quote")
                .sendJson("""
                    {
                      "fareAmount": 100,
                      "currency": "XYZ",
                      "cabinClass": "ECONOMY",
                      "customerTier": "NONE"
                    }
                """)
                .onSuccess(res -> {
                    assertThat(res.statusCode()).isEqualTo(400);
                    assertThat(res.bodyAsJsonObject().getString("error"))
                            .contains("Invalid currency");
                    ctx.completeNow();
                })
                .onFailure(ctx::failNow);
    }
}
