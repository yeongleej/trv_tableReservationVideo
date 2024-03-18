package com.ssafy.jariyo.domain.alarm.entity;

import com.ssafy.jariyo.domain.user.entity.User;
import com.ssafy.jariyo.global.entity.BaseObject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor
public class Alarm extends BaseObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id", nullable = false)
    private Long alarmId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "alarm_is_checked")
    private Long alarmIsChecked;

    @Column(name = "alarm_created_at")
    private Timestamp alarmCreatedAt;

    @Column(name = "alarm_content", length = 255)
    private String alarmContent;

    // Constructors, Getters, Setters...
}
