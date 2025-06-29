package com.example.DACN.dto.GeminiAssistance;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GeminiResponse {
    private List<Candidate> candidates;

    public String getTextReply() {
        if (candidates == null || candidates.isEmpty()) return "No reply.";
        return candidates.get(0).getContent().getParts().get(0).getText();
    }

    @Data
    public static class Candidate {
        private GeminiRequest.Content content;
    }
}
