package com.example.appcore.controller;

import com.example.appcore.service.S3Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/s3")
@CrossOrigin(origins = "*")
public class S3Controller {

    @Autowired
    private S3Uploader s3Uploader;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            String bucketName = "appcore-cursos-videos";
            String key = "videos/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

            s3Uploader.getS3Client().putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes())
            );

            String videoUrl = "https://" + bucketName + ".s3.sa-east-1.amazonaws.com/" + key;

            Map<String, String> response = new HashMap<>();
            response.put("videoUrl", videoUrl);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage())); 
        }
    }
}
