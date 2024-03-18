package com.ssafy.jariyo.domain.s3image.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ssafy.jariyo.domain.board.entity.Board;
import com.ssafy.jariyo.domain.review.entity.Review;
import com.ssafy.jariyo.domain.s3image.entity.BoardS3Image;
import com.ssafy.jariyo.domain.s3image.entity.ReviewS3Image;
import com.ssafy.jariyo.domain.s3image.entity.S3Image;
import com.ssafy.jariyo.domain.s3image.entity.StoreS3Image;
import com.ssafy.jariyo.domain.s3image.repository.BoardS3ImageRepository;
import com.ssafy.jariyo.domain.s3image.repository.ReviewS3ImageRepository;
import com.ssafy.jariyo.domain.s3image.repository.S3ImageRepository;
import com.ssafy.jariyo.domain.s3image.repository.StoreS3ImageRepository;
import com.ssafy.jariyo.domain.store.entity.Store;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
@RequiredArgsConstructor
public class S3ImageService {
    private static final Logger logger = LoggerFactory.getLogger(S3ImageService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final AmazonS3 amazonS3Client;
    private final S3ImageRepository s3ImageRepository;
    private final BoardS3ImageRepository boardS3ImageRepository;
    private final ReviewS3ImageRepository reviewS3ImageRepository;
    private final StoreS3ImageRepository storeS3ImageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public void uploadFilesOnBoard(Board board, MultipartFile[] files) {
        if (files == null || files.length == 0) return;

        String today = LocalDate.now().format(DATE_FORMATTER);

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            String originalFileName = Optional.ofNullable(file.getOriginalFilename()).orElse("unnamed");
            String saveFileName = UUID.randomUUID() + extractExtension(originalFileName);
            String fileKey = today + "/" + saveFileName;

            try {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(file.getSize());
                amazonS3Client.putObject(new PutObjectRequest(bucketName, fileKey, file.getInputStream(), metadata));

                String fileUrl = amazonS3Client.getUrl(bucketName, fileKey).toString();

                S3Image image = new S3Image(fileUrl, originalFileName);
                s3ImageRepository.save(image);

                BoardS3Image boardS3Image = new BoardS3Image(board, image);
                boardS3ImageRepository.save(boardS3Image);

                logger.info("File uploaded successfully: {} with URL: {}", saveFileName, fileUrl);
            } catch (IOException e) {
                logger.error("Failed to upload file: {}", originalFileName, e);
            } catch (AmazonServiceException e) {
                logger.error("Amazon service error: {}", e.getErrorMessage(), e);
            } catch (SdkClientException e) {
                logger.error("Amazon SDK client error: {}", e.getMessage(), e);
            }
        }
    }

    public void uploadFilesOnReview(Review review, MultipartFile[] files) {
        if (files == null || files.length == 0) return;

        String today = LocalDate.now().format(DATE_FORMATTER);

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            String originalFileName = Optional.ofNullable(file.getOriginalFilename()).orElse("unnamed");
            String saveFileName = UUID.randomUUID() + extractExtension(originalFileName);
            String fileKey = today + "/" + saveFileName;

            try {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(file.getSize());
                amazonS3Client.putObject(new PutObjectRequest(bucketName, fileKey, file.getInputStream(), metadata));

                String fileUrl = amazonS3Client.getUrl(bucketName, fileKey).toString();

                S3Image image = new S3Image(fileUrl, originalFileName);
                s3ImageRepository.save(image);

                ReviewS3Image reviewS3Image = new ReviewS3Image(review, image);
                reviewS3ImageRepository.save(reviewS3Image);

                logger.info("File uploaded successfully: {} with URL: {}", saveFileName, fileUrl);
            } catch (IOException e) {
                logger.error("Failed to upload file: {}", originalFileName, e);
            } catch (AmazonServiceException e) {
                logger.error("Amazon service error: {}", e.getErrorMessage(), e);
            } catch (SdkClientException e) {
                logger.error("Amazon SDK client error: {}", e.getMessage(), e);
            }
        }
    }

    public void uploadFilesOnStore(Store store, MultipartFile file) {

        if (file == null) return;

        String today = LocalDate.now().format(DATE_FORMATTER);

        String originalFileName = Optional.ofNullable(file.getOriginalFilename()).orElse("unnamed");
        String saveFileName = UUID.randomUUID() + extractExtension(originalFileName);
        String fileKey = today + "/" + saveFileName;

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileKey, file.getInputStream(), metadata));

            String fileUrl = amazonS3Client.getUrl(bucketName, fileKey).toString();

            S3Image image = new S3Image(fileUrl, originalFileName);
            s3ImageRepository.save(image);

            StoreS3Image storeS3Image = new StoreS3Image(store, image);
            storeS3ImageRepository.save(storeS3Image);

            logger.info("File uploaded successfully: {} with URL: {}", saveFileName, fileUrl);
        } catch (IOException e) {
            logger.error("Failed to upload file: {}", originalFileName, e);
        } catch (AmazonServiceException e) {
            logger.error("Amazon service error: {}", e.getErrorMessage(), e);
        } catch (SdkClientException e) {
            logger.error("Amazon SDK client error: {}", e.getMessage(), e);
        }

    }

    public String uploadEmployFile(MultipartFile file) {

        if (file == null) return null;

        String today = LocalDate.now().format(DATE_FORMATTER);
        String originalFileName = Optional.ofNullable(file.getOriginalFilename()).orElse("unnamed");
        String saveFileName = UUID.randomUUID() + extractExtension(originalFileName);
        String fileKey = "employ/" + today + "/" + saveFileName;

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileKey, file.getInputStream(), metadata);
            amazonS3Client.putObject(putObjectRequest);

            String fileUrl = amazonS3Client.getUrl(bucketName, fileKey).toString();
            logger.info("File uploaded successfully: {} with URL: {}", saveFileName, fileUrl);

            return fileUrl;
        } catch (AmazonServiceException e) {
            logger.error("Amazon service error: {}", e.getErrorMessage(), e);
            return null;
        } catch (SdkClientException e) {
            logger.error("Amazon SDK client error: {}", e.getMessage(), e);
            return null;
        } catch (IOException e) {
            logger.error("Failed to upload file: {}", originalFileName, e);
            throw new RuntimeException(e);
        }
    }

    public String uploadTumbnail(MultipartFile file) {

        if (file == null) return null;

        String today = LocalDate.now().format(DATE_FORMATTER);
        String originalFileName = Optional.ofNullable(file.getOriginalFilename()).orElse("unnamed");
        String saveFileName = UUID.randomUUID() + extractExtension(originalFileName);
        String fileKey = "uploadTumbnail/" + today + "/" + saveFileName;

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileKey, file.getInputStream(), metadata);
            amazonS3Client.putObject(putObjectRequest);

            String fileUrl = amazonS3Client.getUrl(bucketName, fileKey).toString();
            logger.info("File uploaded successfully: {} with URL: {}", saveFileName, fileUrl);

            return fileUrl;
        } catch (AmazonServiceException e) {
            logger.error("Amazon service error: {}", e.getErrorMessage(), e);
            return null;
        } catch (SdkClientException e) {
            logger.error("Amazon SDK client error: {}", e.getMessage(), e);
            return null;
        } catch (IOException e) {
            logger.error("Failed to upload file: {}", originalFileName, e);
            throw new RuntimeException(e);
        }
    }

    private String extractExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex != -1 ? fileName.substring(dotIndex) : "";
    }
}
