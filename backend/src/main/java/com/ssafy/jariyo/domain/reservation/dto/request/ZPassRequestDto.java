package com.ssafy.jariyo.domain.reservation.dto.request;

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
public class ZPassRequestDto {
    private Long storeId;
    private Long userId;
    private Integer zpassQuantity;
}
