package com.ssafy.jariyo.domain.store.dto.request;

import lombok.*;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class OpenApiRequestDto {
    private List<String> b_no;
}
