package com.chatvschat.service;

import com.chatvschat.config.AIModelProperties;
import com.chatvschat.config.AIProvidersConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIRegistry {
    @Resource
    private AIProvidersConfig aiProvidersConfig;

    private final Map<String, AIModelProperties> aiRegistry = new HashMap<>();

    @PostConstruct
    public void init() {
        loadAIConfigs();
    }

    public void loadAIConfigs() {
        aiRegistry.clear();

        for (AIModelProperties provider : aiProvidersConfig.getProviders()) {
            if (provider.isEnabled()) {
                aiRegistry.put(provider.getName().toLowerCase(), provider);
                log.info("Registered AI: {}", provider.getName());
            }
        }

        log.info("Loaded {} AI providers", aiRegistry.size());
    }

    public Optional<AIModelProperties> getAIConfig(String name) {
        return Optional.ofNullable(aiRegistry.get(name.toLowerCase()));
    }

    public List<AIModelProperties> getAllAIConfigs() {
        return new ArrayList<>(aiRegistry.values());
    }
}