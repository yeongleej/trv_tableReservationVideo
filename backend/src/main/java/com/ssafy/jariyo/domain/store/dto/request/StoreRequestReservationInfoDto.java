package com.ssafy.jariyo.domain.store.dto.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreRequestReservationInfoDto {
    private List<LocalDateTime> storeReservationAvailableDates;
    private List<String> storeReservationAvailableHours;
}
