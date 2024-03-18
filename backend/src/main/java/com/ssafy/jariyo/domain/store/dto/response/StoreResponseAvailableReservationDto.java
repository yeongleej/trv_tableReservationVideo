package com.ssafy.jariyo.domain.store.dto.response;

import com.ssafy.jariyo.global.entity.Status;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@ToString
@AllArgsConstructor
@Builder
public class StoreResponseAvailableReservationDto {
//    private String storeOperationHours;
//    private String storeOperationDates;
//    private Status storeReservationStatus;
    private List<LocalDateTime> storeReservationAvailableDates;
    private List<String> storeReservationAvailableHours;
}
