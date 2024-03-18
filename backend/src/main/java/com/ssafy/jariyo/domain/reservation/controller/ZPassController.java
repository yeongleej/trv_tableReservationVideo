package com.ssafy.jariyo.domain.reservation.controller;

import com.ssafy.jariyo.domain.reservation.dto.request.PayInfoDto;
import com.ssafy.jariyo.domain.reservation.dto.request.ZPassRequestDto;
import com.ssafy.jariyo.domain.reservation.dto.response.*;
import com.ssafy.jariyo.domain.reservation.service.ZPassService;
import com.ssafy.jariyo.global.jwt.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/zpass")
@Tag(name = "zpass", description = "예약패스 API")
@Slf4j
public class ZPassController {

    private final ZPassService zPassService;
    private final JwtService jwtService;
    private String rd_uri = "";

//    @Operation(summary = "사용자별 예약패스 검색", description = "사용자Id를 이용하여 예약패스 검색")
//    @ApiResponse(responseCode = "200", description = "예약패스 검색 성공", content = @Content(schema = @Schema(implementation = ZPassResponseDto.class)))
//    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
//    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
//    @GetMapping("/search")
//    public ResponseEntity<Object> searchZPassByUser(HttpServletRequest request) {
//        log.info("=================예약패스 검색 by유저 컨트롤러=================");
//
//        // 엑세스 토큰에서 userId 추출
//        String atk = jwtService.extractAccessToken(request).get();
//        Long userId = jwtService.extractId(atk).get();
//        log.info(">> 요청 유저 id : {}",userId);
//
//        List<ZPassResponseDto> searchResults = zPassService.getZPassByUser(userId);
//        return ResponseEntity.status(HttpStatus.OK).body(searchResults);
//    }

//    @Operation(summary = "[사업자용] 가게별 구매된 예약패스 검색", description = "가게Id를 이용하여 예약패스 검색")
//    @ApiResponse(responseCode = "200", description = "예약패스 검색 성공", content = @Content(schema = @Schema(implementation = ZPassResponseDto.class)))
//    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
//    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
//    @GetMapping("/search/my-store/{storeId}")
//    public ResponseEntity<Object> searchZPassByStore(@PathVariable("storeId") Long storeId, HttpServletRequest request) {
//        log.info("=================구매된 예약패스 검색 by가게 컨트롤러=================");
//
//        String atk = jwtService.extractAccessToken(request).get();
//        // 엑세스 토큰에서 storeId 추출
//        log.info(">> 요청 store id : {}", storeId);
//        Optional<Long> atkStore = jwtService.extractStoreId(atk);
//        if (!atkStore.isEmpty()) {
//            Long atkStoreId = atkStore.get();
//            log.info(">> 엑세스 토큰의 store id {}", atkStoreId);
//            if (atkStoreId.equals(storeId)) {
//                List<ZPassResponseDto> searchResults = zPassService.getZPassByStore(storeId);
//                return ResponseEntity.status(HttpStatus.OK).body(searchResults);
//            }
//
//        }
//        log.info(">>> [error] 올바른 사업자가 아닙니다");
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("[Bad Request] not an owner");
//    }

//    @Operation(summary = "[소비자용] 사용자&가게별 예약패스 검색", description = "사용자Id&가게Id를 이용하여 예약패스 검색")
//    @ApiResponse(responseCode = "200", description = "예약패스 검색 성공", content = @Content(schema = @Schema(implementation = ZPassResponseDto.class)))
//    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
//    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
//    @GetMapping("/search")
//    public ResponseEntity<Object> searchZPassByUserAndStore(@PathVariable("storeId") Long storeId, HttpServletRequest request) {
//        log.info("=================예약패스 검색 by사용자&가게 컨트롤러=================");
//
//        // 엑세스 토큰에서 userId 추출
//        String atk = jwtService.extractAccessToken(request).get();
//        Long userId = jwtService.extractId(atk).get();
//        log.info(">> 요청 유저 id : {}",userId);
//
//        List<ZPassResponseDto> searchResults = zPassService.getZPassByUserAndStore(userId, storeId);
//        return ResponseEntity.status(HttpStatus.OK).body(searchResults);
//    }

