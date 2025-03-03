package com.chatvschat.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.chatvschat.service.ChatService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@RestController
@RequestMapping("/api/idiom")
@AllArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class IdiomController {

    private final ChatService chatService;

    @PostMapping("/next")
    public ResponseEntity<?> getNextIdiom(@RequestBody IdiomRequest request) {
        try {
            log.info("收到成语接龙请求: previousIdiom={}, aiPlayer={}", request.getPreviousIdiom(), request.getAiPlayer());
            
            // 参数验证
            if (!StringUtils.hasText(request.getPreviousIdiom())) {
                return ResponseEntity.badRequest().body(new ErrorResponse("成语不能为空"));
            }
            if (!StringUtils.hasText(request.getAiPlayer())) {
                return ResponseEntity.badRequest().body(new ErrorResponse("AI类型不能为空"));
            }

            // 构建AI提示词
            String prompt = String.format(
                "现在我们在玩成语接龙游戏，上一个成语是：%s。" +
                "请你接一个成语，要求：\n" +
                "1. 必须是标准的成语（四字词语）\n" +
                "2. 第一个字必须和上一个成语的最后一个字的读音相同\n" +
                "3. 只返回这个成语，不要其他任何解释\n",
                request.getPreviousIdiom()
            );

            log.debug("发送到AI的提示词: {}", prompt);

            // 调用AI服务
            String response = chatService.chat(request.getAiPlayer(), prompt);
            log.debug("AI返回的原始响应: {}", response);
            
            // 处理响应
            String idiom = response.trim()
                .replaceAll("[\n\r]", "")  // 移除换行符
                .replaceAll("[\"'\"\"]+", "");  // 移除所有引号
            
            log.debug("处理后的成语: {}", idiom);
            
            // 验证返回的成语
            if (!isValidIdiom(idiom)) {
                log.warn("AI返回了无效的成语: {}", idiom);
                return ResponseEntity.ok(new ErrorResponse("AI返回的不是有效的成语：" + idiom));
            }

            log.info("成功生成成语: {}", idiom);
            return ResponseEntity.ok(new IdiomResponse(idiom));
        } catch (Exception e) {
            log.error("成语接龙处理失败: {}", e.getMessage(), e);
            String errorMessage = e.getMessage().contains("not found") 
                ? "未找到指定的AI模型: " + request.getAiPlayer()
                : "获取成语失败: " + e.getMessage();
            return ResponseEntity.internalServerError()
                .body(new ErrorResponse(errorMessage));
        }
    }

    private boolean isValidIdiom(String idiom) {
        if (idiom == null) return false;
        // 简单验证：去除所有空白字符后长度为4
        String cleaned = idiom.replaceAll("\\s+", "");
        return cleaned.length() == 4 && cleaned.matches("^[\\u4e00-\\u9fa5]{4}$");
    }
}

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
class IdiomRequest {
    private String previousIdiom;
    private String aiPlayer;
}

@lombok.Data
@lombok.AllArgsConstructor
class IdiomResponse {
    private String idiom;
}

@lombok.Data
@lombok.AllArgsConstructor
class ErrorResponse {
    private String error;
} 