package com.example.DACN.repository;

import com.example.DACN.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    List<ChatMessage> findByChatSessionSessionIdOrderByTimestampAsc(String sessionId);

}
