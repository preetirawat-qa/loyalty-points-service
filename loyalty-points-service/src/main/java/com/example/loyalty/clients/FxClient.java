package com.example.loyalty.clients;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
/**
 * FxClient is responsible for retrieving foreign exchange (FX) rates
 * from an external service. It uses Vert.x WebClient for asynchronous
 * HTTP calls and includes a built-in retry mechanism.
 *
 * Behavior:
 * - Calls GET {baseUrl}/v1/fx/{currency}
 * - Validates response format
 * - Retries failed calls up to 3 times with increasing delay
 */

public class FxClient {

    private final WebClient webClient;
    private final String baseUrl;
    private final Vertx vertx;

    public FxClient(WebClient webClient, String baseUrl, Vertx vertx) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
        this.vertx = vertx;
    }

    public Future<Double> getFxRate(String currency) {
        return attempt(currency, 1);
    }

    private Future<Double> attempt(String currency, int attempt) {
        return webClient
                .getAbs(baseUrl + "/v1/fx/" + currency)
                .send()
                .compose(resp -> handleResponse(resp, currency, attempt))
                .recover(err -> retryOrFail(currency, attempt, err));
    }

    private Future<Double> handleResponse(
            HttpResponse<?> resp,
            String currency,
            int attempt
    ) {
        if (resp.statusCode() != 200) {
            return retryOrFail(currency, attempt,
                    new RuntimeException("Bad FX response " + resp.statusCode()));
        }

        JsonObject body = resp.bodyAsJsonObject();
        if (body == null || !body.containsKey("rate")) {
            return Future.failedFuture("Invalid FX payload");
        }
        return Future.succeededFuture(body.getDouble("rate"));
    }

    /**
     * Retries the FX request when possible, otherwise fails.
     *
     * Retry rules:
     * - Max 3 attempts
     * - Backoff increments: 100ms, 200ms, 300ms
     *
     * @param currency currency code
     * @param attempt  current attempt number
     * @param err      failure reason
     * @return Future retrying or failing permanently
     */
    private Future<Double> retryOrFail(String currency, int attempt, Throwable err) {
        if (attempt >= 3) {
            return Future.failedFuture(err);
        }

        long delay = attempt * 100; // 100ms, 200ms, 300ms

        return vertx
                .timer(delay)
                .compose(v -> attempt(currency, attempt + 1));
    }
}
