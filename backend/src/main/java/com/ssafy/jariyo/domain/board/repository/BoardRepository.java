package com.ssafy.jariyo.domain.board.repository;

import com.ssafy.jariyo.domain.board.entity.Board;
import com.ssafy.jariyo.domain.board.entity.BoardDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Board findByBoardId(Long boardId);

    Page<Board> findAllByBoardTitleContaining(String BoardTitle, Pageable pageable);

    Page<Board> findAllByBoardTitleContainingAndBoardDomain(String BoardTitle, BoardDomain domain, Pageable pageable);

}
