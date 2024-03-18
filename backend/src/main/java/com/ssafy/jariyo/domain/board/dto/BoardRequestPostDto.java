package com.ssafy.jariyo.domain.board.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BoardRequestPostDto {
    private String domain;
    private Boolean useStoreId;
    private String title;
    private String content;
    private MultipartFile[] upfile;
}
