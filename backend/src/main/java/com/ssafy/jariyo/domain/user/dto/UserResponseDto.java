package com.ssafy.jariyo.domain.user.dto;

import com.ssafy.jariyo.domain.user.entity.User;
import jakarta.annotation.security.DenyAll;
import lombok.*;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@Getter
@ToString
@Builder
@AllArgsConstructor
public class UserResponseDto {
    private Long userId;
    private String nickname;
    private String email;
    private String phone;
    private String hometown;
//    private String createdAt;

    // User 엔티티를 받아오는 생성자
    public UserResponseDto(User user) {
        this.userId = user.getUserId();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.hometown = user.getHometown();
//        this.createdAt = user.getRegDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
