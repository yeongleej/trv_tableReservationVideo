package com.ssafy.jariyo.domain.s3image.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ssafy.jariyo.domain.user.entity.User;
import com.ssafy.jariyo.global.config.EmailConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class S3ImageController {

    private final EmailConfig emailConfig;


    private final AmazonS3 amazonS3Client;

    String bucketName = "jariyo-s3";

    @PostMapping("/upload")
    public ResponseEntity<String> writeFreeBoard(User user, @RequestParam(value = "upfile", required = false) MultipartFile[] files) throws IOException {

        if (files != null && files.length > 0 && !files[0].isEmpty()) {
            String today = new SimpleDateFormat("yyMMdd").format(new Date());
            String bucketName = "jariyo-s3";

            for (MultipartFile mfile : files) {
                String originalFileName = mfile.getOriginalFilename();
                if (originalFileName != null && !originalFileName.isEmpty()) {
                    String saveFileName = UUID.randomUUID().toString()
                            + originalFileName.substring(originalFileName.lastIndexOf('.'));

                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(mfile.getSize());
                    amazonS3Client.putObject(new PutObjectRequest(bucketName, today + "/" + saveFileName,
                            mfile.getInputStream(), metadata));

                }
            }
        }

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String content) {
        try {
            emailConfig.sendMail(to, subject, content);
            return ResponseEntity.ok().body("Email has been sent successfully to " + to);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to send email to " + to);
        }
    }
}
