package com.chatvschat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ChatVsChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatVsChatApplication.class, args);
    }
} 