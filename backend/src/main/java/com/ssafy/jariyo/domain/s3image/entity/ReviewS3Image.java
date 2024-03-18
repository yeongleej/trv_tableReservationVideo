package com.ssafy.jariyo.domain.s3image.entity;

import com.ssafy.jariyo.domain.board.entity.Board;
import com.ssafy.jariyo.domain.review.entity.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ReviewS3Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Review review;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private S3Image s3Image;

    public ReviewS3Image(Review review, S3Image s3Image) {
        this.review = review;
        this.s3Image = s3Image;
    }
}
