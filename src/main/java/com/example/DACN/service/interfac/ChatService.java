package com.example.DACN.service.interfac;

import com.example.DACN.dto.GeminiAssistance.ChatRequest;
import com.example.DACN.dto.GeminiAssistance.ChatResponse;
import com.example.DACN.dto.GeminiAssistance.GeminiResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

public interface ChatService {
    ChatResponse processChat(ChatRequest chatRequest);

    Map<String, String> getOptions(String username);
}
