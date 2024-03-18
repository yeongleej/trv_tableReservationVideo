package com.ssafy.jariyo.domain.s3image.repository;

import com.ssafy.jariyo.domain.board.entity.Board;
import com.ssafy.jariyo.domain.s3image.entity.BoardS3Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardS3ImageRepository extends JpaRepository<BoardS3Image, Long> {
    List<BoardS3Image> findAllByBoard(Board board); // Board 엔티티 인스턴스를 기반으로 조회
}