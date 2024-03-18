package com.ssafy.jariyo.domain.reservation.dto.response;

import com.ssafy.jariyo.global.entity.Status;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@ToString
@AllArgsConstructor
@Builder
public class ZPassResponseDto {
    private Long zpassId;
    private String zpassName;
    private Integer zpassQuantity;
    private Status zpassStatus;
//    private LocalDateTime zpassExpirationDate;
}
