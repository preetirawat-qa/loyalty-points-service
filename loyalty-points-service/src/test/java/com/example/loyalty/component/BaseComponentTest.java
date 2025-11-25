package com.example.loyalty.component;

import com.example.loyalty.bootstrap.MainVerticle;
import com.github.tomakehurst.wiremock.WireMockServer;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.concurrent.ThreadLocalRandom;

@ExtendWith(VertxExtension.class)
public class BaseComponentTest {

    protected static WireMockServer fxMock = new WireMockServer(0);
    protected static WireMockServer promoMock = new WireMockServer(0);
    protected static int port;

    protected WebClient client(Vertx vertx) {
        return WebClient.create(vertx);
    }

    @BeforeAll
    static void setup(Vertx vertx, VertxTestContext ctx) {

        fxMock.start();
        promoMock.start();

        port = ThreadLocalRandom.current().nextInt(40000, 50000);

        JsonObject cfg = new JsonObject()
                .put("http.port", port)
                .put("fx.url", fxMock.baseUrl())
                .put("promo.url", promoMock.baseUrl());

        vertx.deployVerticle(
                new MainVerticle(),
                new DeploymentOptions().setConfig(cfg)
        ).onComplete(ar -> {
            if (ar.succeeded()) ctx.completeNow();
            else ctx.failNow(ar.cause());
        });
    }

    @AfterAll
    static void tearDown() {
        fxMock.stop();
        promoMock.stop();
    }

    protected Buffer json(String s) {
        return Buffer.buffer(s);
    }
}
