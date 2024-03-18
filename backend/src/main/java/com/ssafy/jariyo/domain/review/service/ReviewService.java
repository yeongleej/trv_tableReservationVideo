package com.ssafy.jariyo.domain.review.service;

import com.ssafy.jariyo.domain.review.entity.Review;
import com.ssafy.jariyo.domain.review.dto.ReviewRequestPatchDto;
import com.ssafy.jariyo.domain.review.dto.ReviewRequestPostDto;
import com.ssafy.jariyo.domain.review.dto.ReviewResponseGetDto;
import com.ssafy.jariyo.domain.review.dto.ReviewResponseGetListDto;
import com.ssafy.jariyo.domain.review.repository.ReviewRepository;
import com.ssafy.jariyo.domain.s3image.entity.ReviewS3Image;
import com.ssafy.jariyo.domain.s3image.repository.ReviewS3ImageRepository;
import com.ssafy.jariyo.domain.s3image.service.S3ImageService;
import com.ssafy.jariyo.domain.store.entity.Store;
import com.ssafy.jariyo.domain.store.repository.StoreRepository;
import com.ssafy.jariyo.domain.user.entity.User;
import com.ssafy.jariyo.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final S3ImageService s3ImageService;
    private final ReviewS3ImageRepository reviewS3ImageRepository;

    public void addReview(Long userId, Long storeId, ReviewRequestPostDto reviewRequestPostDto, MultipartFile[] files) throws IOException {

        User user = userRepository.findByUserId(userId);
        Store store = storeRepository.findByStoreId(storeId);

        Review review = new Review(
                user,
                store,
                reviewRequestPostDto.getContent(),
                reviewRequestPostDto.getReviewStar()
        );

        s3ImageService.uploadFilesOnReview(review, files);
        reviewRepository.save(review);
    }

    public ReviewResponseGetDto getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with id: " + reviewId));

        List<ReviewS3Image> images = reviewS3ImageRepository.findAllByReview(review);
        List<String> imageUrls = images.stream()
                .map(image -> image.getS3Image().getFileUrl())
                .collect(Collectors.toList());

        return toDto(review, imageUrls);
    }


    public ReviewResponseGetListDto searchReview(String keyword, Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findAllByReviewContentContaining(keyword, pageable);

        List<ReviewResponseGetDto> dtos = reviewPage.getContent().stream()
                .map(review -> {
                    List<ReviewS3Image> images = reviewS3ImageRepository.findAllByReview(review);
                    List<String> imageUrls = images.stream()
                            .map(image -> image.getS3Image().getFileUrl())
                            .collect(Collectors.toList());
                    return toDto(review, imageUrls);
                })
                .collect(Collectors.toList());

        ReviewResponseGetListDto reviewResponseGetListDto = new ReviewResponseGetListDto();
        reviewResponseGetListDto.setList(dtos);
        reviewResponseGetListDto.setTotalPages(reviewPage.getTotalPages());
        reviewResponseGetListDto.setTotalElements(reviewPage.getTotalElements());

        return reviewResponseGetListDto;
    }

    public void updateReview(Long reviewId, Long userId, ReviewRequestPatchDto reviewUpdateDto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with id: " + reviewId));

        if (!review.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only update your own posts.");
        }

        review.setReviewContent(reviewUpdateDto.getContent());
        review.setReviewLikes(reviewUpdateDto.getReviewLikes());
        review.setReviewStar(reviewUpdateDto.getReviewStar());

        reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with id: " + reviewId));

        if (!review.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own posts.");
        }

        review.setDeleted(true);
        reviewRepository.save(review);
    }

    public static ReviewResponseGetDto toDto(Review review, List<String> imageUrls) {
        if (review == null) {
            return null;
        }

        ReviewResponseGetDto dto = new ReviewResponseGetDto();

        dto.setReviewId(review.getReviewId());
        dto.setUserId(review.getUser().getUserId());
        dto.setUserName(review.getUser().getNickname());
        dto.setContent(review.getReviewContent());
        dto.setRegDate(review.getRegDate());
        dto.setImages(imageUrls);

        return dto;
    }
}
