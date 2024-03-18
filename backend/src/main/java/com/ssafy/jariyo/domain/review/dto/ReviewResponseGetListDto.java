package com.ssafy.jariyo.domain.review.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReviewResponseGetListDto {
    List<ReviewResponseGetDto> list;
    private int totalPages;
    private long totalElements;
}