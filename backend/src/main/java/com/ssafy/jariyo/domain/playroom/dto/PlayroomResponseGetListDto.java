package com.ssafy.jariyo.domain.playroom.dto;

import com.ssafy.jariyo.domain.board.dto.BoardResponseGetDto;
import com.ssafy.jariyo.domain.playroom.entity.Playroom;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class PlayroomResponseGetListDto {

    List<PlayroomResponseGetDto> list;
    private int totalPages;
    private long totalElements;
}


