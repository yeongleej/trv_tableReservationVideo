package com.ssafy.jariyo.domain.store.dto.response;

import com.ssafy.jariyo.domain.store.entity.Store;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StoreResponseGetDto {
    private Long storeId;
    private String storeName;
    private String storePhone;
    private String storeDetail;
    private String storeAddress;
    private boolean storeIsWaiting;
    private int storeFollowCount;
    private String storeOperationHours;
    private String storeReservationStatus;
    private String storeReservationAvailableDates;
    private Double storePositionX;
    private Double storePositionY;

    private String storeImage;
}
