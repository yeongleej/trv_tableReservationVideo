package com.ssafy.jariyo.domain.reservation.repository;

import com.ssafy.jariyo.domain.reservation.entity.ZPass;
import com.ssafy.jariyo.domain.store.entity.Store;
import com.ssafy.jariyo.domain.user.entity.User;
import com.ssafy.jariyo.global.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ZPassRepository extends JpaRepository<ZPass, Long> {

    ZPass findByZpassId(Long zpassId);

    // 사용자별 예약패스 조회
    List<ZPass> findAllByUser(User user);

    // 가게별 예약패스 조회
//    List<ZPass> findAllByStore(Store store);
//
//    // 사용자 & 가게별 예약패스 조회
//    List<ZPass> findAllByUserAndStore(User user, Store store);

    //만료되지 않은(유효한) 사용자 예약패스 조회
    ZPass findByUserAndZpassStatus(User user, Status zpassStatus);

    //만료되지 않은(유효한) 가게별 예약패스 조회
//    List<ZPass> findAllByStoreAndZpassStatus(Store store, Status zpassStatus);
}
