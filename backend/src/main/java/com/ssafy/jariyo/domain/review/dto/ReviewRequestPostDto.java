package com.ssafy.jariyo.domain.review.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ReviewRequestPostDto {
    private String storeId;
    private String content;
    private Integer reviewStar;
    private MultipartFile[] upfile;
}
