package com.example.DACN.service.interfac;

import com.example.DACN.model.ChatMessage;

import java.util.List;

public interface AIChatService {
    String initializeChat();
    void cleanupOldSessions();
    List<ChatMessage> getChatHistory(String sessionId);
    ChatMessage processUserMessage(String sessionId, String content);
}
