package com.ssafy.jariyo.domain.user.entity;

import com.ssafy.jariyo.global.entity.BaseObject;
import com.ssafy.jariyo.global.entity.Status;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User extends BaseObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "social_id", nullable = false)
    private String socialId;

    @Setter
    @Column(nullable = true)
    private String name;

    @Setter
    @Column(nullable = false)
    private String nickname;

    @Setter
    @Column(nullable = false, unique = true)
    private String email;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Setter
    private String phone; // 선택적 필드, 생성자에 포함하지 않음

    @Setter
    @Column(nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialType socialType;

    @Setter
    @Column(name = "hometown", nullable = true)
    private String hometown;

    @Setter
    @Column(name = "tid", nullable = true)
    private String tid;




    public User(String socialId, String nickname, String email, Role role, String imageUrl, Status status, SocialType socialType) {
        this.socialId = socialId;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
        this.imageUrl = imageUrl;
        this.status = status;
        this.socialType = socialType;
    }

}