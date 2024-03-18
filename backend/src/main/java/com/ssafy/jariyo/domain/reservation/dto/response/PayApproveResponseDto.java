package com.ssafy.jariyo.domain.reservation.dto.response;

import lombok.Getter;

@Getter
public class PayApproveResponseDto {
    private String item_name; // 상품명
    private String created_at; // 결제 요청 시간
    private String approved_at; // 결제 승인 시간
    // 결제 금액 정보
    private int total; // 총 결제 금액
    private int tax_free; // 비과세 금액
    private int tax; // 부가세 금액

}
