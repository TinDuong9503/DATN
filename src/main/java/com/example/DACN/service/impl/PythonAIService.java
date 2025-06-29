package com.example.DACN.service.impl;

import com.example.DACN.dto.ai.AIRequest;
import com.example.DACN.dto.ai.AIResponse;
import com.example.DACN.dto.ai.UserDataResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class PythonAIService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public PythonAIService(WebClient.Builder webClientBuilder,
            @Value("${python.ai.service.url}") String pythonServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(pythonServiceUrl).build();
        this.objectMapper = new ObjectMapper();
    }

    public Mono<AIResponse> processUserQuery(String username, String message) {
        AIRequest request = new AIRequest(username, message);

        return webClient.post()
                .uri("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AIResponse.class)
                .timeout(Duration.ofSeconds(30))
                .onErrorReturn(new AIResponse("Xin lỗi, AI service hiện tại không khả dụng."));
    }

    public Mono<UserDataResponse> getUserDataForAI(String username) {
        return webClient.get()
                .uri("/user-data/{username}", username)
                .retrieve()
                .bodyToMono(UserDataResponse.class);
    }
}
