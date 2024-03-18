package com.ssafy.jariyo.domain.alarm.controller;

import com.ssafy.jariyo.domain.alarm.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;

    // 메시지 알림
    @GetMapping("/subscribe/{userId}")
    public SseEmitter subscribe(@PathVariable("userId") Long userId) {
        SseEmitter sseEmitter = notificationService.subscribe(userId);

        return sseEmitter;
    }
    @GetMapping("/sendMessage/{userId}")
    public void sendData(@PathVariable("userId") Long userId) {
        notificationService.notify(userId, "alarm Test");
    }
}