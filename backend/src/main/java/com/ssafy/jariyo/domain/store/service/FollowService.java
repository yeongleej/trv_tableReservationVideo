package com.ssafy.jariyo.domain.store.service;

import com.ssafy.jariyo.domain.s3image.entity.StoreS3Image;
import com.ssafy.jariyo.domain.s3image.repository.StoreS3ImageRepository;
import com.ssafy.jariyo.domain.store.dto.response.StoreResponseDefaultDto;
import com.ssafy.jariyo.domain.store.entity.Follow;
import com.ssafy.jariyo.domain.store.entity.Store;
import com.ssafy.jariyo.domain.store.repository.FollowRepository;
import com.ssafy.jariyo.domain.store.repository.StoreRepository;
import com.ssafy.jariyo.domain.user.dto.UserResponseDto;
import com.ssafy.jariyo.domain.user.entity.User;
import com.ssafy.jariyo.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FollowService {
    private final FollowRepository followRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final StoreS3ImageRepository storeS3ImageRepository;
    public void followStore(Long storeId, Long userId) {
        Store store = storeRepository.findByStoreId(storeId);
        User user = userRepository.findByUserId(userId);
        // 팔로우 존재여부 확인 (+ 삭제되지 않은)
        Follow isExistFollower = followRepository.findByStoreAndUser(store, user);
        log.info(">> 팔로우 요청 : {}",isExistFollower);
        // 없는 경우 팔로우 진행
        if(isExistFollower == null) {
            Follow follow = Follow.builder()
                    .store(store)
                    .user(userRepository.findByUserId(userId))
                    .build();
            followRepository.save(follow);
            // 구독자 수 증가
            store.setStoreFollowCount(store.getStoreFollowCount()+1);
        } else if (isExistFollower.isDeleted()) {
            // 구독 다시 진행
            isExistFollower.setDeleted(false);
            store.setStoreFollowCount(store.getStoreFollowCount()+1);
        } else {
            log.info("이미 구독자입니다.");
        }

    }

    public void unfollowStore(Long storeId, Long userId) {
        Store store = storeRepository.findByStoreId(storeId);
        User user = userRepository.findByUserId(userId);
        Follow follow = followRepository.findByStoreAndUser(store, user);
        follow.setDeleted(true);
        // 구독자 수 감소
        store.setStoreFollowCount(store.getStoreFollowCount()-1);
    }

    public List<UserResponseDto> getStoreFollowers(Long storeId) {
        Store store = storeRepository.findByStoreId(storeId);
        List<Follow> followList = followRepository.findAllByStoreAndIsDeleted(store, false);
        List<UserResponseDto> users = new ArrayList<>();
        for(Follow follow: followList){
            User user = follow.getUser();
            users.add(
                    UserResponseDto.builder()
                            .userId(user.getUserId())
                            .nickname(user.getNickname())
                            .email(user.getEmail())
                            .build()
            );
        }
        return users;
    }

    public List<StoreResponseDefaultDto> getUserFollowedStores(Long userId) {
        User user = userRepository.findByUserId(userId);
        List<Follow> followList = followRepository.findAllByUserAndIsDeleted(user, false);
        List<StoreResponseDefaultDto> stores = new ArrayList<>();

        for(Follow follow: followList){
            Store store = follow.getStore();

            List<StoreS3Image> images = storeS3ImageRepository.findAllByStore(store);
            List<String> imageUrls = images.stream()
                    .map(image -> image.getS3Image().getFileUrl()) // S3Image 엔티티에서 파일 URL 추출
                    .collect(Collectors.toList());

            stores.add(
                    StoreResponseDefaultDto.builder()
                            .storeId(store.getStoreId())
                            .ownerName(store.getUser().getName())
                            .storeName(store.getStoreName())
                            .storePhone(store.getStorePhone())
                            .storeDetail(store.getStoreDetail())
                            .storeBusinessNumber(store.getStoreBusinessNumber())
                            .storeMenu(store.getStoreMenu())
                            .storeAddress(store.getStoreAddress())
                            .storeFollowCount(store.getStoreFollowCount())
                            .storeOperationHours(store.getStoreOperationHours())
                            .storeOperationDates(store.getStoreOperationDates())
                            .storeReservationStatus(store.getStoreReservationStatus())
                            .storePositionX(store.getStorePositionX())
                            .storePositionY(store.getStorePositionY())
                            .storeIsWaiting(store.getStoreIsWaiting())
                            .storeRating(store.getStoreRating())
                            .storeBusinessFile(store.getStoreBusinessFile())
                            .images(imageUrls)
                            .build()
            );
        }
        return stores;
    }
}
