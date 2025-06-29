package com.example.DACN.controller;

import com.example.DACN.dto.ai.AIRequest;
import com.example.DACN.dto.ai.AIResponse;
import com.example.DACN.dto.ai.UserDataResponse;
import com.example.DACN.service.impl.PythonAIService;
import com.example.DACN.service.impl.UserManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AIChatController {

    @Autowired
    private PythonAIService pythonAIService;

    @Autowired
    private UserManagementService userService;

    @PostMapping("/chat-agent")
    public Mono<ResponseEntity<AIResponse>> chat(@RequestBody AIRequest request) {
        return pythonAIService.processUserQuery(request.getUsername(), request.getMessage())
                .map(aiResponse -> {
                    AIResponse response = new AIResponse(
                            aiResponse.getResponse(),
                            aiResponse.getIntentCategory(),
                            aiResponse.getSuggestedActions()
                    );
                    return ResponseEntity.ok(response);
                })
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new AIResponse("Đã xảy ra lỗi khi xử lý yêu cầu.", null, null)));
    }

    @GetMapping("/user-data/{username}")
    public ResponseEntity<UserDataResponse> getUserData(@PathVariable String username) {
        try {
            UserDataResponse userData = userService.getUserDataForAI(username);
            return ResponseEntity.ok(userData);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}