package com.ssafy.jariyo.domain.waiting.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WaitingResponseGetDto {
    private String storeId;
    private String userId;
    private Integer waitingSequence;
    private Integer waitingUserCount;
    private Boolean waitingIsPostpone;

    private String imageUrl;
}
