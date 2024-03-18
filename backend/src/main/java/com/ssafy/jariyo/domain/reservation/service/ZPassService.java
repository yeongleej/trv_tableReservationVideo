package com.ssafy.jariyo.domain.reservation.service;

import com.ssafy.jariyo.domain.reservation.dto.request.MakePayRequest;
import com.ssafy.jariyo.domain.reservation.dto.request.PayInfoDto;
import com.ssafy.jariyo.domain.reservation.dto.request.PayRequest;
import com.ssafy.jariyo.domain.reservation.dto.request.ZPassRequestDto;
import com.ssafy.jariyo.domain.reservation.dto.response.PayApproveResponseDto;
import com.ssafy.jariyo.domain.reservation.dto.response.PayReadyResponseDto;
import com.ssafy.jariyo.domain.reservation.dto.response.ZPassResponseActiveDto;
import com.ssafy.jariyo.domain.reservation.dto.response.ZPassResponseDto;
import com.ssafy.jariyo.domain.reservation.entity.ZPass;
import com.ssafy.jariyo.domain.reservation.repository.ZPassRepository;
import com.ssafy.jariyo.domain.store.entity.Store;
import com.ssafy.jariyo.domain.store.repository.StoreRepository;
import com.ssafy.jariyo.domain.user.entity.User;
import com.ssafy.jariyo.domain.user.repository.UserRepository;
import com.ssafy.jariyo.global.entity.Status;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ZPassService {

    private final ZPassRepository zPassRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final MakePayRequest makePayRequest;

    @Value("${pay.admin-key}")
    private String adminKey;

//    @Value("${pay.host}")
//    private String host;

//    private final RedissonClient redissonClient;
//    private final int EMPTY = 0;
//
//    public String keyResolver(String code) {
//        return "ZPASS:" + code;
//    }

//    // 가게별 예약패스 조회
//    public List<ZPassResponseDto> getZPassByStore(Long storeId) {
//        Store store = storeRepository.findByStoreId(storeId);
//        List<ZPass> zPassList = zPassRepository.findAllByStore(store);
//        List<ZPassResponseDto> zPassResponseDtoList = new ArrayList<>();
//        for (ZPass zPass : zPassList) {
//            zPassResponseDtoList.add(
//                    ZPassResponseDto.builder()
//                            .zpassId(zPass.getZpassId())
//                            .zpassName(zPass.getZpassName())
//                            .zpassStatus(zPass.getZpassStatus())
//                            .build()
//            );
//        }
//        return zPassResponseDtoList;
//    }

    // 사용자별 예약패스 조회
//    public List<ZPassResponseDto> getZPassByUser(Long userId) {
//        User user = userRepository.findByUserId(userId);
//        List<ZPass> zPassList = zPassRepository.findAllByUser(user);
//        List<ZPassResponseDto> zPassResponseDtoList = new ArrayList<>();
//        for (ZPass zPass : zPassList) {
//            zPassResponseDtoList.add(
//                    ZPassResponseDto.builder()
//                            .zpassId(zPass.getZpassId())
//                            .zpassName(zPass.getZpassName())
//                            .zpassStatus(zPass.getZpassStatus())
//                            .build()
//            );
//        }
//        return zPassResponseDtoList;
//    }

//    // 사용자&가게별 예약패스 조회
//    public List<ZPassResponseDto> getZPassByUserAndStore(Long userId, Long storeId) {
//        Store store = storeRepository.findByStoreId(storeId);
//        User user = userRepository.findByUserId(userId);
//        List<ZPass> zPassList = zPassRepository.findAllByUserAndStore(user, store);
//        List<ZPassResponseDto> zPassResponseDtoList = new ArrayList<>();
//        for (ZPass zPass : zPassList) {
//            zPassResponseDtoList.add(
//                    ZPassResponseDto.builder()
//                            .zpassId(zPass.getZpassId())
//                            .zpassName(zPass.getZpassName())
//                            .zpassStatus(zPass.getZpassStatus())
//                            .build()
//            );
//        }
//        return zPassResponseDtoList;
//    }

    // 사용자별 유효 예약패스 조회
    public ZPassResponseActiveDto getAvailableZPassByUser(Long userId) {
        User user = userRepository.findByUserId(userId);
        ZPass zPass = zPassRepository.findByUserAndZpassStatus(user, Status.ACTIVE);
        Long zpassId = null;
        Status status = null;
        if(zPass != null) {
            status = zPass.getZpassStatus();
            zpassId = zPass.getZpassId();
        }
        log.info(">>> 사용자 {}, 예약 패스 존재여부 : {}",userId, status);
        ZPassResponseActiveDto zPassResponseDto = ZPassResponseActiveDto.builder()
                .userId(userId)
                .zpassId(zpassId)
                .zpassStatus(status)
                .build();
        return zPassResponseDto;
    }

//    // 가게별 유효 예약패스 조회
//    public List<ZPassResponseActiveDto> getAvailableZPassByStore(Long storeId) {
//        Store store = storeRepository.findByStoreId(storeId);
//        List<ZPass> zPassList = zPassRepository.findAllByStoreAndZpassStatus(store, Status.ACTIVE);
//        List<ZPassResponseActiveDto> zPassResponseDtoList = new ArrayList<>();
//        for (ZPass zPass : zPassList) {
//            User user = zPass.getUser();
//            zPassResponseDtoList.add(
//                    ZPassResponseActiveDto.builder()
//                            .zpassId(zPass.getZpassId())
//                            .storeId(storeId)
//                            .storeName(store.getStoreName())
//                            .userId(user.getUserId())
//                            .nickname(user.getNickname())
//                            .zpassName(zPass.getZpassName())
//                            .zpassStatus(zPass.getZpassStatus())
//                            .build()
//            );
//        }
//        return zPassResponseDtoList;
//    }

    // 예약패스 등록
    public void registZPass(Long userId, PayApproveResponseDto approveDto) {
//        Store store = storeRepository.findByStoreId(zPassRequestDto.getStoreId());
        User user = userRepository.findByUserId(userId);
//        String date_str = approveDto.getCreated_at();
//        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date = transFormat.parse(date_str);
//
//        Calendar cal1 = Calendar.getInstance();
//        cal1.setTime(date); // 시간 설정
//        cal1.add(Calendar.MONTH, 1); // 월 연산
//
//        Date vaildDate = new Date(cal1.getTimeInMillis());

        ZPass regZPass = ZPass.builder()
                .user(user)
                .zpassName(approveDto.getItem_name())
                .zpassCreatedDates(approveDto.getCreated_at())
                .zpassStatus(Status.ACTIVE)
                .build();
        zPassRepository.save(regZPass);
    }

    // 예약패스 사용처리
    public void useZPass(Long zpassId) {
        ZPass myZPass = zPassRepository.findByZpassId(zpassId);
        if (myZPass != null) {
            myZPass.setZpassStatus(Status.INACTIVE);
//            myZPass.setDeleted(true);
        } else {
            log.error("Not Found ZPass");
            throw new EntityNotFoundException("ZPass with ID " + zpassId + " not found");
        }
    }

    // 예약패스 삭제
    public void expireZPass(Long zpassId) {
        ZPass myZPass = zPassRepository.findByZpassId(zpassId);
        myZPass.setDeleted(true);
    }
    // 예약패스 이름 생성
    public String getZPassName(Long storeId, Long userId) {
        return "ZPASS:" + storeId + "-" + userId + "-" + LocalDateTime.now();
    }


    /** 카오페이 결제를 시작하기 위해 상세 정보를 카카오페이 서버에 전달하고 결제 고유 번호(TID)를 받는 단계입니다.
     * 어드민 키를 헤더에 담아 파라미터 값들과 함께 POST로 요청합니다.
     * 테스트  가맹점 코드로 'TC0ONETIME'를 사용 */
    @Transactional
    public PayReadyResponseDto getRedirectUrl(Long userId, PayInfoDto payInfoDto)throws Exception{
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String name = authentication.getName();
//        Member member=memberRepository.findByEmail(name)
//                .orElseThrow(()-> new Exception("해당 유저가 존재하지 않습니다."));
//
//        Long id=member.getId();
        log.info(">> 카카오 페이 redirectUrl 요청 service <<");
        User user = userRepository.findByUserId(userId);
        HttpHeaders headers=new HttpHeaders();

        /** 요청 헤더 */
        String auth = "KakaoAK " + adminKey;
        headers.set("Content-type","application/x-www-form-urlencoded;charset=utf-8");
        headers.set("Authorization",auth);

        /** 요청 Body */
        PayRequest payRequest=makePayRequest.getReadyRequest(userId, payInfoDto);
        log.info(">>>> 요청 body : {}",payRequest);

        /** Header와 Body 합쳐서 RestTemplate로 보내기 위한 밑작업 */
        HttpEntity<MultiValueMap<String, String>> urlRequest = new HttpEntity<>(payRequest.getMap(), headers);
        log.info(">>>> 요청 URL : {}",urlRequest);

        /** RestTemplate로 Response 받아와서 DTO로 변환후 return */
        // >>>> !!!!!! 여기서 버그 발생
        RestTemplate rt = new RestTemplate();
        PayReadyResponseDto payReadyResponseDto = rt.postForObject(payRequest.getUrl(), urlRequest, PayReadyResponseDto.class);
        log.info(">>>> 반환 dto : {}",payReadyResponseDto);

        // payment id setting
        user.setTid(payReadyResponseDto.getTid());
//        member.updateTid(payReadyResponseDto.getTid());


        return payReadyResponseDto;
    }

    @Transactional
    public PayApproveResponseDto getApprove(String pgToken, Long userId)throws Exception{

//        Member member=memberRepository.findById(id)
//                .orElseThrow(()->new Exception("해당 유저가 존재하지 않습니다."));
//
//        String tid=member.getTid();

        User user = userRepository.findByUserId(userId);
        String tid = user.getTid();
        HttpHeaders headers=new HttpHeaders();
        String auth = "KakaoAK " + adminKey;

        /** 요청 헤더 */
        headers.set("Content-type","application/x-www-form-urlencoded;charset=utf-8");
        headers.set("Authorization",auth);

        /** 요청 Body */
        PayRequest payRequest=makePayRequest.getApproveRequest(tid, userId, pgToken);


        /** Header와 Body 합쳐서 RestTemplate로 보내기 위한 밑작업 */
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(payRequest.getMap(), headers);

        // 요청 보내기
        RestTemplate rt = new RestTemplate();
        PayApproveResponseDto payApproveResponseDto = rt.postForObject(payRequest.getUrl(), requestEntity, PayApproveResponseDto.class);


        return payApproveResponseDto;
    }
}
