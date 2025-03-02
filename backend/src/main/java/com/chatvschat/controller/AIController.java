package com.chatvschat.controller;

import com.chatvschat.config.AIModelProperties;
import com.chatvschat.service.AIRegistry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@Slf4j
@RequiredArgsConstructor
public class AIController {

    private final AIRegistry aiRegistry;

    @GetMapping("/list")
    public ResponseEntity<List<AIModelProperties>> listAIs() {
        return ResponseEntity.ok(aiRegistry.getAllAIConfigs());
    }

} 