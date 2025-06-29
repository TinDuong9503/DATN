package com.example.DACN.controller;

import com.example.DACN.dto.ApiResponse;
import com.example.DACN.service.impl.AwsS3Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.server.EntityResponse;

import java.util.UUID;

@RestController
public class ImageController {

    private final AwsS3Service s3Service;

    public ImageController(AwsS3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file){
        try {
            String imageUrl = s3Service.saveImageToS3(file);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e ) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed");
        }
    }
}
