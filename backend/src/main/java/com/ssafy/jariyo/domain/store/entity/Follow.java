package com.ssafy.jariyo.domain.store.entity;

import com.ssafy.jariyo.domain.store.entity.Store;
import com.ssafy.jariyo.domain.user.entity.User;
import com.ssafy.jariyo.global.entity.BaseObject;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "follow")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follow extends BaseObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id", nullable = false)
    private Long followId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