    @Operation(summary = "[소비자용] 사용자별 유효한 예약패스 검색", description = "사용자Id를 이용하여 유효한 예약패스 검색")
    @ApiResponse(responseCode = "200", description = "예약패스 검색 성공", content = @Content(schema = @Schema(implementation = ZPassResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/search/available")
    public ResponseEntity<Object> searchAvailableZPassByUser(HttpServletRequest request) {
        log.info("=================유효한 예약패스 검색 by유저 컨트롤러=================");

        // 엑세스 토큰에서 userId 추출
        String atk = jwtService.extractAccessToken(request).get();
        Long userId = jwtService.extractId(atk).get();
        log.info(">> 요청 유저 id : {}",userId);

        ZPassResponseActiveDto searchResult = zPassService.getAvailableZPassByUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(searchResult);
    }

//    @Operation(summary = "[사업자용] 가게별 유효한 예약패스 검색", description = "가게Id를 이용하여 유효한 예약패스 검색")
//    @ApiResponse(responseCode = "200", description = "예약패스 검색 성공", content = @Content(schema = @Schema(implementation = ZPassResponseDto.class)))
//    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
//    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
//    @GetMapping("/search/available/my-store/{storeId}")
//    public ResponseEntity<Object> searchAvailableZPassByStore(@PathVariable("storeId") Long storeId, HttpServletRequest request) {
//        log.info("=================유효한 예약패스 검색 by가게 컨트롤러=================");
//
//        String atk = jwtService.extractAccessToken(request).get();
//        // 엑세스 토큰에서 storeId 추출
//        log.info(">> 요청 store id : {}", storeId);
//        Optional<Long> atkStore = jwtService.extractStoreId(atk);
//        if (!atkStore.isEmpty()) {
//            Long atkStoreId = atkStore.get();
//            log.info(">> 엑세스 토큰의 store id {}", atkStoreId);
//            if (atkStoreId.equals(storeId)) {
//                List<ZPassResponseActiveDto> searchResults = zPassService.getAvailableZPassByStore(storeId);
//                return ResponseEntity.status(HttpStatus.OK).body(searchResults);
//            }
//
//        }
//        log.info(">>> [error] 올바른 사업자가 아닙니다");
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("[Bad Request] not an owner");
//    }

    @Operation(summary = "예약패스 구매 요청", description = "예약패스 구매")
    @ApiResponse(responseCode = "200", description = "예약패스 구매 성공", content = @Content(schema = @Schema(implementation = PayInfoDto.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PostMapping("/payment/ready")
    public ResponseEntity<Object> paymentGetRedirectUrl(@RequestBody PayInfoDto payInfoDto, HttpServletResponse response, HttpServletRequest request) {
        log.info("=================예약패스 구매 컨트롤러=================");
//        // 엑세스 토큰에서 userId 추출
        String atk = jwtService.extractAccessToken(request).get();
        Long userId = jwtService.extractId(atk).get();

        log.info(">> 요청 유저 id : {}",userId);
        try {
            rd_uri = payInfoDto.getRedirectUri();
            log.info("자리요패스 구매 요청 프론트 param uri >> " + rd_uri);
            PayReadyResponseDto payDto = zPassService.getRedirectUrl(userId, payInfoDto);
            log.info("자리요패스 구매 redirect uri >> " + payDto.getNext_redirect_pc_url());
//            response.sendRedirect(payDto.getNext_redirect_pc_url());
            return ResponseEntity.status(HttpStatus.OK).body(payDto);
        } catch (Exception e) {
            log.info("error : {}",e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("결제 요청 실패");

    }
    /**
     * 결제 성공 pid 를  받기 위해 request를 받고 pgToken은 rediret url에 뒤에 붙어오는걸 떼서 쓰기 위함
     */
    @GetMapping("/payment/success/{userId}")
    public ResponseEntity<?> afterGetRedirectUrl(@PathVariable("userId")Long userId,
                                                 @RequestParam("pg_token") String pgToken, HttpServletResponse response) {
        try {
            PayApproveResponseDto kakaoApprove = zPassService.getApprove(pgToken, userId);
            zPassService.registZPass(userId, kakaoApprove);
            log.info("결제 성공 : {}", kakaoApprove);
            log.info("rd_uri >> " +rd_uri);

            response.sendRedirect(rd_uri);

            return ResponseEntity.status(HttpStatus.OK)
                    .body("결제 성공 !!");
//            response.sendRedirect("http://localhost:3000/myPage/:"+id);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("결제 요청 실패");
        }
    }

    /**
     * 결제 진행 중 취소
     */
    @GetMapping("/payment/cancel")
    public ResponseEntity<?> cancel(HttpServletResponse response) throws IOException {
        log.info("결제 취소");
        log.info("rd_uri >> " +rd_uri);

        response.sendRedirect(rd_uri);
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                .body("사용자가 결제를 취소하였습니다.");
    }

    /**
     * 결제 실패
     */
    @GetMapping("/payment/fail")
    public ResponseEntity<?> fail() {

        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                .body("결제가 실패하였습니다.");

    }

//    @Operation(summary = "예약패스 등록", description = "예약패스 등록하기")
//    @ApiResponse(responseCode = "200", description = "예약패스 등록 성공", content = @Content(schema = @Schema(implementation = ZPassRequestDto.class)))
//    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
//    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
//    @PostMapping("/regist")
//    public ResponseEntity<Object> registZPass(@RequestBody ZPassRequestDto zPassRequestDto) {
//        log.info("=================예약패스 등록 컨트롤러=================");
//        zPassService.registZPass(zPassRequestDto);
//        return ResponseEntity.status(HttpStatus.OK).body("예약패스 등록 성공");
//    }

    @Operation(summary = "예약패스 사용", description = "zpassId를 이용하여 예약패스 사용하기")
    @ApiResponse(responseCode = "200", description = "예약패스 사용 성공", content = @Content(schema = @Schema(implementation = ZPassResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/use/{zpassId}")
    public ResponseEntity<Object> useZPassBy(@PathVariable("zpassId") Long zpassId) {
        log.info("=================예약패스 사용 컨트롤러=================");
        zPassService.useZPass(zpassId);
        return ResponseEntity.status(HttpStatus.OK).body("예약패스 사용 성공");
    }

    @Operation(summary = "예약패스 만료하기", description = "예약패스 만료하기")
    @ApiResponse(responseCode = "200", description = "예약패스 만료 성공", content = @Content(schema = @Schema(implementation = ZPassResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/{zpassId}/expire")
    public ResponseEntity<Object> expireZPass(@PathVariable("zpassId") Long zpassId) {
        log.info("=================예약패스 만료 컨트롤러=================");
        zPassService.expireZPass(zpassId);
        return ResponseEntity.status(HttpStatus.OK).body("예약 만료 성공");
    }
}
