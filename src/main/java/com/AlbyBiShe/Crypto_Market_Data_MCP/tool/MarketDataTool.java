package com.AlbyBiShe.Crypto_Market_Data_MCP.tool;

import com.AlbyBiShe.Crypto_Market_Data_MCP.binance.BinanceSpotClient;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;


public class MarketDataTool {

    @Resource
    private BinanceSpotClient binanceSpotClient;


    @Tool(description = "获取价格")
    public Object getTickerPrice(String symbol) {
        String normalized = normalizeSymbol(symbol);
        return binanceSpotClient.getTickerPrice(normalized);
    }

    @Tool(description = "获取市场深度")
    public Object getDepth(String symbol, int limit) {
        String normalized = normalizeSymbol(symbol);
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be positive");
        }
        return binanceSpotClient.getDepth(normalized, limit);
    }

    @Tool(description = "获取K线")
    public Object getKlines(String symbol, String interval, Long startTime, Long endTime, Integer limit) {
        String normalized = normalizeSymbol(symbol);
        if (interval == null || interval.isBlank()) {
            throw new IllegalArgumentException("interval must not be blank");
        }
        if (limit != null && limit <= 0) {
            throw new IllegalArgumentException("limit must be positive when provided");
        }
        if (startTime != null && endTime != null && endTime < startTime) {
            throw new IllegalArgumentException("endTime must be >= startTime");
        }
        return binanceSpotClient.getKlines(normalized, interval, startTime, endTime, limit);
    }

    @Tool(description = "获取交易所信息")
    public Object getExchangeInfo(String symbol) {
        String normalized = symbol == null ? null : normalizeSymbol(symbol);
        return binanceSpotClient.getExchangeInfo(normalized);
    }

    private String normalizeSymbol(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("symbol must not be blank");
        }
        return symbol.trim().toUpperCase();
    }
}
