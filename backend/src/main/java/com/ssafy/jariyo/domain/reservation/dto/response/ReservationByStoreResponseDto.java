package com.ssafy.jariyo.domain.reservation.dto.response;

import com.ssafy.jariyo.global.entity.Status;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@ToString
@AllArgsConstructor
@Builder
public class ReservationByStoreResponseDto {
    private Long reservationId;
    private String nickname;
    private LocalDate reservationDate;
    private String reservationTime;
    private Integer reservationUserCount;
    private Integer diningTableNumber;
    private Status status;
}
