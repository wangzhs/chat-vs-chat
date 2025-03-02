package com.chatvschat.service;

import com.chatvschat.config.AIModelProperties;
import com.chatvschat.model.AIResponse;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LangChainService {

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Map<String, ChatLanguageModel> aiModels;


    /**
     * 发送消息到指定的AI
     *
     * @param aiModelProperties AI配置
     * @param message           用户消息
     * @return AI响应
     */
    public CompletableFuture<AIResponse> sendMessageToAI(AIModelProperties aiModelProperties, String message) {
        log.info("Sending message to AI: {}, Message: {}", aiModelProperties.getName(), message);

        return CompletableFuture.supplyAsync(() -> {
            try {
                // 根据AI ID获取对应的LangChain4j模型
                ChatLanguageModel model = getModelForAI(aiModelProperties);
                if (model == null) {
                    log.error("No LangChain4j model available for AI: {}", aiModelProperties.getName());
                    return AIResponse.builder()
                            .aiName(aiModelProperties.getName())
                            .content("Error: No model available for " + aiModelProperties.getName())
                            .build();
                }

                // 发送请求并获取响应
                Response<AiMessage> response = model.generate(List.of(UserMessage.from(message)));
                String content = response.content().text();

                return AIResponse.builder()
                        .aiName(aiModelProperties.getName())
                        .content(content)
                        .build();
            } catch (Exception e) {
                log.error("Error communicating with AI: {}, Error: {}", aiModelProperties.getName(), e.getMessage(), e);
                return AIResponse.builder()
                        .aiName(aiModelProperties.getName())
                        .content("Error: Unable to get response from " + aiModelProperties.getName() + ". " + e.getMessage())
                        .build();
            }
        }, executorService);
    }

    /**
     * 根据AI配置获取对应的LangChain4j模型
     *
     * @param aiConfig AI配置
     * @return LangChain4j模型
     */
    private ChatLanguageModel getModelForAI(AIModelProperties aiConfig) {

        try {
            return aiModels.get(aiConfig.getId());
        } catch (Exception e) {
            log.error("Error getting model bean for AI: {}, Bean name: {}, Error: {}",
                    aiConfig.getName(), aiConfig.getId(), e.getMessage());
            return null;
        }
    }
} 