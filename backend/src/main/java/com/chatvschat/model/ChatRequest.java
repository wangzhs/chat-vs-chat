package com.chatvschat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private String message;
    private String targetAi; // 可以是特定AI的ID，或者为null表示发送给所有启用的AI
} 