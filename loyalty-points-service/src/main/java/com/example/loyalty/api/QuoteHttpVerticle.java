package com.example.loyalty.api;

import com.example.loyalty.clients.FxClient;
import com.example.loyalty.clients.PromoClient;
import com.example.loyalty.domain.LoyaltyCalculator;
import com.example.loyalty.dto.*;
import com.example.loyalty.exceptions.ValidationException;
import com.example.loyalty.util.ValidationUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * HTTP verticle exposing the `/v1/points/quote` endpoint.
 *
 * This API accepts a QuoteRequest, validates it, retrieves FX and promo info,
 * delegates calculation to LoyaltyCalculator, and returns a QuoteResponse.
 *
 * Technologies:
 * - Vert.x Web for HTTP routing
 * - Asynchronous FX + promo calls
 * - Graceful fallback behavior for promo failures
 */
public class QuoteHttpVerticle extends AbstractVerticle {

    private final FxClient fxClient;
    private final PromoClient promoClient;
    private final LoyaltyCalculator calculator;
    private final int httpPort;

    public QuoteHttpVerticle(
            FxClient fxClient,
            PromoClient promoClient,
            LoyaltyCalculator calculator,
            int httpPort
    ) {
        this.fxClient = fxClient;
        this.promoClient = promoClient;
        this.calculator = calculator;
        this.httpPort = httpPort;
    }

    @Override
    public void start(Promise<Void> promise) {

        Router router = Router.router(vertx);

        router.post("/v1/points/quote")
                .handler(this::handleQuote);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(httpPort)
                .onSuccess(s -> promise.complete())
                .onFailure(promise::fail);
    }
 /**
     * Handles incoming quote requests.
     * Steps:
     *  1. Parse + validate JSON request payload
     *  2. Call FX service
     *  3. Call Promo service (with fallback behavior)
     *  4. Pass all data to LoyaltyCalculator
     *  5. Respond with JSON result
     */
    private void handleQuote(RoutingContext ctx) {
        ctx.request().body().onSuccess(buffer -> {

            QuoteRequest req;
            try {
                req = Json.decodeValue(buffer, QuoteRequest.class);
                ValidationUtil.validate(req);
            } catch (ValidationException ve) {
                fail(ctx, 400, ve.getMessage());
                return;
            } catch (Exception e) {
                fail(ctx, 400, "Invalid JSON");
                return;
            }

            // Fetch FX first
            fxClient.getFxRate(req.getCurrency()).onComplete(fxAr -> {
                if (fxAr.failed()) {
                    fail(ctx, 503, "FX_UNAVAILABLE");
                    return;
                }

                double fxRate = fxAr.result();

                // Fetch promo in parallel but with timeout fallback
                promoClient.getPromoInfo(req.getPromoCode()).onComplete(promoAr -> {

                    PromoInfo promo = null;
                    if (promoAr.failed()) {
                        // Use fallback behavior
                        promo = PromoInfo.unavailable();
                    } else {
                        promo = promoAr.result();
                    }

                    QuoteResponse response = calculator.calculate(req, fxRate, promo);

                    ctx.response()
                            .putHeader("Content-Type", "application/json")
                            .setStatusCode(200)
                            .end(Json.encodePrettily(response));
                });
            });

        }).onFailure(err -> fail(ctx, 400, "Invalid request"));
    }
/**
     * Sends an error response with the given HTTP status and message.
     *
     * @param ctx     routing context managing the HTTP request/response
     * @param status  HTTP status code
     * @param message error message to return in JSON form
     */
    private void fail(RoutingContext ctx, int status, String message) {
        ErrorResponse error = new ErrorResponse(message);
        ctx.response()
                .putHeader("Content-Type", "application/json")
                .setStatusCode(status)
                .end(Json.encodePrettily(error));
    }
}
