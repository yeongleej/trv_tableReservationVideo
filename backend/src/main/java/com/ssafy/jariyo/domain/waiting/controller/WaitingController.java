package com.ssafy.jariyo.domain.waiting.controller;

import com.ssafy.jariyo.domain.waiting.Serializable.Waiting;
import com.ssafy.jariyo.domain.waiting.dto.*;
import com.ssafy.jariyo.domain.waiting.service.WaitingService;
import com.ssafy.jariyo.global.jwt.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@Tag(name = "waiting", description = "웨이팅 API")
@RequestMapping("/api")
public class WaitingController {

    private final WaitingService waitingService;
    private final JwtService jwtService;

    @Operation(summary = "웨이팅 등록", description = "새로운 웨이팅을 등록합니다.")
    @ApiResponse(responseCode = "201", description = "웨이팅이 성공적으로 등록됨")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PostMapping("/store/{storeId}/waiting")
    public ResponseEntity<Object> addWaiting(@PathVariable Long storeId,
                                             @Valid @RequestBody WaitingRequestPostDto waitingRequestPostDto,
                                             HttpServletRequest httpServletRequest) {
        String token = jwtService.extractAccessToken(httpServletRequest).get();
        Long userId = jwtService.extractId(token).get();

        waitingService.addWaiting(storeId, userId, waitingRequestPostDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Waiting added successfully");
    }

    @Operation(summary = "유저가 웨이팅 리스트 확인", description = "유저가 웨이팅 리스트를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "웨이팅 리스트 조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Waiting.class))))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/user/waiting/list")
    public ResponseEntity<Object> getWaitingListByUserId(HttpServletRequest httpServletRequest) {
        String token = jwtService.extractAccessToken(httpServletRequest).get();
        Long userId = jwtService.extractId(token).get();

        List<Waiting> waitingList = waitingService.getWaitingListByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(waitingList);
    }

    @Operation(summary = "유저의 특정 상점 웨이팅 조회", description = "유저가 특정 상점에 대한 자신의 웨이팅 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "웨이팅 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = Waiting.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "404", description = "웨이팅 정보를 찾을 수 없음")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/user/waiting/{storeId}/waiting")
    public ResponseEntity<Object> getWaitingByStoreIdAndUserId(@PathVariable Long storeId, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractAccessToken(httpServletRequest).get();
        Long userId = jwtService.extractId(token).get();

        Waiting waiting = waitingService.getWaitingByStoreIdAndUserId(storeId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(waiting);
    }


    @Operation(summary = "식당에서 웨이팅 리스트 확인", description = "식당에서 웨이팅 리스트를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "웨이팅 리스트 조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Waiting.class))))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/store/{storeId}/waiting/list")
    public ResponseEntity<Object> getWaitingListByStoreId(@PathVariable Long storeId) {
        List<Waiting> waitingList = waitingService.getWaitingListByStoreId(storeId);
        return ResponseEntity.status(HttpStatus.OK).body(waitingList);
    }

    @Operation(summary = "웨이팅 취소", description = "웨이팅을 취소합니다.")
    @ApiResponse(responseCode = "204", description = "웨이팅이 성공적으로 취소됨")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @DeleteMapping("/store/{storeId}/waiting")
    public ResponseEntity<Object> cancelWaiting(@PathVariable Long storeId, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractAccessToken(httpServletRequest).get();
        Long userId = jwtService.extractId(token).get();

        waitingService.cancelWaiting(storeId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Waiting cancelled successfully");
    }

    @Operation(summary = "웨이팅 미루기", description = "웨이팅을 미룹니다.")
    @ApiResponse(responseCode = "204", description = "웨이팅이 성공적으로 미뤄짐")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PutMapping("/store/{storeId}/waiting/{postponeCount}")
    public ResponseEntity<Object> postponeWaiting(@PathVariable Long storeId, @PathVariable int postponeCount, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractAccessToken(httpServletRequest).get();
        Long userId = jwtService.extractId(token).get();

        waitingService.postponeWaiting(storeId, userId, postponeCount);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Waiting postponed successfully");
    }

    @Operation(summary = "사장님이 웨이팅을 삭제(수락, 임의)", description = "웨이팅을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "웨이팅이 성공적으로 삭제됨")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @DeleteMapping("/store/{storeId}/waiting/{userId}")
    public ResponseEntity<Object> cancelWaiting(@PathVariable Long storeId, @PathVariable Long userId, HttpServletRequest httpServletRequest) {

        waitingService.deleteWaiting(storeId, userId, httpServletRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Waiting cancelled successfully");
    }

}
