package com.AlbyBiShe.Crypto_Market_Data_MCP;

import com.AlbyBiShe.Crypto_Market_Data_MCP.tool.MarketDataTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CryptoMarketDataMcpApplication {



	public static void main(String[] args) {
		SpringApplication.run(CryptoMarketDataMcpApplication.class, args);
	}

    @Bean
    public ToolCallbackProvider marketDataTool() {
        return MethodToolCallbackProvider.builder().toolObjects(new MarketDataTool()).build();
    }

}
