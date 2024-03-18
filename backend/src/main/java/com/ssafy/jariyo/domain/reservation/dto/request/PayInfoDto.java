package com.ssafy.jariyo.domain.reservation.dto.request;

import lombok.*;

@NoArgsConstructor
@Getter
@ToString
@AllArgsConstructor
@Builder
public class PayInfoDto {
    private Integer price;
    private String itemName;
    private String redirectUri;
}
