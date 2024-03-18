package com.ssafy.jariyo.domain.playroom.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlayroomRequestUpdateDto {

    private String title;
    private String image;
    private String info;
    private Integer userCount;
    private String category;

    private Boolean chatting;
    private Boolean calling;
    private Boolean broadcasting;
    private Boolean waiting;
}
