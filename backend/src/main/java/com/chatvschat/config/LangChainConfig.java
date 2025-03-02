package com.chatvschat.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class LangChainConfig {


    @Bean
    @ConfigurationProperties(prefix = "ai")
    public AIProvidersConfig aiProvidersConfig() {
        return new AIProvidersConfig();
    }

    @Bean(name = "aiModels")
    public Map<String, ChatLanguageModel> aiModels(AIProvidersConfig aiProvidersConfig) {
        Map<String, ChatLanguageModel> models = new HashMap<>();

        // 从配置的AI模型属性中创建模型配置
        for (AIModelProperties properties : aiProvidersConfig.getProviders()) {
            if (properties.isEnabled() && properties.getApiKey() != null && !properties.getApiKey().isEmpty()) {
                models.put(properties.getId(), createModelConfig(properties));
            }
        }

        return models;
    }

    private ChatLanguageModel createModelConfig(AIModelProperties properties) {
        return OpenAiChatModel.builder()
                .baseUrl(properties.getApiEndpoint())
                .apiKey(properties.getApiKey())
                .modelName(properties.getModel())
                .temperature(0.6)
                .maxTokens(4096)
                .topP(0.6)
                .timeout(Duration.ofMinutes(5))
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}