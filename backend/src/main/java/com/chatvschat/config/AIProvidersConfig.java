package com.chatvschat.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AIProvidersConfig {
    private List<AIModelProperties> providers = new ArrayList<>();
} 