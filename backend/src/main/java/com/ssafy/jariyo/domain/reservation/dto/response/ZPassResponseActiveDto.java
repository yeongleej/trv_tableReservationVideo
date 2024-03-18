package com.ssafy.jariyo.domain.reservation.dto.response;

import com.ssafy.jariyo.global.entity.Status;
import lombok.*;

@NoArgsConstructor
@Getter
@ToString
@AllArgsConstructor
@Builder
public class ZPassResponseActiveDto {
    private Long userId;
    private Long zpassId;
    private Status zpassStatus;
}
