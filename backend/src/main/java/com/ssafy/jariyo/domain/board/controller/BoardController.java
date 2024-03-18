package com.ssafy.jariyo.domain.board.controller;

import com.ssafy.jariyo.domain.board.dto.*;
import com.ssafy.jariyo.domain.board.service.BoardService;
import com.ssafy.jariyo.domain.board.dto.BoardResponseGetDto;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Tag(name = "board", description = "게시판 API")
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;
    private final JwtService jwtService;

    @Operation(summary = "식당 게시판 작성", description = "식당 게시판에 새로운 글을 추가합니다.")
    @ApiResponse(responseCode = "201", description = "게시판이 성공적으로 작성됨")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PostMapping
    public ResponseEntity<Object> addBoard(HttpServletRequest httpRequest,
                                           @ModelAttribute BoardRequestPostDto boardRequestPostDto) throws IOException {
        Optional<String> tokenOptional = jwtService.extractAccessToken(httpRequest);
        Long userId = null; // 초기 userId 값을 null로 설정

        // 토큰이 존재할 경우 userId 값을 추출
        if (tokenOptional.isPresent()) {
            String token = tokenOptional.get();
            Optional<Long> userIdOptional = jwtService.extractId(token);
            if (userIdOptional.isPresent()) {
                userId = userIdOptional.get();
            }
        }

        boardService.addBoard(userId, boardRequestPostDto, boardRequestPostDto.getUpfile());
        return ResponseEntity.status(HttpStatus.CREATED).body("Board added successfully");
    }

    @Operation(summary = "식당 게시판 조회", description = "게시판을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "게시판 조회에 성공", content = @Content(schema = @Schema(implementation = BoardResponseGetDto.class)))
    @ApiResponse(responseCode = "404", description = "제공된 boardId로 게시판을 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/{boardId}")
    public ResponseEntity<Object> getBoard(@PathVariable Long boardId) {
        BoardResponseGetDto boardResponseDto = boardService.getBoard(boardId);
        return ResponseEntity.status(HttpStatus.OK).body(boardResponseDto);
    }

    @Operation(summary = "상점 검색", description = "키워드, 위치, 카테고리, 정렬 기준, 페이지, 페이지 크기를 이용하여 상점을 검색합니다.")
    @ApiResponse(responseCode = "200", description = "상점 검색 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BoardResponseGetDto.class))))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/search")
    public ResponseEntity<Object> searchBoard(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String domain,
            @PageableDefault(page = 0, size = 10, sort = "boardId", direction = Sort.Direction.DESC) Pageable pageable) {

        BoardResponseGetListDto searchResults = boardService.searchBoard(keyword, domain, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(searchResults);
    }

    @Operation(summary = "식당 게시판 수정", description = "기존 게시판을 수정합니다.")
    @ApiResponse(responseCode = "204", description = "게시판이 성공적으로 업데이트됨")
    @ApiResponse(responseCode = "404", description = "제공된 boardId로 게시판을 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PutMapping("/{boardId}")
    public ResponseEntity<Object> updateBoard(HttpServletRequest httpRequest,
                                              @PathVariable Long boardId,
                                              @Valid @RequestBody BoardRequestPatchDto boardUpdateDto) {

        String token = jwtService.extractAccessToken(httpRequest).get();
        Long userId = jwtService.extractId(token).get();

        boardService.updateBoard(boardId, userId, boardUpdateDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Board updated successfully");
    }

    @Operation(summary = "식당 게시판 삭제", description = "게시판을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "게시판이 성공적으로 삭제됨")
    @ApiResponse(responseCode = "404", description = "제공된 boardId로 게시판을 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Object> deleteBoard(HttpServletRequest httpRequest,
                                              @PathVariable Long boardId) {

        String token = jwtService.extractAccessToken(httpRequest).get();
        Long userId = jwtService.extractId(token).get();

        boardService.deleteBoard(boardId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Board deleted successfully");
    }
}
