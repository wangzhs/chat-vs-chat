package com.chatvschat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIResponse {
    private String aiId;
    private String aiName;
    private String content;
    private String error;
    private long responseTimeMs;
    private Instant timestamp;
    
    public static AIResponse success(String aiId, String aiName, String content, long responseTimeMs) {
        return AIResponse.builder()
                .aiId(aiId)
                .aiName(aiName)
                .content(content)
                .responseTimeMs(responseTimeMs)
                .timestamp(Instant.now())
                .build();
    }
    
    public static AIResponse error(String aiId, String aiName, String error, long responseTimeMs) {
        return AIResponse.builder()
                .aiId(aiId)
                .aiName(aiName)
                .error(error)
                .responseTimeMs(responseTimeMs)
                .timestamp(Instant.now())
                .build();
    }
} 