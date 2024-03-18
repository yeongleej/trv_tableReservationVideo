package com.ssafy.jariyo.domain.user.controller;

import com.ssafy.jariyo.domain.user.service.UserService;
import com.ssafy.jariyo.global.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;
    private final JwtService jwtService;
    private String rd_uri = "";

    @GetMapping("/api/login-success/{atk}/{rtk}")
    public void success(@PathVariable("atk") String atk, @PathVariable("rtk") String rtk, HttpServletResponse response) throws IOException {
        log.info("rd_uri >> " +rd_uri);
        log.info("atk >> " + atk);
        log.info("rtk >> " + rtk);

        response.sendRedirect(rd_uri+atk+"/"+rtk);
    }

    /**
     * 소셜로그인 - Kakao
     * */
    @GetMapping("/api/user/login/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestParam("redirect_uri") String redirect_uri, HttpServletRequest request, HttpServletResponse response) throws IOException {
        rd_uri = redirect_uri + "#/login-success/";
        log.info("카카오 소셜로그인 요청 프론트 param uri >> " + rd_uri);
        response.sendRedirect("/oauth2/authorization/kakao");
        return new ResponseEntity<>("end login request", HttpStatus.OK);
    }

    /**
     * 소셜로그인 - Google
     * */
    @GetMapping("/api/user/login/google")
    public ResponseEntity<?> googleLogin(@RequestParam("redirect_uri") String redirect_uri, HttpServletResponse response) throws IOException {
        rd_uri = redirect_uri + "#/login-success/";
        log.info("구글 소셜로그인 요청 프론트 param uri >> " + rd_uri);
        response.sendRedirect("/oauth2/authorization/google");

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    /**
     * 소셜로그인 - Naver
     * */
    @GetMapping("/api/user/login/naver")
    public ResponseEntity<?> naverLogin(@RequestParam("redirect_uri") String redirect_uri, HttpServletResponse response) throws IOException {
        rd_uri = redirect_uri + "#/login-success/";
        log.info("네이버 소셜로그인 요청 프론트 param uri >> " + rd_uri);
        response.sendRedirect("/oauth2/authorization/naver");

        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
