package com.chatvschat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai.providers")
public class AIModelProperties {
    private String id;
    private String name;
    private String type;
    private String description;
    private String apiEndpoint;
    private String apiKey;
    private String model;
    private boolean enabled;
} 