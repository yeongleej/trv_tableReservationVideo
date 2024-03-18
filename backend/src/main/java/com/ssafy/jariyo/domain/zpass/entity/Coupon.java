package com.ssafy.jariyo.domain.zpass.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Coupon {

    private String name;
    private String code;
    private int quantity;
}