package com.ssafy.jariyo.domain.store.dto.request;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreRequestInfoDto {

    private String storeDetail;
    private String storeOperationHours;
    private String storeOperationDates;
    private String storeMenu;
    private String storePhone;

}
