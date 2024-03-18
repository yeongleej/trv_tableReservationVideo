package com.ssafy.jariyo.domain.reservation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.LinkedMultiValueMap;

@Getter
@ToString
@AllArgsConstructor
public class PayRequest {
    private String url;
    private LinkedMultiValueMap<String,String> map;
}
