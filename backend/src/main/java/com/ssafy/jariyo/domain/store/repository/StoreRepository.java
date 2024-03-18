package com.ssafy.jariyo.domain.store.repository;

import com.ssafy.jariyo.domain.store.entity.Store;
import com.ssafy.jariyo.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Store findByStoreId(Long storeId);

    Store findByUser(User user);

    List<Store> findAllByStoreNameContainingAndStoreAddressContaining (String name, String location, Pageable pageable);

    Page<Store> findAllByStoreAddressContaining(String location, Pageable pageable);

    @Query("SELECT s FROM Store s WHERE s.storeName LIKE %:keyword% AND s.storeAddress LIKE %:keyword%")
    Page<Store> findAllByCustomQuery(String keyword, Pageable pageable);

    List<Store> findAll(Specification<Store> spec, Sort sort);

    Store findByStoreAddress(String address);
}

