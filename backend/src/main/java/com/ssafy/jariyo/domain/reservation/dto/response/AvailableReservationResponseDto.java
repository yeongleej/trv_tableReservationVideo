package com.ssafy.jariyo.domain.reservation.dto.response;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@Getter
@ToString
@AllArgsConstructor
@Builder
public class AvailableReservationResponseDto {
    private Long storeId;
    private String time;
    private List<DiningTableResponseDto> diningTableResponseDtoList;
}
