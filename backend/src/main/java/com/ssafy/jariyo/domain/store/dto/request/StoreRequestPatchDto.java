package com.ssafy.jariyo.domain.store.dto.request;

import com.ssafy.jariyo.global.entity.Status;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StoreRequestPatchDto {
    private String storePhone;
    private String storeDetail;
    private boolean storeIsWaiting;
    private String storeOperationHours;
    private Status storeReservationStatus;
    private String storeReservationAvailableDates;
}
