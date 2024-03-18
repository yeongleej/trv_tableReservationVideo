package com.ssafy.jariyo.domain.board.dto;
import com.ssafy.jariyo.domain.board.entity.BoardDomain;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BoardResponseGetDto {
    private Long boardId;
    private Long userId;
    private String userName;
    private Long storeId;
    private List<String> images;
    private String title;
    private String content;
    private BoardDomain domain;
    private LocalDateTime regDate;
}