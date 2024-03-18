package com.ssafy.jariyo.domain.user.controller;

import com.ssafy.jariyo.domain.user.service.UserService;
import com.ssafy.jariyo.domain.user.dto.UserResponseDto;
import com.ssafy.jariyo.domain.user.dto.UserUpdateDto;
import com.ssafy.jariyo.global.jwt.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;

@Slf4j
@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    /**
     * 로그아웃
     * */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        log.info("로그아웃 컨트롤러 진입 ===================== ");

        log.info("isError ? "+ request.getAttribute("exception"));
        HttpStatus status = (HttpStatus) request.getAttribute("exception");
        if (status == null) {
            status = HttpStatus.OK;
        }
        if (!status.is2xxSuccessful()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        String accessToken = jwtService.extractAccessToken(request).orElseThrow(() -> new NullPointerException(""));
        System.out.println("헤더의 ATK : "+ accessToken);

        userService.logout(accessToken);


        return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
    }

    /**
     * 로그인 연장
     * */
    @GetMapping("/reissue")
    public ResponseEntity refreshLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        log.info("로그인 연장 컨틀롤러 진입================== ");

        log.info("isError ? "+ request.getAttribute("exception"));
        HttpStatus status = (HttpStatus) request.getAttribute("exception");
        String atk = response.getHeader("Authorization");
        String rtk = response.getHeader("Authorization-refresh");
        log.info("새로 발급된 엑세스 토큰 : "+atk);
        log.info("새로 발급된 리프레시 토큰 : "+rtk);

        if (status == null) {
            status = HttpStatus.OK;
        }
        if (!status.is2xxSuccessful()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Authorization",
                response.getHeader("Authorization"));
        responseHeaders.set("Authorization-refresh", response.getHeader("Authorization-refresh"));

        return ResponseEntity.ok()
//                .headers(responseHeaders)
                .body("Response with header using ResponseEntity");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        try {
            UserResponseDto userResponseDto = userService.getUserById(userId);

            if (userResponseDto == null) {
                return ResponseEntity.notFound().build(); // 404 Not Found
            }

            return ResponseEntity.ok(userResponseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid user ID"); // 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error"); // 500 Internal Server Error
        }
    }

    @GetMapping("/{userId}/store")
    public ResponseEntity<Object> getUserStore(@PathVariable Long userId) {
        try {
            Long storeId = userService.getStoreIdByUserId(userId);

            if (storeId == null) {
                return ResponseEntity.notFound().build(); // 404 Not Found
            }

            return ResponseEntity.ok(storeId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid user ID"); // 400 Bad Request
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @RequestBody UserUpdateDto userUpdateDto) {
        try {
            log.info("=========== 유저정보 수정 컨트롤러 진입 =============");
            log.info(">> 변경 phone : {}", userUpdateDto.getPhone());
            log.info(">> 변경 hometown : {}", userUpdateDto.getHometown());
            userService.updateUser(userId, userUpdateDto);
            return ResponseEntity.ok().body("User updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid request data"); // 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error"); // 500 Internal Server Error
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok().body("User deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid user ID"); // 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error"); // 500 Internal Server Error
        }
    }

}
