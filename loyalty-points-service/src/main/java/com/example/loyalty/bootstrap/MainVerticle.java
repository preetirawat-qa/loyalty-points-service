package com.example.loyalty.bootstrap;

import com.example.loyalty.api.QuoteHttpVerticle;
import com.example.loyalty.clients.FxClient;
import com.example.loyalty.clients.PromoClient;
import com.example.loyalty.domain.LoyaltyCalculator;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) {

        JsonObject config = config();

        int httpPort = config.getInteger("http.port", 8080);

        // Instantiate shared services
        WebClient webClient = WebClient.create(vertx);

        FxClient fxClient = new FxClient(
                webClient,
                config.getString("fx.url"),
                vertx
        );

        PromoClient promoClient = new PromoClient(
                webClient,
                config.getString("promo.url"),
                vertx
        );

        LoyaltyCalculator calculator = new LoyaltyCalculator();

        // Deploy HTTP verticle with injected services
        vertx.deployVerticle(
                new QuoteHttpVerticle(fxClient, promoClient, calculator, httpPort)
        ).onComplete(ar -> {
            if (ar.succeeded()) {
                System.out.println("Loyalty Points Service started on port " + httpPort);
                startPromise.complete();
            } else {
                startPromise.fail(ar.cause());
            }
        });
    }
}
