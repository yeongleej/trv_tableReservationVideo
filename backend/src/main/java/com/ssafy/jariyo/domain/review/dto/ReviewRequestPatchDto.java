package com.ssafy.jariyo.domain.review.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class ReviewRequestPatchDto {
    private String content;
    private Integer reviewLikes;
    private Integer reviewStar;
}
