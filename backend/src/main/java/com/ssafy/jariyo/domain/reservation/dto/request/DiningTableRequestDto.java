package com.ssafy.jariyo.domain.reservation.dto.request;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
//@NoArgsConstructor
@Builder
public class DiningTableRequestDto {
    private Integer diningTableCapacity;
    private Boolean diningTableIsAvailable;
    private Integer diningTableNumber;
    private String diningTableType;
    private Integer diningTableX;
    private Integer diningTableY;
    private Integer height;
    private Integer width;

}
