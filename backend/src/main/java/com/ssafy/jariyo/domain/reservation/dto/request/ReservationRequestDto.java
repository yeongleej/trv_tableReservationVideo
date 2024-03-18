package com.ssafy.jariyo.domain.reservation.dto.request;

import com.ssafy.jariyo.domain.reservation.entity.DiningTable;
import com.ssafy.jariyo.domain.store.entity.Store;
import com.ssafy.jariyo.domain.user.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@ToString
@AllArgsConstructor
@Builder
public class ReservationRequestDto {
    private Long storeId;
    private Integer diningTableNumber;
    private String reservationDate;
    private String reservationTime;
    private Integer reservationUserCount;
    private Long zpassId;
}
