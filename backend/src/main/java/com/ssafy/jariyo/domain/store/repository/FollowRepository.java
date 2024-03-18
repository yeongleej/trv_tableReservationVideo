package com.ssafy.jariyo.domain.store.repository;

import com.ssafy.jariyo.domain.store.entity.Follow;
import com.ssafy.jariyo.domain.store.entity.Store;
import com.ssafy.jariyo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    List<Follow> findAllByStoreAndIsDeleted(Store store, Boolean isDeleted);

    List<Follow> findAllByUserAndIsDeleted(User user, Boolean isDeleted);

    Follow findByStoreAndUser(Store store, User user);

    Follow findByStoreAndUserAndIsDeleted(Store store, User user, Boolean isDeleted);

}
