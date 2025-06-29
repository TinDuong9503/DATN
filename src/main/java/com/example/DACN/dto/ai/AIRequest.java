package com.example.DACN.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AIRequest {
    private String username;
    private String message;
    private Map<String,String> context;

    public AIRequest(String username, String message) {
        this.username= username;
        this.message = message;
    }
}
