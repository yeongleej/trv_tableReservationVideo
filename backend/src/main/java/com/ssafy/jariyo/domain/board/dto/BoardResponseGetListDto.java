package com.ssafy.jariyo.domain.board.dto;

import com.ssafy.jariyo.domain.board.entity.BoardDomain;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BoardResponseGetListDto {
    List<BoardResponseGetDto> list;
    private int totalPages;
    private long totalElements;
}