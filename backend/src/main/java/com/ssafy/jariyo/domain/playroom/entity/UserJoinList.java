package com.ssafy.jariyo.domain.playroom.entity;

import com.ssafy.jariyo.domain.user.entity.User;
import com.ssafy.jariyo.global.entity.BaseObject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_join_list")
public class UserJoinList extends BaseObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_join_list_id", nullable = false)
    private Long userJoinListId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playroom_id", referencedColumnName = "playroom_id")
    private Playroom playroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Constructors, Getters, Setters...

}
