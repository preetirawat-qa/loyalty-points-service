package com.example.loyalty.bootstrap;

import com.example.loyalty.api.QuoteHttpVerticle;
import com.example.loyalty.clients.FxClient;
import com.example.loyalty.clients.PromoClient;
import com.example.loyalty.domain.LoyaltyCalculator;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

/**
 * MainVerticle is the application's bootstrap entry point.
 *
 * Responsibilities:
 * - Load configuration
 * - Initialize HTTP clients and domain services
 * - Instantiate and deploy the QuoteHttpVerticle
 * - Start the Loyalty Points Service
 *
 * This class wires together all dependencies required for handling
 * loyalty point quote requests.
 */
public class MainVerticle extends AbstractVerticle {

    /**
     * Called automatically by Vert.x when this verticle starts.
     * Initializes services and deploys the main HTTP verticle.
     *
     * @param startPromise used to signal success or failure during startup
     */
    @Override
    public void start(Promise<Void> startPromise) {

        // Load configuration provided externally or via Vert.x launcher
        JsonObject config = config();

        // HTTP server port with fallback default
        int httpPort = config.getInteger("http.port", 8080);

        // Shared WebClient used for outbound HTTP calls (FX + promo)
        WebClient webClient = WebClient.create(vertx);

        // Instantiate FX client using configured service URL
        FxClient fxClient = new FxClient(
                webClient,
                config.getString("fx.url"),
                vertx
        );

        // Instantiate Promo client using configured service URL
        PromoClient promoClient = new PromoClient(
                webClient,
                config.getString("promo.url"),
                vertx
        );

        // Business logic service responsible for computing loyalty points
        LoyaltyCalculator calculator = new LoyaltyCalculator();

        // Deploy the HTTP API verticle, injecting all required dependencies
        vertx.deployVerticle(
                new QuoteHttpVerticle(fxClient, promoClient, calculator, httpPort)
        ).onComplete(ar -> {
            if (ar.succeeded()) {
                // Startup success
                System.out.println("Loyalty Points Service started on port " + httpPort);
                startPromise.complete();
            } else {
                // Startup failure
                startPromise.fail(ar.cause());
            }
        });
    }
}
