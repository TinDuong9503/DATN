package com.example.DACN.service.interfac;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface IVisionService {
    Map<String, String> extractFieldsFromImage(MultipartFile file) throws IOException;
}
