package com.chatvschat.service;

import com.chatvschat.config.AIModelProperties;
import com.chatvschat.model.AIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final AIRegistry aiRegistry;
    private final LangChainService langChainService;


    public CompletableFuture<List<AIResponse>> processMessage(String message, String targetAi) {
        if (targetAi != null && !targetAi.isEmpty()) {
            // 发送到特定AI
            return sendToSpecificAI(message, targetAi);
        } else {
            // 发送到所有AI
            return sendToAllAIs(message);
        }
    }

    private CompletableFuture<List<AIResponse>> sendToSpecificAI(String message, String targetAi) {
        Optional<AIModelProperties> aiConfig = aiRegistry.getAIConfig(targetAi);

        if (aiConfig.isEmpty()) {
            log.warn("AI not found: {}", targetAi);
            CompletableFuture<List<AIResponse>> future = new CompletableFuture<>();
            future.complete(List.of(
                    AIResponse.builder()
                            .aiName("System")
                            .content("AI '" + targetAi + "' not found")
                            .build()
            ));
            return future;
        }

        return langChainService.sendMessageToAI(aiConfig.get(), message)
                .thenApply(List::of);
    }

    private CompletableFuture<List<AIResponse>> sendToAllAIs(String message) {
        List<AIModelProperties> allAIs = aiRegistry.getAllAIConfigs();
        List<CompletableFuture<AIResponse>> futures = new ArrayList<>();

        for (AIModelProperties aiConfig : allAIs) {
            futures.add(langChainService.sendMessageToAI(aiConfig, message));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
    }

    /**
     * 处理成语接龙请求
     * @param message 提示信息
     * @param targetAi AI模型名称
     * @return AI返回的成语
     * @throws RuntimeException 如果AI调用失败
     */
    public String chat(String targetAi, String message) {
        try {
            CompletableFuture<List<AIResponse>> future = processMessage(message, targetAi);
            List<AIResponse> responses = future.get(5, TimeUnit.MINUTES); // 设置30秒超时

            if (responses == null || responses.isEmpty()) {
                throw new RuntimeException("No response from AI");
            }

            AIResponse response = responses.get(0);
            if (response == null || response.getContent() == null) {
                throw new RuntimeException("Invalid response from AI");
            }

            return response.getContent();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error while getting AI response", e);
            throw new RuntimeException("AI service error: " + e.getMessage());
        } catch (TimeoutException e) {
            log.error("AI response timeout", e);
            throw new RuntimeException("AI response timeout");
        }
    }
} 