package com.ssafy.jariyo.global.oauth2.handler;


import com.ssafy.jariyo.domain.user.entity.User;
import com.ssafy.jariyo.domain.user.repository.UserRepository;
import com.ssafy.jariyo.global.jwt.service.JwtService;
import com.ssafy.jariyo.global.oauth2.OAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.access.expiration}")
    private String accessTokenExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            User user = userRepository.findByEmail(oAuth2User.getEmail()).get();
            String accessToken = jwtService.createAccessToken(user, oAuth2User.getEmail());
            response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
//            response.setHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
            String refreshToken = jwtService.createRefreshToken(); // JwtService의 createRefreshToken을 사용하여 RefreshToken 발급
            response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);
//            response.setHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);


//            Cookie[] cList = request.getCookies();
//            log.info("cc >> "+cList);
//            for(Cookie cookie:cList) {
//                log.info("cookie name : "+cookie.getName());
//                if(cookie.getName().equals("redirect_uri")) {
//                    log.info("redirect_uri >>>> "+ cookie.getValue());
//                }
//            }

            response.sendRedirect("/api/login-success/"
                                    +accessToken
                                    +"/"
                                    + refreshToken);
//            response.setStatus(302);

            jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken); // 응답 헤더에 AccessToken, RefreshToken 실어서 응답



            // ifPresent() : 객체가 값을 가지고 있으면 실행
//               // v1 RDB에 저장 : 로그인 성공 시, refreshToken을 발급하고 DB에 저장
//               userRepository.findByEmail(oAuth2User.getEmail())
//                       .ifPresent(user -> {
//                           user.updateRefreshToken(refreshToken);
//                           userRepository.saveAndFlush(user);
//                       });
            // v2 Redis에 저장 : Redis Cache에 저장
            Long refreshExpiration = jwtService.extractExpiration(refreshToken).get().getTime();
            jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken, refreshExpiration);
            log.info("로그인에 성공하였습니다. 이메일 : {}", oAuth2User.getEmail());
            log.info("로그인에 성공하였습니다. AccessToken : {}", accessToken);
            log.info("로그인에 성공하였습니다. RefreshToken : {}", refreshToken);
            log.info("발급된 AccessToken 만료 기간 : {}", accessTokenExpiration);

//               jwtService.sendAccessAndRefreshToken(response, accessToken, null);
//               User findUser = userRepository.findByEmail(oAuth2User.getEmail())
//                                .orElseThrow(() -> new IllegalArgumentException("이메일에 해당하는 유저가 없습니다."));
//               findUser.authorizeUser();

        } catch (Exception e) {
                throw e;
        }

    }

//    // TODO : 소셜 로그인 시에도 무조건 토큰 생성하지 말고 JWT 인증 필터처럼 RefreshToken 유/무에 따라 다르게 처리해보기
//    private void loginSuccess(HttpServletResponse response, OAuth2User oAuth2User) throws IOException {
//        String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
//        String refreshToken = jwtService.createRefreshToken();
//        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
//        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);
//
//        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
//        jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
//    }
}
