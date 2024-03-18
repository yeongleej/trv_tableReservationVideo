package com.ssafy.jariyo.domain.review.dto;
import com.ssafy.jariyo.domain.board.entity.BoardDomain;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReviewResponseGetDto {
    private Long reviewId;
    private Long userId;
    private String userName;
    private List<String> images;
    private String content;
    private LocalDateTime regDate;
}