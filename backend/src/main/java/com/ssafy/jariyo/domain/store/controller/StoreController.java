package com.ssafy.jariyo.domain.store.controller;

import com.ssafy.jariyo.domain.reservation.dto.request.DiningTableRequestDto;
import com.ssafy.jariyo.domain.reservation.dto.response.DiningTableResponseDto;
import com.ssafy.jariyo.domain.reservation.dto.response.ReservationByStoreResponseDto;
import com.ssafy.jariyo.domain.store.dto.request.StoreRequestInfoDto;
import com.ssafy.jariyo.domain.store.dto.request.StoreRequestRegistDto;
import com.ssafy.jariyo.domain.store.dto.request.StoreRequestReservationInfoDto;
import com.ssafy.jariyo.domain.store.dto.response.StoreResponseAvailableReservationDto;
import com.ssafy.jariyo.domain.store.dto.response.StoreResponseDefaultDto;
import com.ssafy.jariyo.domain.store.dto.response.StoreResponseGetDto;
import com.ssafy.jariyo.domain.store.service.StoreService;
import com.ssafy.jariyo.domain.user.dto.UserResponseDto;
import com.ssafy.jariyo.domain.user.entity.User;
import com.ssafy.jariyo.domain.user.service.UserService;
import com.ssafy.jariyo.global.jwt.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Tag(name = "store", description = "상점 API")
@RequestMapping("/api/store")
@Slf4j
public class StoreController {

    private final StoreService storeService;
    private final JwtService jwtService;

