package com.ssafy.jariyo.domain.board.entity;

import com.ssafy.jariyo.domain.s3image.entity.BoardS3Image;
import com.ssafy.jariyo.domain.store.entity.Store;
import com.ssafy.jariyo.domain.user.entity.User;
import com.ssafy.jariyo.global.entity.BaseObject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Board extends BaseObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "board_domain")
    private BoardDomain boardDomain;

    @Setter
    @Column(name = "board_title", length = 100)
    private String boardTitle;

    @Lob
    @Setter
    @Column(name = "board_content")
    private String boardContent;

    public Board(User user, Store store, BoardDomain boardDomain, String boardTitle, String boardContent) {
        this.user = user;
        this.store = store;
        this.boardDomain = boardDomain;
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
    }

    //Getters, Setters...

}
