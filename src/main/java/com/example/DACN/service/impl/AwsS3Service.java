package com.example.DACN.service.impl;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.DACN.exception.OurException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service

public class AwsS3Service {

    //    private final String bucketName = "phegon-hotel-images";
    private final String bucketName = "hirot-donation-images";

    @Value("${aws.s3.access.key}")
    private String awsS3AccessKey;

    @Value("${aws.s3.secret.key}")
    private String awsS3SecretKey;

    private AmazonS3 s3Client;

    @PostConstruct
    private void init() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsS3AccessKey, awsS3SecretKey);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.AP_SOUTHEAST_1)
                .build();
    }

    public String saveImageToS3(MultipartFile photo) {
        try {
            String s3Filename = UUID.randomUUID() + "_" + photo.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(photo.getContentType());
            metadata.setContentLength(photo.getSize());

            s3Client.putObject(new PutObjectRequest(bucketName, s3Filename, photo.getInputStream(), metadata));
            return getFileUrl(s3Filename);

        } catch (Exception e) {
            e.printStackTrace();
            throw new OurException("Unable to upload image to S3: " + e.getMessage());
        }
    }

    public String updateImageToS3(MultipartFile newPhoto, String oldPhotoUrl) {
        try {
            // Xoá ảnh cũ
            deleteImageFromS3(oldPhotoUrl);

            // Upload ảnh mới
            return saveImageToS3(newPhoto);

        } catch (Exception e) {
            e.printStackTrace();
            throw new OurException("Unable to update image on S3: " + e.getMessage());
        }
    }

    public void deleteImageFromS3(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return;

        try {
            String key = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            if (s3Client.doesObjectExist(bucketName, key)) {
                s3Client.deleteObject(bucketName, key);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new OurException("Unable to delete image from S3: " + e.getMessage());
        }
    }

    private String getFileUrl(String key) {
        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }
}