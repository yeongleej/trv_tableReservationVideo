package com.ssafy.jariyo.domain.waiting.dto;

import com.ssafy.jariyo.global.entity.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WaitingRequestPatchDto {
    private Integer waitingSequence;
    private Integer waitingUserCount;
    private Boolean waitingIsPostpone;
}
