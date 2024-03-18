package com.ssafy.jariyo.domain.user.dto;

import lombok.*;

@NoArgsConstructor
@Getter
@ToString
@Builder
@AllArgsConstructor
public class UserUpdateDto {
    private String phone;
    private String hometown;
}
