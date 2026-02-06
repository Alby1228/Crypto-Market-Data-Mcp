package com.AlbyBiShe.Crypto_Market_Data_MCP.binance;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class BinanceSpotClient {

    private final String baseUrl;
    private final HttpClient httpClient;
    private final WeightRateLimiter rateLimiter;

    public BinanceSpotClient(
            @Value("${binance.spot.base-url:https://api.binance.com}") String baseUrl,
            @Value("${binance.spot.max-weight-per-minute:1200}") int maxWeightPerMinute,
            @Value("${binance.spot.request-timeout-ms:10000}") long requestTimeoutMs
    ) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(requestTimeoutMs))
                .build();
        this.rateLimiter = new WeightRateLimiter(maxWeightPerMinute);
    }

    public String getTickerPrice(String symbol) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("symbol", symbol);
        return get("/api/v3/ticker/price", params, 1);
    }

    public String getDepth(String symbol, int limit) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("symbol", symbol);
        params.put("limit", String.valueOf(limit));
        return get("/api/v3/depth", params, depthWeight(limit));
    }

    public String getKlines(String symbol, String interval, Long startTime, Long endTime, Integer limit) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("symbol", symbol);
        params.put("interval", interval);
        if (startTime != null) {
            params.put("startTime", String.valueOf(startTime));
        }
        if (endTime != null) {
            params.put("endTime", String.valueOf(endTime));
        }
        if (limit != null) {
            params.put("limit", String.valueOf(limit));
        }
        return get("/api/v3/klines", params, 1);
    }

    public String getExchangeInfo(String symbol) {
        Map<String, String> params = new LinkedHashMap<>();
        if (symbol != null && !symbol.isBlank()) {
            params.put("symbol", symbol);
        }
        return get("/api/v3/exchangeInfo", params, 10);
    }

    private String get(String path, Map<String, String> params, int weight) {
        rateLimiter.acquire(weight);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path + "?" + buildQuery(params)))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new RuntimeException("Binance API error: " + response.statusCode() + " " + response.body());
            }
            return response.body();
        } catch (IOException ex) {
            throw new RuntimeException("Binance API request failed", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Binance API request interrupted", ex);
        }
    }

    private String buildQuery(Map<String, String> params) {
        if (params.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            if (!sb.isEmpty()) {
                sb.append('&');
            }
            sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            sb.append('=');
            sb.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    private int depthWeight(int limit) {
        if (limit <= 100) {
            return 1;
        }
        if (limit <= 500) {
            return 5;
        }
        if (limit <= 1000) {
            return 10;
        }
        return 50;
    }
}