    @Operation(summary = "상점 추가", description = "새로운 상점의 정보를 추가합니다.")
    @ApiResponse(responseCode = "201", description = "상점이 성공적으로 추가됨")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PostMapping("/regist")
    public ResponseEntity<Object> addStore(
            @RequestParam("storeBusinessFile") MultipartFile storeBusinessFile,
            @RequestParam("images") MultipartFile[] images,
            @RequestParam("menu") MultipartFile menu,
            @ModelAttribute StoreRequestRegistDto storeRegistDto,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        log.info("=================상점 추가 컨트롤러=================");
        String atk = jwtService.extractAccessToken(request).get();
        Long userId = jwtService.extractId(atk).get();

        if (storeService.checkBusinessNumber(storeRegistDto.getStoreBusinessNumber())) {
            List<String> tokens =  storeService.addStore(storeRegistDto, userId, atk, storeBusinessFile, images, menu);

            if(!tokens.isEmpty()) {
                String reATK = tokens.get(0);
                String reRTK = tokens.get(1);
                log.info("새로 발급된 엑세스 토큰 : {}",reATK);
                log.info("새로 발급된 리프레시 토큰 : {}",reRTK);
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set("Authorization", reATK);
                responseHeaders.set("Authorization-refresh", reRTK);
                return ResponseEntity.ok()
                .headers(responseHeaders)
                    .body("[SUCCESS ADD STORE]Response with header using ResponseEntity");

            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Store added successfully");

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증되지 않은 사업자 등록번호 입니다.");
        }
    }

    @Operation(summary = "상점 사업자 수정", description = "상점의 사업자 정보를 수정합니다.")
    @ApiResponse(responseCode = "201", description = "상점이 성공적으로 추가됨")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PutMapping("/{storeId}/update")
    public ResponseEntity<Object> addStore(
            @PathVariable("storeId")Long storeId,
            @RequestParam("storeBusinessFile") MultipartFile storeBusinessFile,
            @RequestParam("images") MultipartFile[] images,
            @RequestParam("menu") MultipartFile menu,
            @ModelAttribute StoreRequestRegistDto modDto,
            HttpServletRequest request,
            HttpServletResponse response) {

        log.info("=================상점 수정 컨트롤러=================");
        String atk = jwtService.extractAccessToken(request).get();
        Long userId = jwtService.extractId(atk).get();
        log.info(">> 요청 유저 id : {}",userId);

        // 엑세스 토큰에서 storeId 추출
        log.info(">> 요청 store id : {}", storeId);
        Optional<Long> atkStore = jwtService.extractStoreId(atk);
        if (!atkStore.isEmpty()) {
            Long atkStoreId = atkStore.get();
            log.info(">> 엑세스 토큰의 store id {}", atkStoreId);
            if (atkStoreId.equals(storeId) && storeService.checkBusinessNumber(modDto.getStoreBusinessNumber())) {
                storeService.updateStore(modDto, storeId, userId, storeBusinessFile, images, menu);
                return ResponseEntity.status(HttpStatus.OK).body("Store business info update successfully!");
            }
        }
        log.info(">>> [error] 올바른 사업자가 아닙니다");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("[Bad Request] not an owner");
    }

    @Operation(summary = "상점 조회", description = "특정 상점의 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "상점 조회에 성공", content = @Content(schema = @Schema(implementation = StoreResponseDefaultDto.class)))
    @ApiResponse(responseCode = "404", description = "제공된 storeId로 상점을 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/{storeId}")
    public ResponseEntity<Object> getStore(@PathVariable Long storeId) {
        log.info("=================상점 Id 조회 컨트롤러=================");
        StoreResponseDefaultDto storeResponseDto = storeService.getStoreById(storeId);
        return ResponseEntity.status(HttpStatus.OK).body(storeResponseDto);
    }

    @Operation(summary = "내 상점 조회", description = "유저 id로 내 상점의 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "상점 조회에 성공", content = @Content(schema = @Schema(implementation = StoreResponseDefaultDto.class)))
    @ApiResponse(responseCode = "404", description = "제공된 storeId로 상점을 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/owner")
    public ResponseEntity<Object> getStoreByUser(HttpServletRequest request) {
        log.info("=================[사업자용]내 상점 조회 컨트롤러=================");
        String atk = jwtService.extractAccessToken(request).get();
        Long userId = jwtService.extractId(atk).get();

        StoreResponseDefaultDto storeResponseDto = storeService.getMyStoreByOwner(userId);
        log.info(">>>> 내 상점 : {}", storeResponseDto);
        return ResponseEntity.status(HttpStatus.OK).body(storeResponseDto);
    }

    @Operation(summary = "주소로 상점 조회", description = "주소를 이용하여 상점을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "상점 조회 성공",
            content = @Content(schema = @Schema(implementation = StoreResponseGetDto.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/search/address")
    public ResponseEntity<Object> searchStoresByAddress(
            @RequestParam(defaultValue = "") String address) {
        log.info("=================주소로 상점 검색 컨트롤러=================");
        log.info(address);
        StoreResponseGetDto searchResults = storeService.searchStoresByAddress(address);
        return ResponseEntity.status(HttpStatus.OK).body(searchResults);
    }

    @Operation(summary = "상점 필터링", description = "위치, 카테고리, 정렬 기준, 페이지, 페이지 크기를 이용하여 상점을 필터링합니다.")
    @ApiResponse(responseCode = "200", description = "상점 필터링 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = StoreResponseGetDto.class))))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/filter")
    public ResponseEntity<Object> filteringStores(
            @RequestParam(defaultValue = "") String location,
            @PageableDefault(sort = "storeFollowCount", direction = Sort.Direction.DESC) Pageable pageable) {
        List<StoreResponseGetDto> searchResults = storeService.filterStores(location, pageable);
        return ResponseEntity.ok(searchResults);
    }

    @Operation(summary = "상점 검색", description = "키워를 이용하여 상점을 검색합니다.")
    @ApiResponse(responseCode = "200", description = "상점 검색 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = StoreResponseGetDto.class))))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/search")
    public ResponseEntity<Object> searchStores(@RequestParam(defaultValue = "") String keyword) {

        String[] keywordsArray = keyword.split(" ");
        List<String> keywordsList = new ArrayList<>(Arrays.asList(keywordsArray));

        List<StoreResponseGetDto> searchResults = storeService.searchStoresWithMultipleKeywords(keywordsList);
        return ResponseEntity.status(HttpStatus.OK).body(searchResults);
    }

    @Operation(summary = "상점 상세 정보 업데이트", description = "기존 상점의 상세 정보(운영시간, 영업일 등등)를 업데이트합니다.")
    @ApiResponse(responseCode = "204", description = "상점 정보가 성공적으로 업데이트됨")
    @ApiResponse(responseCode = "404", description = "제공된 storeId로 상점을 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PutMapping("/{storeId}/info")
    public ResponseEntity<Object> updateStore(@PathVariable Long storeId,
                                              @Valid @RequestBody StoreRequestInfoDto storeRequestInfoDto) {
        log.info("=================상점 상세 정보 업데이트 컨트롤러=================");
        storeService.updateStoreDetailInfo(storeId, storeRequestInfoDto);
        return ResponseEntity.status(HttpStatus.OK).body("Store updated successfully");
    }

    @Operation(summary = "상점 예약 상태 업데이트", description = "기존 상점의 예약 상태를(예약일, 예약시간) 업데이트합니다.")
    @ApiResponse(responseCode = "204", description = "상점 정보가 성공적으로 업데이트됨")
    @ApiResponse(responseCode = "404", description = "제공된 storeId로 상점을 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PutMapping("/{storeId}/reservation/info")
    public ResponseEntity<Object> updateStoreReservation(@PathVariable Long storeId,
                                                         @RequestBody StoreRequestReservationInfoDto requestReservationInfoDto) {
        log.info("=================상점 예약상태 정보 업데이트 컨트롤러=================");
        storeService.updateStoreReservationInfo(storeId, requestReservationInfoDto);
        return ResponseEntity.status(HttpStatus.OK).body("Store updated successfully");
    }

    @Operation(summary = "상점 예약 상태 조회", description = "기존 상점의 예약 상태를(예약일, 예약시간) 조회.")
    @ApiResponse(responseCode = "204", description = "상점 정보가 성공적으로 조회됨")
    @ApiResponse(responseCode = "404", description = "제공된 storeId로 상점을 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/{storeId}/reservation/info")
    public ResponseEntity<Object> searchStoreReservation(@PathVariable Long storeId) {
        log.info("=================상점 예약상태 정보 조회 컨트롤러=================");
        StoreResponseAvailableReservationDto searchResults = storeService.searchStoreReservationInfo(storeId);
        log.info(">>>> 조회 결과 : {}", searchResults);
        return ResponseEntity.status(HttpStatus.OK).body(searchResults);
    }

    @Operation(summary = "상점 테이블 등록", description = "상점의 테이블 목록 등록")
    @ApiResponse(responseCode = "204", description = "상점 테이블 정보가 성공적으로 업데이트됨")
    @ApiResponse(responseCode = "404", description = "제공된 storeId로 상점을 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PostMapping("/{storeId}/dining-tables")
    public ResponseEntity<Object> addStoreDiningTables(@PathVariable Long storeId,
                                                       @RequestBody List<DiningTableRequestDto> diningTableRequestDtoList) {
        log.info("=================상점 테이블 리스트 등록 컨트롤러=================");
        log.info(">> 입력 상점 테이블들 : {}", diningTableRequestDtoList);
        log.info(">> getClass : {}", diningTableRequestDtoList.getClass());
        storeService.addStoreDiningTables(storeId, diningTableRequestDtoList);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Store Tables added successfully");
    }

    @Operation(summary = "상점 테이블 조회", description = "상점의 테이블 목록 조회")
    @ApiResponse(responseCode = "204", description = "상점 테이블 정보가 성공적으로 업데이트됨", content = @Content(schema = @Schema(implementation = DiningTableResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "제공된 storeId로 상점을 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/{storeId}/dining-tables")
    public ResponseEntity<Object> searchStoreDiningTable(@PathVariable Long storeId) {
        log.info("=================상점 {} 테이블 리스트 조회 컨트롤러=================", storeId);
        List<DiningTableResponseDto> resultTables = storeService.searchStoreDiningTables(storeId);
        log.info(">>> 테이블 리스트 : {}", resultTables);
        return ResponseEntity.status(HttpStatus.OK).body(resultTables);
    }

    @Operation(summary = "상점 테이블 수정", description = "상점의 테이블 수정 등록")
    @ApiResponse(responseCode = "204", description = "상점 테이블 정보가 성공적으로 업데이트됨")
    @ApiResponse(responseCode = "404", description = "제공된 storeId로 상점을 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PutMapping("/{storeId}/dining-tables")
    public ResponseEntity<Object> updateStoreDiningTables(@PathVariable("storeId") Long storeId,
                                                          @RequestBody List<DiningTableRequestDto> diningTableRequestDtoList, HttpServletRequest request) {
        log.info("================= 상점 {} 테이블 수정 컨트롤러 =================", storeId);

        // 엑세스 토큰에서 userId 추출
        String atk = jwtService.extractAccessToken(request).get();
        Long userId = jwtService.extractId(atk).get();
        log.info(">> 요청 유저 id : {}", userId);

        // 엑세스 토큰에서 storeId 추출
        log.info(">> 요청 store id : {}", storeId);
        Optional<Long> atkStore = jwtService.extractStoreId(atk);
        if (!atkStore.isEmpty()) {
            Long atkStoreId = atkStore.get();
            log.info(">> 엑세스 토큰의 store id {}", atkStoreId);
            if (atkStoreId.equals(storeId)) {
                storeService.updateStoreDiningTable(storeId, diningTableRequestDtoList);
                return ResponseEntity.status(HttpStatus.OK).body("Store Tables updated successfully");
            }

        }
        log.info(">>> [error] 올바른 사업자가 아닙니다");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("[Bad Request] not an owner");

    }

    @Operation(summary = "상점 삭제", description = "특정 상점을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "상점이 성공적으로 삭제됨")
    @ApiResponse(responseCode = "404", description = "제공된 storeId로 상점을 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<Object> deleteStore(@PathVariable Long storeId) {
        log.info("=================상점 테이블 삭제 컨트롤러=================");
        storeService.deleteStore(storeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Store deleted successfully");
    }

    @GetMapping("/openapi/{businessNum}")
    public void openApiTest(@PathVariable("businessNum") String businessNum) {
        log.info("=================open api 활용 테스트 =================");
        storeService.checkBusinessNumber(businessNum);
    }

}
