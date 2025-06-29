package com.example.DACN.service.impl;
import com.example.DACN.dto.GeminiAssistance.ChatRequest;
import com.example.DACN.dto.GeminiAssistance.ChatResponse;
import com.example.DACN.dto.GeminiAssistance.GeminiRequest;
import com.example.DACN.dto.GeminiAssistance.GeminiResponse;
import com.example.DACN.dto.UserDTO;
import com.example.DACN.model.User;
import com.example.DACN.model.UserInfo;
import com.example.DACN.repository.UsersRepo;
import com.example.DACN.service.interfac.ChatService;
import com.example.DACN.service.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService {
    private final RestTemplate restTemplate;
    private final UsersRepo usersRepo;

    @Value("${gemini.secret.key}")
    private String secretKey;

    public ChatServiceImpl(RestTemplate restTemplate, UsersRepo usersRepo) {
        this.restTemplate = restTemplate;
        this.usersRepo = usersRepo;
    }

    public ChatResponse processChat(ChatRequest chatRequest) {

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + secretKey;

//        boolean check = checkProfileBaseOnUsername(chatRequest.getMessage());
//        if (check) {
//            User user = usersRepo.findUserByUsername(chatRequest.getMessage()).orElseThrow(null);
//            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
//            return new ChatResponse(userDTO.toString());
//        }

        GeminiRequest requestBody = new GeminiRequest(chatRequest.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GeminiRequest> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                GeminiResponse.class
        );

        String reply = response.getBody() != null ? response.getBody().getTextReply() : "Không nhận được phản hồi từ Gemini.";

        return new ChatResponse(reply);
    }

    public Map<String, String> getOptions(String username) {
        User user = usersRepo.findUserByUsername(username).orElse(null);

        if (user!= null) {
            Map<String, String> options = new LinkedHashMap<>();
            String profile = String.format(
                    "Tên tôi là %s, tôi %d tuổi.",
                    user.getUserInfo().getFullName(),
                    LocalDate.now().getYear() - user.getUserInfo().getDob().getYear()
            );

//            options.put("Thông tin của tôi", user.getUsername());
            options.put("Lời khuyên trước khi hiến máu", "Tôi muốn biết các lời khuyên trước khi đi hiến máu.");
//            options.put("SCHEDULE", "Tôi muốn tìm lịch hiến máu gần đây.");
            options.put("Tôi có thể hiến máu chưa?", profile + " Tôi có thể hiến máu chưa?");
            return options;
        }
        return null;
    }

    private boolean checkProfileBaseOnUsername(String username) {
        if (usersRepo.findUserByUsername(username).isPresent()) {
            return true;
        }
        return false;
    }

}
