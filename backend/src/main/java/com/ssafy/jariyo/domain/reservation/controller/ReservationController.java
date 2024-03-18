package com.ssafy.jariyo.domain.reservation.controller;

import com.ssafy.jariyo.domain.reservation.dto.response.AvailableReservationResponseDto;
import com.ssafy.jariyo.domain.reservation.dto.request.ReservationRequestDto;
import com.ssafy.jariyo.domain.reservation.dto.response.ReservationByStoreResponseDto;
import com.ssafy.jariyo.domain.reservation.dto.response.ReservationByUserResponseDto;
import com.ssafy.jariyo.domain.reservation.entity.Reservation;
import com.ssafy.jariyo.domain.reservation.service.ReservationService;
import com.ssafy.jariyo.global.jwt.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservation")
@Tag(name = "reservation", description = "예약 API")
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;
    private final JwtService jwtService;


    @Operation(summary = "[소비자용] 내 예약 전체 검색", description = "엑세스 토큰의 사용자Id를 이용하여 예약 내역 검색")
    @ApiResponse(responseCode = "200", description = "예약 검색 성공", content = @Content(schema = @Schema(implementation = ReservationByUserResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/search")
    public ResponseEntity<Object> searchReservationsByUser(HttpServletRequest request) {
        log.info("=================유저 예약검색 컨트롤러=================");

        String atk = jwtService.extractAccessToken(request).get();
        Long userId = jwtService.extractId(atk).get();
        log.info(">> 요청 유저 id : {}",userId);

        List<ReservationByUserResponseDto> searchResults = reservationService.getReservationByUser(userId);
        log.info(">>>> 예약 현황 {} : {}", searchResults.size(), searchResults);
        return ResponseEntity.status(HttpStatus.OK).body(searchResults);
    }

    @Operation(summary = "[사업자용]가게별 전체 예약 검색", description = "엑세스 토큰의 가게I와 파라미터 가게 Id를 이용하여 예약 내역 검색")
    @ApiResponse(responseCode = "200", description = "예약 검색 성공", content = @Content(schema = @Schema(implementation = ReservationByStoreResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/search/store/{storeId}")
    public ResponseEntity<Object> searchReservationsByStore(@PathVariable("storeId") Long storeId, HttpServletRequest request) {
        log.info("=================예약검색 상점 컨트롤러=================");

        // 엑세스 토큰에서 userId 추출
        String atk = jwtService.extractAccessToken(request).get();
        Long userId = jwtService.extractId(atk).get();
        log.info(">> 요청 유저 id : {}",userId);

        // 엑세스 토큰에서 storeId 추출
        log.info(">> 요청 store id : {}", storeId);
        Optional<Long> atkStore = jwtService.extractStoreId(atk);
        if (!atkStore.isEmpty()) {
            Long atkStoreId = atkStore.get();
            log.info(">> 엑세스 토큰의 store id {}", atkStoreId);
            if (atkStoreId.equals(storeId)) {
                List<ReservationByStoreResponseDto> searchResults = reservationService.getReservationByStore(storeId);
                for(ReservationByStoreResponseDto responseDto: searchResults){
                    System.out.println(responseDto);
                }
                return ResponseEntity.status(HttpStatus.OK).body(searchResults);
            }

        }
        log.info(">>> [error] 올바른 사업자가 아닙니다");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("[Bad Request] not an owner");
    }

    @Operation(summary = "[소비자용] 날짜(2022-05-05) + 가게별 예약 검색", description = "가게Id, 선택된 날짜를 이용하여 예약 내역 검색")
    @ApiResponse(responseCode = "200", description = "예약 검색 성공", content = @Content(schema = @Schema(implementation = ReservationByStoreResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/search/store/{storeId}/date/{reqDate}")
    public ResponseEntity<Object> searchReservationsByDateAndStore(@PathVariable("storeId")Long storeId, @PathVariable("reqDate")String reqDate) {
        log.info("=================예약검색 상점 & 날짜 컨트롤러=================");
        List<ReservationByStoreResponseDto> searchResults = reservationService.getReservationByDateAndStore(reqDate, storeId);
        return ResponseEntity.status(HttpStatus.OK).body(searchResults);
    }

    @Operation(summary = "[소비자용] 날짜(2022-05-05) + 사용자별 예약 검색", description = "유저Id(엑세스토큰), 선택된 날짜를 이용하여 예약 내역 검색")
    @ApiResponse(responseCode = "200", description = "예약 검색 성공", content = @Content(schema = @Schema(implementation = ReservationByUserResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/search/date/{reqDate}")
    public ResponseEntity<Object> searchReservationsByDateAndUser(@PathVariable("reqDate")String reqDate, HttpServletRequest request) {
        log.info("=================예약검색 유저 & 날짜 컨트롤러=================");
        // 엑세스 토큰에서 userId 추출
        String atk = jwtService.extractAccessToken(request).get();
        Long userId = jwtService.extractId(atk).get();
        log.info(">> 요청 유저 id : {}",userId);

        List<ReservationByUserResponseDto> searchResults = reservationService.getReservationByDateAndUser(reqDate, userId);
        return ResponseEntity.status(HttpStatus.OK).body(searchResults);
    }



    @Operation(summary = "가게별 예약 가능한 날짜 정보 반환", description = "가게Id를 이용하여 예약이 가능한 날짜들을 반환")
    @ApiResponse(responseCode = "200", description = "예약 가능한 테이블 검색 성공", content = @Content(schema = @Schema(implementation = LocalDateTime.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/search/available/store/{storeId}")
    public ResponseEntity<Object> searchAvailableReservationsByStore(@PathVariable("storeId")Long storeId) {
        log.info("=================상점{}의 예약 가능 날짜 조회 컨트롤러=================", storeId);
        List<LocalDateTime> searchResults = reservationService.getAvailableReservationDatesByStore(storeId);
        return ResponseEntity.status(HttpStatus.OK).body(searchResults);
    }

    @Operation(summary = "날짜(2022-05-05) + 가게별 예약 가능한 테이블 반환", description = "가게Id, 선택된 날짜를 이용하여 예약이 가능한 테이블 반환(진행중&완료된 예약을 제외하고)")
    @ApiResponse(responseCode = "200", description = "예약 가능한 테이블 검색 성공", content = @Content(schema = @Schema(implementation = AvailableReservationResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/search/available/store/{storeId}/date/{reqDate}")
    public ResponseEntity<Object> searchAvailableReservationsByDateAndStore(@PathVariable("storeId")Long storeId, @PathVariable("reqDate")String reqDate) {
        log.info("=================상점{}의 날짜:{} ㅣ 가능 예약 상황 조회 컨트롤러=================", storeId, reqDate);
//        List<AvailableReservationResponseDto> searchResults = reservationService.getAvailableReservationByDateAndStore(reqDate, storeId);
        List<AvailableReservationResponseDto> searchResults = reservationService.getInAvailableReservation(reqDate, storeId);
        return ResponseEntity.status(HttpStatus.OK).body(searchResults);
    }

    @Operation(summary = "예약 등록하기", description = "예약 정보를 통해 예약하기")
    @ApiResponse(responseCode = "200", description = "예약 성공", content = @Content(schema = @Schema(implementation = ReservationRequestDto.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PostMapping("/regist")
    public ResponseEntity<Object> registReservation(@RequestBody ReservationRequestDto reservationRequestDto, HttpServletRequest request) {
        log.info("=================예약 등록 컨트롤러=================");
        // 엑세스 토큰에서 userId 추출
        String atk = jwtService.extractAccessToken(request).get();
        Long userId = jwtService.extractId(atk).get();
        log.info(">> 요청 유저 id : {}",userId);
        log.info(">> 요청 예약 : {}", reservationRequestDto);

        reservationService.registReservation(reservationRequestDto, userId);
        return ResponseEntity.status(HttpStatus.OK).body("예약 등록 성공!");
    }

    @Operation(summary = "[ZPASS 사용]예약 등록하기", description = "예약 정보와 ZPASS를 통해 예약하기")
    @ApiResponse(responseCode = "200", description = "예약 성공", content = @Content(schema = @Schema(implementation = ReservationRequestDto.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PostMapping("/regist/zpass/{zpassId}")
    public ResponseEntity<Object> registReservationWithZPass(@PathVariable("zpassId")Long zpassId, @RequestBody ReservationRequestDto reservationRequestDto, HttpServletRequest request) {
        log.info("=================[ZPASS]예약 등록 컨트롤러=================");
        // 엑세스 토큰에서 userId 추출
        String atk = jwtService.extractAccessToken(request).get();
        Long userId = jwtService.extractId(atk).get();
        log.info(">> 요청 유저 id : {}",userId);
        log.info(">> zpass {} | 요청 예약 : {}", zpassId, reservationRequestDto);
        Boolean isVaildTicket = reservationService.validateZpass(zpassId, userId);
        if (isVaildTicket) {
            reservationService.registReservationWithZPass(zpassId, userId, reservationRequestDto);
            return ResponseEntity.status(HttpStatus.OK).body("예약 등록 성공! & ZPASS 사용 성공");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 ZPASS 입니다.");
    }

    @Operation(summary = "예약 취소하기 ", description = "예약 취소 (예약 당사자 or 가게 사업자만 가능)")
    @ApiResponse(responseCode = "200", description = "예약 취소 성공", content = @Content(schema = @Schema(implementation = ReservationByUserResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @DeleteMapping("{reservationId}/cancle")
    public ResponseEntity<Object> cancleReservation(@PathVariable("reservationId")Long reservationId, HttpServletRequest request){
        log.info("=================예약 취소 컨트롤러=================");

        // 엑세스 토큰에서 userId 추출
        String atk = jwtService.extractAccessToken(request).get();
        Long userId = jwtService.extractId(atk).get();
        log.info(">> 요청 유저 id : {}",userId);

        Boolean isCheck = reservationService.validateUser(userId, reservationId);
        if (isCheck) {
            log.info(">>> 올바른 유저입니다. 예약 취소를 진행합니다.");
            Reservation cancledReservation = reservationService.reomveReservation(reservationId);
            return ResponseEntity.status(HttpStatus.OK).body("예약 취소 성공!");
        }
        log.info(">>> [error] 올바른 유저가 아닙니다");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("[Bad Request] not a reservation user");
    }

    @Operation(summary = "예약 종료하기 - 사업자용", description = "예약 종료 (사업자만 가능)")
    @ApiResponse(responseCode = "200", description = "예약 종료 성공", content = @Content(schema = @Schema(implementation = ReservationByUserResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("{reservationId}/expire")
    public ResponseEntity<Object> expireReservation(@PathVariable("reservationId")Long reservationId, HttpServletRequest request){
        log.info("=================예약 종료 컨트롤러=================");

        // 엑세스 토큰에서 userId 추출
        String atk = jwtService.extractAccessToken(request).get();
        Long userId = jwtService.extractId(atk).get();
        log.info(">> 요청 유저 id : {}",userId);

        Boolean isStoreOwner = reservationService.validateStoreOwner(userId, reservationId);
        if (isStoreOwner) {
            Reservation expiredReservation = reservationService.expireReservation(reservationId);
            return ResponseEntity.status(HttpStatus.OK).body("예약 종료 성공!");
        }


        log.info(">>> [error] 올바른 사업자가 아닙니다");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("[Bad Request] not an owner");

    }

//    @Operation(summary = "예약 확정", description = "사용자 요청 예약 확정하기")
//    @ApiResponse(responseCode = "200", description = "예약 종료 성공", content = @Content(schema = @Schema(implementation = ReservationByUserResponseDto.class)))
//    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
//    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
//    @GetMapping("{reservationId}/accept")
//    public ResponseEntity<Object> acceptReservation(@PathVariable("reservationId")Long reservationId){
//        log.info("=================예약 확정 컨트롤러=================");
//        Reservation acceptedReservation = reservationService.allowReservation(reservationId);
//        return ResponseEntity.status(HttpStatus.OK).body("예약 확정 성공!");
//    }


}
