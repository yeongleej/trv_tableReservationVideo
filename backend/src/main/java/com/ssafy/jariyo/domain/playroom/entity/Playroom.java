package com.ssafy.jariyo.domain.playroom.entity;

import com.ssafy.jariyo.domain.store.entity.Store;
import com.ssafy.jariyo.global.entity.BaseObject;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Playroom extends BaseObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playroom_id", nullable = false)
    private Long playroomId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", referencedColumnName = "store_id")
    private Store store;

    @Setter
    @Column(name = "playroom_title", length = 100)
    private String title;

    @Setter
    @Column(name = "playroom_image", length = 255)
    private String image;

    @Setter
    @Column(name = "playroom_info", columnDefinition = "TEXT")
    private String info;

    @Setter
    @Column(name = "playroom_user_count")
    private Integer userCount;

    @Setter
    private String category;

    @Setter
    private Boolean chatting;

    @Setter
    private Boolean calling;

    @Setter
    private Boolean broadcasting;

    @Setter
    private Boolean waiting;

    public Playroom(Store store, String title, String image, String info, Integer userCount, String category, Boolean chatting, Boolean calling, Boolean broadcasting, Boolean waiting) {
        this.store = store;
        this.title = title;
        this.image = image;
        this.info = info;
        this.userCount = userCount;
        this.category = category;
        this.chatting = chatting;
        this.calling = calling;
        this.broadcasting = broadcasting;
        this.waiting = waiting;
    }
}