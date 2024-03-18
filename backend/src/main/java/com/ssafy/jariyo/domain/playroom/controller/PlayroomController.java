package com.ssafy.jariyo.domain.playroom.controller;

import com.ssafy.jariyo.domain.playroom.dto.PlayroomRequestUpdateDto;
import com.ssafy.jariyo.domain.playroom.dto.PlayroomResponseGetDto;
import com.ssafy.jariyo.domain.playroom.dto.PlayroomResponseGetListDto;
import com.ssafy.jariyo.domain.playroom.service.PlayroomService;
import com.ssafy.jariyo.domain.s3image.service.S3ImageService;
import com.ssafy.jariyo.global.jwt.service.JwtService;
import io.lettuce.core.ScriptOutputType;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "playroom", description = "플레이룸 API")
@RequestMapping("/api/playroom")
public class PlayroomController {

    private final PlayroomService playroomService;
    private final JwtService jwtService;

    @Operation(summary = "플레이룸 정보 조회", description = "특정 등록번호(StoreId)에 대한 플레이룸 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "플레이룸 정보 조회에 성공했습니다.", content = @Content(schema = @Schema(implementation = PlayroomResponseGetDto.class)))
    @ApiResponse(responseCode = "404", description = "제공된 StoreId로 플레이룸를 찾을 수 없습니다.")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.")
    @GetMapping("/{StoreId}")
    public ResponseEntity<Object> getPlayroomInfo(@PathVariable Long StoreId) {
        PlayroomResponseGetDto playroomResponseGetDto = playroomService.getPlayroomInfo(StoreId);
        return ResponseEntity.status(HttpStatus.OK).body(playroomResponseGetDto);
    }

    @Operation(summary = "플레이룸 검색", description = "키워드, 위치, 카테고리, 정렬 기준, 페이지, 페이지 크기를 이용하여 플레이룸을 검색합니다.")
    @ApiResponse(responseCode = "200", description = "플레이룸 검색 성공",
            content = @Content(schema = @Schema(implementation = PlayroomResponseGetListDto.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/search")
    public ResponseEntity<Object> searchPlayroom(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String category,
            @PageableDefault(page = 0, size = 10, sort = "StoreId", direction = Sort.Direction.DESC) Pageable pageable) {

        PlayroomResponseGetListDto searchResults = playroomService.searchPlayroomsWithKeywordAndCategory(keyword, category, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(searchResults);
    }

    @Operation(summary = "플레이룸 정보 업데이트", description = "특정 플레이룸의 정보를 업데이트합니다.")
    @ApiResponse(responseCode = "204", description = "플레이룸 정보가 성공적으로 업데이트되었습니다.")
    @ApiResponse(responseCode = "404", description = "제공된 StoreId로 플레이룸를 찾을 수 없습니다.")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터입니다.")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.")
    @PutMapping("/{StoreId}")
    public ResponseEntity<Object> updatePlayroomInfo(HttpServletRequest httpRequest,
                                                     @PathVariable Long StoreId,
                                                     @Valid @RequestBody PlayroomRequestUpdateDto playroomRequestUpdateDto) {

        String token = jwtService.extractAccessToken(httpRequest).get();
        Long userId = jwtService.extractId(token).get();

        System.out.println("======== 시청자수 업데이트 ========");
        playroomService.updatePlayroomInfo(userId, StoreId, playroomRequestUpdateDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Playroom info updated successfully");
    }

    @Operation(summary = "플레이룸 썸네일 업데이트", description = "특정 플레이룸의 썸네일을 업데이트합니다.")
    @ApiResponse(responseCode = "204", description = "플레이룸 정보가 성공적으로 업데이트되었습니다.")
    @ApiResponse(responseCode = "404", description = "제공된 StoreId로 플레이룸를 찾을 수 없습니다.")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터입니다.")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.")
    @PutMapping(value = "/{StoreId}/thumbnail", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadThumbnail(@PathVariable Long StoreId, @RequestPart("image") MultipartFile file) {
        try {
            playroomService.updateTumbnail(StoreId, file);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }

}
