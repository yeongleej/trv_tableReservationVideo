package com.ssafy.jariyo.domain.s3image.entity;

import com.ssafy.jariyo.domain.board.entity.Board;
import com.ssafy.jariyo.global.entity.BaseObject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class BoardS3Image extends BaseObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private S3Image s3Image;

    public BoardS3Image(Board board, S3Image s3Image) {
        this.board = board;
        this.s3Image = s3Image;
    }
}
