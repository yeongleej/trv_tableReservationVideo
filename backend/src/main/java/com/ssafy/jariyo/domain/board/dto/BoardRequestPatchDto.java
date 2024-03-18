package com.ssafy.jariyo.domain.board.dto;

import com.ssafy.jariyo.domain.board.entity.BoardDomain;
import lombok.Data;

@Data
public class BoardRequestPatchDto {
    private String domain;
    private String title;
    private String content;
}
