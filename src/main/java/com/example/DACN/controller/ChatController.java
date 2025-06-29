package com.example.DACN.controller;

import com.example.DACN.dto.GeminiAssistance.ChatRequest;
import com.example.DACN.dto.GeminiAssistance.ChatResponse;
import com.example.DACN.service.interfac.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private  ChatService chatService;


    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.processChat(request));
    }

    @GetMapping("/getOptions/{username}")
    public ResponseEntity<Map<String, String>> getOptions(@PathVariable String username) {
        return ResponseEntity.ok(chatService.getOptions(username));
    }
}
