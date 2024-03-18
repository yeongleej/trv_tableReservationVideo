package com.ssafy.jariyo.domain.waiting.service;

import com.ssafy.jariyo.domain.s3image.entity.StoreS3Image;
import com.ssafy.jariyo.domain.s3image.repository.StoreS3ImageRepository;
import com.ssafy.jariyo.domain.store.entity.Store;
import com.ssafy.jariyo.domain.store.repository.StoreRepository;
import com.ssafy.jariyo.domain.user.entity.User;
import com.ssafy.jariyo.domain.user.repository.UserRepository;
import com.ssafy.jariyo.domain.waiting.Serializable.Waiting;
import com.ssafy.jariyo.domain.waiting.dto.WaitingRequestPatchDto;
import com.ssafy.jariyo.domain.waiting.dto.WaitingRequestPostDto;
import com.ssafy.jariyo.domain.waiting.dto.WaitingResponseGetDto;
import com.ssafy.jariyo.global.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class WaitingService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final StoreS3ImageRepository storeS3ImageRepository;
    private final RedisTemplate<String, String> customRedisTemplate;
    private final JwtService jwtService;

    public void addWaiting(Long storeId, Long userId, WaitingRequestPostDto waitingRequestPostDto) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new RuntimeException("Store not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Waiting waiting = new Waiting();

        waiting.setStoreId(store.getStoreId());
        waiting.setStoreName(store.getStoreName());
        waiting.setUserId(user.getUserId());
        waiting.setUserName(user.getNickname());
        waiting.setWaitingIsPostpone(false);
        waiting.setWaitingUserCount(waitingRequestPostDto.getWaitingUserCount());
        waiting.setRegisteredTime(LocalDateTime.now());

        List<StoreS3Image> images = storeS3ImageRepository.findAllByStore(store);
        List<String> imageUrls = images.stream()
                .map(image -> image.getS3Image().getFileUrl()) // S3Image 엔티티에서 파일 URL 추출
                .toList();

        waiting.setStoreImage(imageUrls.get(0));

        // 현재 시간부터 다음 날 자정까지 남은 시간 계산
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT);
        long secondsUntilMidnight = now.until(midnight, ChronoUnit.SECONDS);

        customRedisTemplate.opsForValue().set("waiting:store:" + store.getStoreId() + ":user:" + user.getUserId(),
                waiting.toString(),
                secondsUntilMidnight,
                TimeUnit.SECONDS);

        String key = "waitingLine:store:" + storeId;
        double score = System.currentTimeMillis();
        customRedisTemplate.opsForZSet().add(key, userId.toString(), score);
        customRedisTemplate.expire(key, secondsUntilMidnight, TimeUnit.SECONDS);
    }


    public Waiting getWaitingByStoreIdAndUserId(Long storeId, Long userId) {
        String waitingKey = "waiting:store:" + storeId + ":user:" + userId;
        String waitingStr = customRedisTemplate.opsForValue().get(waitingKey);

        if (waitingStr == null) {
            return null;
        }

        Waiting waiting = parseWaiting(waitingStr);
        waiting.setWaitingUserOrder(getWaitingOrder(waiting.getStoreId(), waiting.getUserId()));
        return waiting;
    }


    public List<Waiting> getWaitingListByStoreId(Long storeId) {
        String waitingLineKey = "waitingLine:store:" + storeId;
        Set<String> userIds = customRedisTemplate.opsForZSet().range(waitingLineKey, 0, -1);

        List<Waiting> waitings = new ArrayList<>();
        int order = 1;
        if (userIds != null) {
            for (String userId : userIds) {
                String waitingKey = "waiting:store:" + storeId + ":user:" + userId;
                String waitingStr = customRedisTemplate.opsForValue().get(waitingKey);
                if (waitingStr != null) {
                    Waiting waiting = parseWaiting(waitingStr);
                    waiting.setWaitingUserOrder(order++);
                    waitings.add(waiting);
                }
            }
        }
        return waitings;
    }

    public void cancelWaiting(Long storeId, Long userId) {
        String redisKey = "waiting:store:" + storeId + ":user:" + userId;
        boolean isRemoved = Boolean.TRUE.equals(customRedisTemplate.delete(redisKey));
        if (!isRemoved) {
            throw new RuntimeException("Failed to cancel waiting");
        }

        String key = "waitingLine:store:" + storeId;
        customRedisTemplate.opsForZSet().remove(key, userId.toString());
    }

    public List<Waiting> getWaitingListByPattern(String pattern) {
        List<Waiting> waitings = new ArrayList<>();

        try (Cursor<byte[]> cursor = customRedisTemplate.execute(
                (RedisConnection connection) -> connection.scan(ScanOptions.scanOptions().match(pattern).count(1000).build()),
                true)) {
            while (Objects.requireNonNull(cursor).hasNext()) {
                String key = new String(cursor.next(), StandardCharsets.UTF_8);
                String waitingStr = customRedisTemplate.opsForValue().get(key);
                if (waitingStr != null) {
                    Waiting waiting = parseWaiting(waitingStr);
                    waiting.setWaitingUserOrder(getWaitingOrder(waiting.getStoreId(), waiting.getUserId()));
                    waitings.add(waiting);
                }
            }
        }
        return waitings;
    }

    public List<Waiting> getWaitingListByUserId(Long userId) {
        String pattern = "waiting:*:*:user:" + userId;
        return getWaitingListByPattern(pattern);
    }

    public void postponeWaiting(Long storeId, Long userId, int postponeCount) {
        String waitingLineKey = "waitingLine:store:" + storeId;

        Waiting waiting = getWaitingByStoreIdAndUserId(storeId, userId);

        if (waiting.getWaitingIsPostpone()) {
            throw new RuntimeException("User has already postponed");
        }

        // 현재 사용자의 순서(점수) 찾기
        Double currentScore = customRedisTemplate.opsForZSet().score(waitingLineKey, userId.toString());
        if (currentScore == null) {
            throw new RuntimeException("User not found in waiting list");
        }

        // 대기열에서 현재 사용자 포함하여 postponeCount 만큼 뒤에 있는 사용자의 점수 찾기
        Set<String> targetUserIds = customRedisTemplate.opsForZSet().range(waitingLineKey, 0, -1);
        if (targetUserIds == null || targetUserIds.size() <= 1) {
            // 대기열에 사용자가 혼자거나 없으면 순서 변경 불필요
            return;
        }

        List<String> userIdsList = new ArrayList<>(targetUserIds);
        int currentUserIndex = userIdsList.indexOf(userId.toString());
        int targetIndex = Math.min(currentUserIndex + postponeCount, userIdsList.size() - 1);

        if (targetIndex == userIdsList.size() - 1) {
            // 대상 인덱스가 대기열의 마지막이라면, 마지막 사용자의 점수를 기반으로 새 점수 계산
            String lastUserId = userIdsList.get(targetIndex);
            Double lastUserScore = customRedisTemplate.opsForZSet().score(waitingLineKey, lastUserId);
            if (lastUserScore != null) {
                customRedisTemplate.opsForZSet().add(waitingLineKey, userId.toString(), lastUserScore + 1);
            }
        } else {
            // 대상 인덱스의 다음 사용자 점수를 찾아 현재 사용자 점수 업데이트
            String nextUserId = userIdsList.get(targetIndex + 1);
            Double nextUserScore = customRedisTemplate.opsForZSet().score(waitingLineKey, nextUserId);
            if (nextUserScore != null) {
                customRedisTemplate.opsForZSet().add(waitingLineKey, userId.toString(), nextUserScore - 1);
            }
        }

        setUserPostponeStatus(storeId, userId);
    }

    public int getWaitingOrder(Long storeId, Long userId) {
        String waitingLineKey = "waitingLine:store:" + storeId;
        Double userScore = customRedisTemplate.opsForZSet().score(waitingLineKey, userId.toString());

        if (userScore == null) {
            throw new RuntimeException("User not found in waiting list");
        }

        Long waitingOrder = customRedisTemplate.opsForZSet().count(waitingLineKey, Double.NEGATIVE_INFINITY, userScore);
        return waitingOrder.intValue();
    }

    public static Waiting parseWaiting(String str) {
        String[] parts = str.split(",");
        Waiting waiting = new Waiting();
        waiting.setStoreId(Long.parseLong(parts[0]));
        waiting.setStoreName(parts[1]);
        waiting.setUserId(Long.parseLong(parts[2]));
        waiting.setUserName(parts[3]);
        waiting.setWaitingUserCount(Integer.parseInt(parts[4]));
        waiting.setWaitingIsPostpone(Boolean.parseBoolean(parts[5]));
        waiting.setRegisteredTime(LocalDateTime.parse(parts[6]));
        waiting.setStoreImage(parts[8]);
        return waiting;
    }

    public void deleteWaiting(Long storeId, Long userId, HttpServletRequest httpServletRequest) {

        String token = jwtService.extractAccessToken(httpServletRequest).get();

        Long ownerId = jwtService.extractId(token).get();
        Long ownerId2 = storeRepository.getReferenceById(storeId).getUser().getUserId();

        if (!ownerId.equals(ownerId2)) {
            throw new RuntimeException("Unauthorized to update Waiting with StoreId: " + storeId);
        }

        String redisKey = "waiting:store:" + storeId + ":user:" + userId;
        boolean isRemoved = Boolean.TRUE.equals(customRedisTemplate.delete(redisKey));
        if (!isRemoved) {
            throw new RuntimeException("Failed to cancel waiting");
        }

        String key = "waitingLine:store:" + storeId;
        customRedisTemplate.opsForZSet().remove(key, userId.toString());
    }

    public void setUserPostponeStatus(Long storeId, Long userId) {
        String waitingKey = "waiting:store:" + storeId + ":user:" + userId;
        String waitingStr = customRedisTemplate.opsForValue().get(waitingKey);

        if (waitingStr == null) {
            throw new RuntimeException("Waiting not found for storeId: " + storeId + " and userId: " + userId);
        }

        Waiting waiting = parseWaiting(waitingStr);
        waiting.setWaitingIsPostpone(true);
        String updatedWaitingStr = waiting.toString();

        // 현재 시간부터 다음 날 자정까지 남은 시간 계산
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT);
        long secondsUntilMidnight = now.until(midnight, ChronoUnit.SECONDS);

        customRedisTemplate.opsForValue().set(
                waitingKey,
                updatedWaitingStr,
                secondsUntilMidnight,
                TimeUnit.SECONDS);
    }

}
