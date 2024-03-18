package com.ssafy.jariyo.domain.s3image.repository;

import com.ssafy.jariyo.domain.store.entity.Store;
import com.ssafy.jariyo.domain.s3image.entity.StoreS3Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreS3ImageRepository extends JpaRepository<StoreS3Image, Long> {
    List<StoreS3Image> findAllByStore(Store store); // Store 엔티티 인스턴스를 기반으로 조회
}