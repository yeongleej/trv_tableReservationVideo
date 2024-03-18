package com.ssafy.jariyo.domain.store.dto.response;

import com.ssafy.jariyo.global.entity.Status;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@ToString
@AllArgsConstructor
@Builder
public class StoreResponseDefaultDto {
    private Long storeId;
    private String ownerName;
    private String storeName;
    private String storePhone;
    private String storeDetail;
    private String storeBusinessNumber;
    private String storeMenu;
    private String storeAddress;
    private Integer storeFollowCount;
    private String storeOperationHours;
    private String storeOperationDates;
    private Status storeReservationStatus;
    private Double storePositionX;
    private Double storePositionY;
    private Boolean storeIsWaiting;
    private Double storeRating;
    private String storeBusinessFile;
    private List<String> images;
    private String menu;
}
