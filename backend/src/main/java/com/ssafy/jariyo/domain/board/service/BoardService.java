package com.ssafy.jariyo.domain.board.service;

import com.ssafy.jariyo.domain.board.dto.BoardRequestPatchDto;
import com.ssafy.jariyo.domain.board.dto.BoardRequestPostDto;
import com.ssafy.jariyo.domain.board.dto.BoardResponseGetDto;
import com.ssafy.jariyo.domain.board.dto.BoardResponseGetListDto;
import com.ssafy.jariyo.domain.board.entity.Board;
import com.ssafy.jariyo.domain.board.entity.BoardDomain;
import com.ssafy.jariyo.domain.board.repository.BoardRepository;
import com.ssafy.jariyo.domain.s3image.entity.BoardS3Image;
import com.ssafy.jariyo.domain.s3image.repository.BoardS3ImageRepository;
import com.ssafy.jariyo.domain.s3image.service.S3ImageService;
import com.ssafy.jariyo.domain.store.entity.Store;
import com.ssafy.jariyo.domain.store.repository.StoreRepository;
import com.ssafy.jariyo.domain.user.entity.User;
import com.ssafy.jariyo.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final S3ImageService s3ImageService;
    private final BoardS3ImageRepository boardS3ImageRepository;

    public void addBoard(Long userId, BoardRequestPostDto boardRequestPostDto, MultipartFile[] files) throws IOException {

        User user;
        Store store;

        if (userId != null) {
            user = userRepository.findByUserId(userId);
            store = null;

            if (boardRequestPostDto.getUseStoreId()) {
                store = storeRepository.findByUser(user);
            }
        } else {
            user = null;
            store = null;
        }


        Board board = new Board(
                user,
                store,
                getDomain(boardRequestPostDto.getDomain()),
                boardRequestPostDto.getTitle(),
                boardRequestPostDto.getContent()
        );

        s3ImageService.uploadFilesOnBoard(board, files);
        boardRepository.save(board);
    }

    public BoardResponseGetDto getBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + boardId));

        // Board 엔티티 인스턴스를 사용하여 모든 관련 이미지 조회
        List<BoardS3Image> images = boardS3ImageRepository.findAllByBoard(board);

        BoardResponseGetDto boardResponseGetDto = toDto(board);
        List<String> imageUrls = images.stream()
                .map(image -> image.getS3Image().getFileUrl()) // S3Image 엔티티에서 파일 URL 추출
                .collect(Collectors.toList());
        boardResponseGetDto.setImages(imageUrls);

        return boardResponseGetDto;
    }


    public BoardResponseGetListDto searchBoard(String keyword, String domain, Pageable pageable) {
        Page<Board> boardList;

        if (domain.isEmpty()) {
            boardList = boardRepository.findAllByBoardTitleContaining(keyword, pageable);
        } else {
            boardList = boardRepository.findAllByBoardTitleContainingAndBoardDomain(keyword, getDomain(domain), pageable);
        }

        BoardResponseGetListDto boardResponseGetListDto = new BoardResponseGetListDto();
        boardResponseGetListDto.setList(toDtoList(boardList.getContent()));
        boardResponseGetListDto.setTotalPages(boardList.getTotalPages());
        boardResponseGetListDto.setTotalElements(boardList.getTotalElements());

        return boardResponseGetListDto;
    }

    public void updateBoard(Long boardId, Long userId, BoardRequestPatchDto boardUpdateDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + boardId));

        if (!board.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only update your own posts.");
        }

        board.setBoardTitle(boardUpdateDto.getTitle());
        board.setBoardContent(boardUpdateDto.getContent());
        board.setBoardDomain(getDomain(boardUpdateDto.getDomain()));

        boardRepository.save(board);
    }

    public void deleteBoard(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + boardId));

        if (!board.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own posts.");
        }

        board.setDeleted(true);
        boardRepository.save(board);
    }

    public BoardDomain getDomain(String domain) {
        if (Objects.equals(domain, "RECIPE")) {
            return BoardDomain.RECIPE;
        }
        if (Objects.equals(domain, "NOTIFICATION")) {
            return BoardDomain.NOTIFICATION;
        }
        return BoardDomain.GENERAL;
    }

    public static BoardResponseGetDto toDto(Board board) {
        if (board == null) {
            return null;
        }

        BoardResponseGetDto dto = new BoardResponseGetDto();
        dto.setBoardId(board.getBoardId());

        if (board.getUser() == null) {
            dto.setUserId(0L);
            dto.setUserName("익명");
        }
        else {
            dto.setUserId(board.getUser().getUserId());
            dto.setUserName(board.getUser().getNickname());
        }
        if (board.getStore() != null) {
            dto.setStoreId(board.getStore().getStoreId());
        }
        dto.setTitle(board.getBoardTitle());
        dto.setContent(board.getBoardContent());
        dto.setDomain(board.getBoardDomain());
        dto.setRegDate(board.getRegDate());

        return dto;
    }

    public static List<BoardResponseGetDto> toDtoList(List<Board> boards) {
        if (boards == null) {
            return null;
        }

        return boards.stream()
                .map(BoardService::toDto)
                .collect(Collectors.toList());
    }
}
