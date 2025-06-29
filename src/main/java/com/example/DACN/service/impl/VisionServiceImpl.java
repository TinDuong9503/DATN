package com.example.DACN.service.impl;

import com.example.DACN.service.interfac.IVisionService;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.vision.v1.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class VisionServiceImpl implements IVisionService {

    @Override
    public Map<String, String> extractFieldsFromImage(MultipartFile file) throws IOException {

        Map<String, String> extractedFields = new HashMap<>();
        try (ImageAnnotatorClient visionClient = ImageAnnotatorClient.create()) {
            // Read the image file
            ByteString imgBytes = ByteString.copyFrom(file.getBytes());
            Image img = Image.newBuilder().setContent(imgBytes).build();

            // Set up the feature for text detection
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            ImageContext context = ImageContext.newBuilder().addLanguageHints("vi").build();

            // Build the request for text detection
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .setImageContext(context)
                    .build();

            // Send the request and get the response
            BatchAnnotateImagesResponse response = visionClient.batchAnnotateImages(List.of(request));
            AnnotateImageResponse imageResponse = response.getResponses(0);

            // Check for any errors in the response
            if (imageResponse.hasError()) {
                throw new IOException("Vision API Error: " + imageResponse.getError().getMessage());
            }

            // Get the full text extracted from the image
            String fullText = imageResponse.getFullTextAnnotation().getText();

            extractedFields = extractFields(fullText);
        } catch (Exception e) {
            throw new IOException("Error occurred while processing the image", e);
        }

        return extractedFields;
    }
    private String extractAfterColon(String line) {
        int colonIndex = line.indexOf(":");
        if (colonIndex != -1 && colonIndex + 1 < line.length()) {
            return line.substring(colonIndex + 1).trim();
        }
        return "";
    }

    private Map<String, String> extractFields(String ocrText) {
        Map<String, String> data = new HashMap<>();

        // Gom nhóm theo từng dòng
        String[] lines = ocrText.split("\\r?\\n");
        List<String> filteredLines = Arrays.stream(lines)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        // Duyệt từng dòng và dùng từ khóa để ánh xạ
        StringBuilder fullNameBuilder = new StringBuilder();
        for (int i = 0; i < filteredLines.size(); i++) {
            String line = filteredLines.get(i);

            if (line.toLowerCase().contains("so/no") || line.toLowerCase().contains("no") || line.toLowerCase().contains("số")) {
                data.put("cccd", extractAfterColon(line));
            } else if (line.toLowerCase().contains("họ và tên") || line.toLowerCase().contains("full name")) {
                // Có thể có nhiều dòng tên phía sau
                int j = i + 1;
                while (j < filteredLines.size()
                        && !filteredLines.get(j).toLowerCase().contains("ngày sinh")) {
                    fullNameBuilder.append(filteredLines.get(j)).append(" ");
                    j++;
                }
                data.put("fullName", fullNameBuilder.toString().trim());
            } else if (line.toLowerCase().contains("ngày sinh")) {
                data.put("dateOfBirth", extractAfterColon(line));
            } else if (line.toLowerCase().contains("giới tính") && line.toLowerCase().contains("quốc tịch")) {
                // Tách giới tính và quốc tịch từ 1 dòng
                Pattern pattern = Pattern.compile("Giới tính.*?:\\s*(.*?)\\s+Quốc tịch.*?:\\s*(.*)");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String gender = matcher.group(1).trim();
                    String nationality = matcher.group(2).trim();
                    data.put("gender", gender);
                    data.put("nationality", nationality);
                }
            } else if (line.toLowerCase().contains("quê quán") || line.toLowerCase().contains("place of origin")) {
                data.put("placeOfOrigin", extractAfterColon(line));
            } else if (line.toLowerCase().contains("nơi thường trú") || line.toLowerCase().contains("place of residence")) {
                data.put("placeOfResidence", extractAfterColon(line));
            }
        }

        return data;
    }
//    private Map<String, String> extractFields(String fullText) {
//        Map<String, String> extractedData = new HashMap<>();
//
//        // Assuming the full text contains all necessary fields in a predictable order
//        String[] lines = fullText.split("\n");
//
//        extractedData.put("CCCD", extractCCCD(lines));
//        extractedData.put("fullName", extractFullName(lines));
//        extractedData.put("dateOfBirth", extractDateOfBirth(lines));
//        extractedData.put("sex", extractSex(lines));
//
//        return extractedData;
//    }

    private String extractCCCD(String[] lines) {
        return lines[8].split(":")[1].trim();
    }

    private String extractFullName(String[] lines) {
        // Based on the provided text, we assume that the full name is at line 3
        return lines[10].trim();  // "DƯƠNG NGUYỄN CHÍ TÍN"
    }

    private String extractDateOfBirth(String[] lines) {
        // Assuming date of birth is at line 4
        return lines[11].split(":")[1].trim();  // "09/05/2003"
    }

    private String extractSex(String[] lines) {
        // Assuming sex is at line 5
        return lines[12].split(":")[1].trim();  // "Nam"
    }

    private String extractNationality(String[] lines) {
        // Assuming nationality is at line 6
        return lines[5].split(":")[1].trim();  // "Việt Nam"
    }

    private String extractPlaceOfOrigin(String[] lines) {
        // Assuming place of origin is at line 7
        return lines[6].split(":")[1].trim();  // "Định Thành, Thoại Sơn, An Giang"
    }

    private String extractPlaceOfResidence(String[] lines) {
        // Assuming place of residence is at line 8
        return lines[7].split(":")[1].trim();  // "An Hưng, Mỹ Thới, Thành phố Long Xuyên, An Giang"
    }

}
