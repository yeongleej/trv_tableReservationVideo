package com.ssafy.jariyo.domain.store.controller;

import com.ssafy.jariyo.domain.store.dto.response.StoreResponseDefaultDto;
import com.ssafy.jariyo.domain.store.service.FollowService;
import com.ssafy.jariyo.domain.user.dto.UserResponseDto;
import com.ssafy.jariyo.global.jwt.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "follow", description = "상점 구독 API")
@RequestMapping("/api/follow")
public class FollowController {

    private final FollowService followService;
    private final JwtService jwtService;

    @Operation(summary = "상점 구독", description = "특정 상점을 구독합니다.")
    @ApiResponse(responseCode = "201", description = "상점 구독 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "404", description = "상점을 찾을 수 없음")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.")
    @GetMapping("/store/{storeId}")
    public ResponseEntity<Object> followStore(@PathVariable("storeId") Long storeId, HttpServletRequest request) {
        log.info("=================상점 구독 컨트롤러=================");
        String atk = jwtService.extractAccessToken(request).get();
        Long userId = jwtService.extractId(atk).get();
        followService.followStore(storeId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Store followed successfully");
    }

    @Operation(summary = "상점 구독 취소", description = "특정 상점 구독을 취소합니다.")
    @ApiResponse(responseCode = "204", description = "상점 구독 취소 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "404", description = "상점을 찾을 수 없음")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.")
    @DeleteMapping("/store/{storeId}")
    public ResponseEntity<Object> unfollowStore(@PathVariable("storeId") Long storeId, HttpServletRequest request) {
        log.info("=================상점 구독 취소 컨트롤러=================");
        String atk = jwtService.extractAccessToken(request).get();
        Long userId = jwtService.extractId(atk).get();
        followService.unfollowStore(storeId, userId);
        return ResponseEntity.status(HttpStatus.OK).body("Store unfollowed successfully");
    }

    @Operation(summary = "상점 구독자 확인", description = "특정 상점의 모든 구독자를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "상점 구독자 목록 조회 성공")
    @ApiResponse(responseCode = "404", description = "상점을 찾을 수 없음")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.")
    @GetMapping("/store/{storeId}/search")
    public ResponseEntity<Object> getStoreFollowers(@PathVariable("storeId") Long storeId) {
        log.info("=================상점 구독자 확인 컨트롤러=================");
        List<UserResponseDto> followers = followService.getStoreFollowers(storeId);
        return ResponseEntity.status(HttpStatus.OK).body(followers);
    }

    @Operation(summary = "유저 구독 상점 확인", description = "특정 유저가 구독한 모든 상점을 확인합니다.")
    @ApiResponse(responseCode = "200", description = "유저 구독 상점 목록 조회 성공")
    @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.")
    @GetMapping("/store/search")
    public ResponseEntity<Object> getUserFollowedStores(HttpServletRequest request) {
        log.info("=================구독한 상점 조회 컨트롤러=================");
        String atk = jwtService.extractAccessToken(request).get();
        Long userId = jwtService.extractId(atk).get();
        List<StoreResponseDefaultDto> followedStores = followService.getUserFollowedStores(userId);
        return ResponseEntity.status(HttpStatus.OK).body(followedStores);
    }
}
