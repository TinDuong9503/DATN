package com.example.DACN.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AIResponse {
    private String response;
    private String intentCategory;
    private List<String> suggestedActions;
    private Map<String, Object> metadata;

    public AIResponse(String response){
        this.response = response;
    }

    public AIResponse(String response, String intentCategory, List<String> suggestedActions) {
        this.response = response;
        this.intentCategory = intentCategory;
        this.suggestedActions = suggestedActions;
    }
}
