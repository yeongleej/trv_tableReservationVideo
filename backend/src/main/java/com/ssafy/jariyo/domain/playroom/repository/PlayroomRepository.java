package com.ssafy.jariyo.domain.playroom.repository;

import com.ssafy.jariyo.domain.board.entity.Board;
import com.ssafy.jariyo.domain.playroom.dto.PlayroomResponseGetDto;
import com.ssafy.jariyo.domain.playroom.entity.Playroom;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayroomRepository extends JpaRepository<Playroom, Long> {
    Playroom findByStore_StoreId(Long storeId);

    @Query("SELECT p FROM Playroom p WHERE " +
            "(p.title LIKE %:keyword% AND p.category LIKE %:category%) AND " +
            "p.isDeleted = false AND " +
            "(p.chatting = true OR p.calling = true OR p.broadcasting = true OR p.waiting = true)")
    Page<Playroom> search(
            @Param("keyword") String keyword,
            @Param("category") String category,
            Pageable pageable);
}
