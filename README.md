## Crypto Market Data MCP

基于 Spring AI MCP Server 的加密市场数据服务，面向大模型/代理提供 Binance Spot 行情能力（最新价、深度、K 线、交易所信息），内置权重限速以符合 Binance 规则。

## 功能

- Binance Spot 行情数据获取（ticker、depth、klines、exchangeInfo）
- 客户端权重限速（按分钟配额）
- 统一 SSE 接入，适配 MCP 工具调用

## 运行环境

- JDK 17
- Maven（内置 `mvnw` 可直接使用）

## 快速开始

```bash
./mvnw spring-boot:run
```

默认端口：`8082`  
SSE 接入地址：`http://localhost:8082/api/v1/sse`  
MCP 消息地址：`http://localhost:8082/api/v1/mcp`

## 工具列表（MCP Tools）

工具注册在 `MarketDataTool`，参数说明如下：

- `getTickerPrice(symbol)`
  - `symbol`：交易对，例如 `BTCUSDT`
- `getDepth(symbol, limit)`
  - `limit`：深度档位（>0）
- `getKlines(symbol, interval, startTime, endTime, limit)`
  - `interval`：K 线周期，例如 `1m`、`5m`、`1h`
  - `startTime`/`endTime`：毫秒时间戳，可选
  - `limit`：条数，可选（>0）
- `getExchangeInfo(symbol)`
  - `symbol` 可选，不填返回全量信息

说明：
- `symbol` 会自动转为大写并去空格
- `startTime` 和 `endTime` 同时传入时需满足 `endTime >= startTime`

## 配置项

配置文件：`src/main/resources/application.yaml`

```yaml
binance:
  spot:
    base-url: https://api.binance.com
    max-weight-per-minute: 1200
    request-timeout-ms: 10000
server:
  port: 8082
```

## 参考

- Binance Spot REST API 限流规则  
  https://developers.binance.com/docs/binance-spot-api-docs/rest-api/limits
