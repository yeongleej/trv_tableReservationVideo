package com.ssafy.jariyo.domain.s3image.repository;

import com.ssafy.jariyo.domain.review.entity.Review;
import com.ssafy.jariyo.domain.s3image.entity.ReviewS3Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewS3ImageRepository extends JpaRepository<ReviewS3Image, Long> {
    List<ReviewS3Image> findAllByReview(Review review); // Review 엔티티 인스턴스를 기반으로 조회
}