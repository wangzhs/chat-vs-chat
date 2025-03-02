package com.chatvschat.controller;

import com.chatvschat.model.AIResponse;
import com.chatvschat.model.ChatRequest;
import com.chatvschat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    
    @PostMapping("/send")
    public CompletableFuture<ResponseEntity<List<AIResponse>>> sendMessage(@RequestBody ChatRequest request) {
        return chatService.processMessage(request.getMessage(), request.getTargetAi())
                .thenApply(ResponseEntity::ok);
    }
} 