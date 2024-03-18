package com.ssafy.jariyo.domain.reservation.repository;

import com.ssafy.jariyo.domain.reservation.entity.DiningTable;
import com.ssafy.jariyo.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {

    // 가게 정보로 테이블 정보 조회
    List<DiningTable> findByStore(Store store);


    List<DiningTable> findByStoreAndDiningTableIsAvailable(Store store, boolean diningTableIsVailable);

    DiningTable findByDiningTableId(Long diningTableId);

    DiningTable findByStoreAndDiningTableNumber(Store store, Integer diningTableNumber);

    @Modifying
    @Transactional
    @Query("delete from DiningTable d where d.store = :store")
    void deleteByStore(Store store);
}
