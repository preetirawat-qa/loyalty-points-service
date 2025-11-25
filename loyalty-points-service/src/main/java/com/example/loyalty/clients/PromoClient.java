package com.example.loyalty.clients;

import com.example.loyalty.dto.PromoInfo;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.HttpResponse;

public class PromoClient {

    private final WebClient webClient;
    private final String baseUrl;
    private final Vertx vertx;
    private final long timeoutMs = 300;

    public PromoClient(WebClient webClient, String baseUrl, Vertx vertx) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
        this.vertx = vertx;
    }

    public Future<PromoInfo> getPromoInfo(String promoCode) {
        if (promoCode == null || promoCode.isBlank()) {
            return Future.succeededFuture(PromoInfo.none());
        }

        Promise<PromoInfo> promise = Promise.promise();

        long timerId = vertx.setTimer(timeoutMs, id -> {
            if (!promise.future().isComplete()) {
                promise.tryFail(new RuntimeException("PROMO_TIMEOUT"));
            }
        });

        webClient.getAbs(baseUrl + "/v1/promos/" + promoCode)
                .send(ar -> {
                    vertx.cancelTimer(timerId); // cancel timeout if response received

                    if (ar.failed()) {
                        promise.tryFail(ar.cause());
                        return;
                    }

                    HttpResponse<?> resp = ar.result();

                    if (resp.statusCode() != 200) {
                        promise.tryFail(new RuntimeException("PROMO_BAD_STATUS"));
                        return;
                    }

                    try {
                        PromoInfo info = resp.bodyAsJson(PromoInfo.class);
                        promise.complete(info);
                    } catch (Exception e) {
                        promise.tryFail(e);
                    }
                });

        return promise.future();
    }
}
