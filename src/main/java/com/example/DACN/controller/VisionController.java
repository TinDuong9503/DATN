package com.example.DACN.controller;

import com.example.DACN.service.interfac.IVisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/ocr")
public class VisionController {
    @Autowired
    private IVisionService visionService;

    @PostMapping("/extract")
    public ResponseEntity<Map<String, String>> extractFields(@RequestParam("file") MultipartFile file) {
        try {
            // Call service to extract fields from the image
            Map<String, String> extractedData = visionService.extractFieldsFromImage(file);

            // Return the extracted data as JSON
            return ResponseEntity.ok(extractedData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error extracting text from image: " + e.getMessage()));
        }
    }
}

