package com.ssafy.jariyo.domain.review.controller;

import com.ssafy.jariyo.domain.review.dto.ReviewRequestPatchDto;
import com.ssafy.jariyo.domain.review.dto.ReviewRequestPostDto;
import com.ssafy.jariyo.domain.review.dto.ReviewResponseGetDto;
import com.ssafy.jariyo.domain.review.dto.ReviewResponseGetListDto;
import com.ssafy.jariyo.domain.review.service.ReviewService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Tag(name = "review", description = "리뷰 API")
@RequestMapping("/api/store/{storeId}/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtService jwtService;

    @Operation(summary = "식당 리뷰 작성", description = "식당 리뷰에 새로운 글을 추가합니다.")
    @ApiResponse(responseCode = "201", description = "리뷰이 성공적으로 작성됨")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PostMapping
    public ResponseEntity<Object> addReview(@PathVariable Long storeId,
                                            HttpServletRequest httpRequest,
                                            @ModelAttribute ReviewRequestPostDto reviewRequestPostDto) throws IOException {
        String token = jwtService.extractAccessToken(httpRequest).get();
        Long userId = jwtService.extractId(token).get();

        reviewService.addReview(userId, storeId, reviewRequestPostDto, reviewRequestPostDto.getUpfile());
        return ResponseEntity.status(HttpStatus.CREATED).body("Review added successfully");
    }

    @Operation(summary = "식당 리뷰 조회", description = "리뷰을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 조회에 성공", content = @Content(schema = @Schema(implementation = ReviewResponseGetDto.class)))
    @ApiResponse(responseCode = "404", description = "제공된 reviewId로 리뷰을 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/{reviewId}")
    public ResponseEntity<Object> getReview(@PathVariable Long reviewId) {
        ReviewResponseGetDto reviewResponseDto = reviewService.getReview(reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(reviewResponseDto);
    }

    @Operation(summary = "식당 리뷰 검색", description = "키워드, 위치, 카테고리, 정렬 기준, 페이지, 페이지 크기를 이용하여 상점을 검색합니다.")
    @ApiResponse(responseCode = "200", description = "상점 검색 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReviewResponseGetDto.class))))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/search")
    public ResponseEntity<Object> searchReview(
            @RequestParam(defaultValue = "") String keyword,
            @PageableDefault(page = 0, size = 10, sort = "reviewId", direction = Sort.Direction.DESC) Pageable pageable) {

        ReviewResponseGetListDto searchResults = reviewService.searchReview(keyword, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(searchResults);
    }

    @Operation(summary = "식당 리뷰 수정", description = "기존 리뷰을 수정합니다.")
    @ApiResponse(responseCode = "204", description = "리뷰이 성공적으로 업데이트됨")
    @ApiResponse(responseCode = "404", description = "제공된 reviewId로 리뷰을 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PutMapping("/{reviewId}")
    public ResponseEntity<Object> updateReview(HttpServletRequest httpRequest,
                                              @PathVariable Long reviewId,
                                              @Valid @RequestBody ReviewRequestPatchDto reviewUpdateDto) {

        String token = jwtService.extractAccessToken(httpRequest).get();
        Long userId = jwtService.extractId(token).get();

        reviewService.updateReview(reviewId, userId, reviewUpdateDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Review updated successfully");
    }

    @Operation(summary = "식당 리뷰 삭제", description = "리뷰을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "리뷰이 성공적으로 삭제됨")
    @ApiResponse(responseCode = "404", description = "제공된 reviewId로 리뷰을 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Object> deleteReview(HttpServletRequest httpRequest,
                                              @PathVariable Long reviewId) {

        String token = jwtService.extractAccessToken(httpRequest).get();
        Long userId = jwtService.extractId(token).get();

        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Review deleted successfully");
    }
}


