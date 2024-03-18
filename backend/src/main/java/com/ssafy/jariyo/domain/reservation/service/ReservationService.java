package com.ssafy.jariyo.domain.reservation.service;

import com.ssafy.jariyo.domain.reservation.dto.response.*;
import com.ssafy.jariyo.domain.reservation.dto.request.ReservationRequestDto;
import com.ssafy.jariyo.domain.reservation.entity.DiningTable;
import com.ssafy.jariyo.domain.reservation.entity.Reservation;
import com.ssafy.jariyo.domain.reservation.entity.ZPass;
import com.ssafy.jariyo.domain.reservation.repository.DiningTableRepository;
import com.ssafy.jariyo.domain.reservation.repository.ReservationRepository;
import com.ssafy.jariyo.domain.reservation.repository.ZPassRepository;
import com.ssafy.jariyo.domain.s3image.entity.StoreS3Image;
import com.ssafy.jariyo.domain.s3image.repository.StoreS3ImageRepository;
import com.ssafy.jariyo.domain.store.entity.Store;
import com.ssafy.jariyo.domain.store.repository.StoreRepository;
import com.ssafy.jariyo.domain.store.service.StoreService;
import com.ssafy.jariyo.domain.user.entity.User;
import com.ssafy.jariyo.domain.user.repository.UserRepository;
import com.ssafy.jariyo.global.config.EmailConfig;
import com.ssafy.jariyo.global.entity.Status;
import com.sun.xml.messaging.saaj.soap.ver1_1.BodyElement1_1Impl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final RedissonClient redissonClient;
    private final int EMPTY = 0;

    public String keyResolver(String code) {
        return "RESERVATION:" + code;
    }

    private final ReservationRepository reservationRepository;
    private final DiningTableRepository diningTableRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ZPassRepository zPassRepository;
    private final StoreService storeService;
    private final ZPassService zPassService;
    private final EmailConfig emailConfig;
    private final StoreS3ImageRepository storeS3ImageRepository;

    /**
     * 사용자별로 예약된 현황 조회
     */
    public List<ReservationByUserResponseDto> getReservationByUser(Long userId) {
        User user = userRepository.findByUserId(userId);
        List<Reservation> rList = reservationRepository.findAllByUser(user);
        List<ReservationByUserResponseDto> rDtoList = new ArrayList<>();
        for (Reservation r : rList) {
//            DiningTableResponseDto diningTableResponseDto = DiningTableResponseDto.builder()
//                    .diningTableNumber(r.getDiningTable().getDiningTableNumber())
//                    .diningTableCapacity(r.getDiningTable().getDiningTableCapacity())
//                    .diningTableType(r.getDiningTable().getDiningTableType())
//                    .diningTableX(r.getDiningTable().getDiningTableX())
//                    .diningTableY(r.getDiningTable().getDiningTableY())
//                    .build();

            List<StoreS3Image> images = storeS3ImageRepository.findAllByStore(r.getStore());
            List<String> imageUrls = images.stream()
                    .map(image -> image.getS3Image().getFileUrl()) // S3Image 엔티티에서 파일 URL 추출
                    .toList();

            ReservationByUserResponseDto uDto = ReservationByUserResponseDto.builder()
                    .reservationId(r.getReservationId())
                    .storeName(r.getStore().getStoreName())
                    .reservationDate(r.getReservationDate())
                    .reservationTime(r.getReservationTime())
                    .reservationUserCount(r.getReservationUserCount())
                    .diningTableNumber(r.getDiningTable().getDiningTableNumber())
                    .status(r.getStatus())
                    .storeImage(imageUrls.get(0))
                    .build();
            rDtoList.add(uDto);
        }
        return rDtoList;

    }

    /**
     * 가게별로 예약된 현황 조회
     */
    public List<ReservationByStoreResponseDto> getReservationByStore(Long storeId) {
        Store store = storeRepository.findByStoreId(storeId);
        List<Reservation> rList = reservationRepository.findAllByStore(store);
        List<ReservationByStoreResponseDto> rDtoList = new ArrayList<>();
        for (Reservation r : rList) {
            ReservationByStoreResponseDto sDto = ReservationByStoreResponseDto.builder()
                    .reservationId(r.getReservationId())
                    .nickname(r.getUser().getNickname())
                    .reservationDate(r.getReservationDate())
                    .reservationTime(r.getReservationTime())
                    .reservationUserCount(r.getReservationUserCount())
                    .diningTableNumber(r.getDiningTable().getDiningTableNumber())
                    .status(r.getStatus())
                    .build();
            rDtoList.add(sDto);
        }
        return rDtoList;
    }


    /**
     * 날짜(예: "2021-12-05") + 가게로 예약된 현황 조회(사업자용)
     *
     * @param : 조회 시작 일자 : "2021-12-05 00:00"
     * @param : 조회 마지막 일자 : "2021-12-05 23:59"
     *          between 쿼리로 조회하기 위함
     */
    public List<ReservationByStoreResponseDto> getReservationByDateAndStore(String search_date, Long storeId) {
        Store store = storeRepository.findByStoreId(storeId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate searchDate = LocalDate.parse(search_date, formatter);

        List<Reservation> rList = reservationRepository.findAllByDateAndStore(searchDate, store);
        List<ReservationByStoreResponseDto> rDtoList = new ArrayList<>();
        for (Reservation r : rList) {
            ReservationByStoreResponseDto sDto = ReservationByStoreResponseDto.builder()
                    .reservationId(r.getReservationId())
                    .nickname(r.getUser().getNickname())
                    .reservationDate(r.getReservationDate())
                    .reservationTime(r.getReservationTime())
                    .reservationUserCount(r.getReservationUserCount())
                    .diningTableNumber(r.getDiningTable().getDiningTableNumber())
                    .status(r.getStatus())
                    .build();
            rDtoList.add(sDto);
        }
        return rDtoList;
    }

    /**
     * 날짜(예: "2021-12-05") + 사용자로 예약된 현황 조회(사용자용)
     *
     * @param : 조회 시작 일자 : "2021-12-05 00:00"
     * @param : 조회 마지막 일자 : "2021-12-05 23:59"
     *          between 쿼리로 조회하기 위함
     */
    public List<ReservationByUserResponseDto> getReservationByDateAndUser(String search_date, Long userId) {
        User user = userRepository.findByUserId(userId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate searchDate = LocalDate.parse(search_date, formatter);

        List<Reservation> rList = reservationRepository.findAllByDateAndUser(searchDate, user);
        List<ReservationByUserResponseDto> rDtoList = new ArrayList<>();
        for (Reservation r : rList) {
            ReservationByUserResponseDto uDto = ReservationByUserResponseDto.builder()
                    .reservationId(r.getReservationId())
                    .storeName(r.getStore().getStoreName())
                    .reservationDate(r.getReservationDate())
                    .reservationTime(r.getReservationTime())
                    .reservationUserCount(r.getReservationUserCount())
                    .diningTableNumber(r.getDiningTable().getDiningTableNumber())
                    .status(r.getStatus())
                    .build();
            rDtoList.add(uDto);
        }
        return rDtoList;
    }

    /**
     * 가게별 예약 가능 날짜 리스트 출력
     */
    public List<LocalDateTime> getAvailableReservationDatesByStore(Long storeId) {
        Store store = storeRepository.findByStoreId(storeId);
        String[] storeDates = store.getStoreReservationAvailableDates().split(" ");

        List<LocalDateTime> availableDateList = new ArrayList<>();
        for (String date : storeDates) {
            // String to LocalDateTime
            log.info("변환전 stringdate : {}", date);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            LocalDateTime realDate = LocalDateTime.parse(date + " 00:00:00.000", formatter);
            log.info("변환된 localdate : {}", realDate);
            availableDateList.add(realDate);
        }
        return availableDateList;
    }

    /**
     * 가게별 선택된 날짜에 예약 가능한 시간 출력
     * - 예약 할 수 있는 시간 대 출력
     */
    public List<AvailableReservationResponseDto> getAvailableReservationByDateAndStore(String search_date, Long storeId) {
        Store store = storeRepository.findByStoreId(storeId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate searchDate = LocalDate.parse(search_date, formatter);

//        List<Status> inVailableStatusList = new ArrayList<>();
//        inVailableStatusList.add(Status.STANDBY);
//        inVailableStatusList.add(Status.ACTIVE);
        // 1. 해당 날짜에 예약이 진행중(ACTIVE)&완료되지않은(!is_deleted) 현황리스틑 조회
        List<Reservation> rList = reservationRepository.findAllByDateAndStoreAndStatus(searchDate, store, Status.ACTIVE);
        log.info("< {} > 예약 현황 : {}", search_date, rList);

        // 2. 가게 운영시간 & 테이블 현황 조회
        String[] storeAvailableOperationHours = storeService.getAvailableOperationHours(store.getStoreId()).split(" ");
        List<String> operations = new ArrayList<>();
//        List<String> realAvailableOperations = new ArrayList<>();
        for (String o : storeAvailableOperationHours) {
            operations.add(o);
        }
//        System.out.println("가게운영시간: "+operations);
        // 가게 테이블들
        List<DiningTable> diningTables = diningTableRepository.findByStore(store);

        // 3. 운영 시간과 예약된 날짜를 통해 예약 가능한 시간 출력
        List<AvailableReservationResponseDto> aList = new ArrayList<>();
        for (String oTime : operations) {
            List<DiningTableResponseDto> dTableList = new ArrayList<>();
            for (DiningTable diningTable : diningTables) {
                Boolean isOk = true;
                for (Reservation reservation : rList) {
                    // LocalDate -> String
                    String reservedTime = reservation.getReservationTime();
//                    System.out.println("예약된 시간 : "+reservedTime);
                    DiningTable reservedDiningTable = reservation.getDiningTable();
                    if (oTime.equals(reservedTime) && diningTable.getDiningTableNumber().equals(reservedDiningTable.getDiningTableNumber())) {
                        isOk = false;
                    }
                }
                // 예약 가능한 시간&테이블
                if (isOk) {
//                    System.out.println(">> 예약 가능!! : "+oTime+" | "+diningTable.getDiningTableNumber());
                    DiningTableResponseDto diningTableResponseDto = DiningTableResponseDto.builder()
                            .diningTableNumber(diningTable.getDiningTableNumber())
                            .diningTableCapacity(diningTable.getDiningTableCapacity())
                            .diningTableIsAvailable(diningTable.getDiningTableIsAvailable())
                            .diningTableType(diningTable.getDiningTableType())
                            .diningTableX(diningTable.getDiningTableX())
                            .diningTableY(diningTable.getDiningTableY())
                            .width(diningTable.getDiningTableWidth())
                            .height(diningTable.getDiningTableHeight())
                            .build();
                    dTableList.add(diningTableResponseDto);
                }
            }
            // 가능한 테이블이 있다면 해당 시간 추가
            if (!dTableList.isEmpty()) {
                log.info("{} => 가능한 테이블 존재", oTime);
                aList.add(AvailableReservationResponseDto.builder()
                        .storeId(store.getStoreId())
                        .time(oTime)
                        .diningTableResponseDtoList(dTableList)
                        .build());
            }
        }

        return aList;
    }

    public List<AvailableReservationResponseDto> getInAvailableReservation(String search_date, Long storeId) {
        Store store = storeRepository.findByStoreId(storeId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate searchDate = LocalDate.parse(search_date, formatter);

//        List<Status> inVailableStatusList = new ArrayList<>();
//        inVailableStatusList.add(Status.STANDBY);
//        inVailableStatusList.add(Status.ACTIVE);
        // 1. 해당 날짜에 예약이 진행중(ACTIVE)&완료되지않은(!is_deleted) 현황리스틑 조회
        List<Reservation> rList = reservationRepository.findAllByDateAndStoreAndStatus(searchDate, store, Status.ACTIVE);
        log.info("< {} > 예약 현황 : {}", search_date, rList);

        // 2. 가게 운영시간 & 테이블 현황 조회
        String[] storeAvailableOperationHours = storeService.getAvailableOperationHours(store.getStoreId()).split(" ");
        List<String> operations = new ArrayList<>();
//        List<String> realAvailableOperations = new ArrayList<>();
        for (String o : storeAvailableOperationHours) {
            operations.add(o);
        }
        // 가게 테이블들
        List<DiningTable> diningTables = diningTableRepository.findByStore(store);

        // 3. 운영 시간과 예약된 날짜를 통해 예약 가능한 시간 출력
        List<AvailableReservationResponseDto> aList = new ArrayList<>();
        for (String oTime : operations) {
            List<DiningTableResponseDto> dTableList = new ArrayList<>();
            for (DiningTable diningTable : diningTables) {
                Boolean isOk = true;
                Status status = Status.ACTIVE;
                for (Reservation reservation : rList) {
                    // LocalDate -> String
                    String reservedTime = reservation.getReservationTime();
//                    System.out.println("예약된 시간 : "+reservedTime);
                    DiningTable reservedDiningTable = reservation.getDiningTable();
                    if (oTime.equals(reservedTime) && diningTable.getDiningTableNumber().equals(reservedDiningTable.getDiningTableNumber())) {
                        isOk = false;
                        status = Status.INACTIVE;
                    }
                }
                // 예약 가능한 시간&테이블
                DiningTableResponseDto diningTableResponseDto = DiningTableResponseDto.builder()
                        .diningTableNumber(diningTable.getDiningTableNumber())
                        .diningTableCapacity(diningTable.getDiningTableCapacity())
                        .diningTableIsAvailable(diningTable.getDiningTableIsAvailable())
                        .diningTableType(diningTable.getDiningTableType())
                        .diningTableX(diningTable.getDiningTableX())
                        .diningTableY(diningTable.getDiningTableY())
                        .width(diningTable.getDiningTableWidth())
                        .height(diningTable.getDiningTableHeight())
                        .status(status)
                        .build();
                dTableList.add(diningTableResponseDto);
//                if (isOk) {
////                    System.out.println(">> 예약 가능!! : "+oTime+" | "+diningTable.getDiningTableNumber());
//                }
            }
            // 가능한 테이블이 있다면 해당 시간 추가
            if (!dTableList.isEmpty()) {
                log.info("{} => 가능한 테이블 존재", oTime);
                aList.add(AvailableReservationResponseDto.builder()
                        .storeId(store.getStoreId())
                        .time(oTime)
                        .diningTableResponseDtoList(dTableList)
                        .build());
            }
        }
        return aList;
    }


    /**
     * 예약 등록하기
     *
     * @param : ReservationRequestDto(예약자, 가게, 원하는 날짜와 시간, 테이블 번호, 인원 수)
     * @return : 성공적으로 등록한 예약
     * redisson 사용으로 동시성 제어
     */
    public void registReservation(ReservationRequestDto reservationRequestDto, Long userId) {
        User regUser = userRepository.findByUserId(userId);
        Store regStore = storeRepository.findByStoreId(reservationRequestDto.getStoreId());
        DiningTable regDiningTable = diningTableRepository.findByStoreAndDiningTableNumber(regStore, reservationRequestDto.getDiningTableNumber());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate regDate = LocalDate.parse(reservationRequestDto.getReservationDate(), formatter);
        String regTime = reservationRequestDto.getReservationTime();

        System.out.println("예약 일자 >> " + regDate);
        System.out.println("예약 시간 >> " + regTime);


        String reqDate = reservationRequestDto.getReservationDate();
        String reqTime = reservationRequestDto.getReservationTime();
        Integer reqTableNum = reservationRequestDto.getDiningTableNumber();
        String code = regStore.getStoreId()
                + "/" + reqDate
                + "/" + reqTime
                + "/" + reqTableNum;


//        // 수량 1로 설정하기
//        setUsableReservation(key, quantity);


        String key = keyResolver(code);         // RESERVATION : {상점Id}/{날짜}/{시간}/{테이블번호}
        // 락 이름 설정
        final String lockName = key + ":lock";  // lockname => RESERVATION : {상점Id}/{날짜}/{시간}/{테이블번호}:lock
        // 락 생성
        final RLock lock = redissonClient.getLock(lockName);
        final String threadName = Thread.currentThread().getName();

        try {
            // 락 획득 요청
            /**
             * tryLock(waitTime, lessTime)
             * - waitTime: 락을 시도할 시간 (해당 시간동안 락을 획득하지 못하면 false)
             * - lessTime: 락을 점유하는 시간 (락 획득 성공 후, lessTime 만큼 점유하고 락 해제)
             * */
            boolean isLock = lock.tryLock(5, 1, TimeUnit.SECONDS);
            if (!isLock) {
                //락 획득 실패 시 예외 처리
//                throw new Exception( ... );
                return;
            }

            // 현재 수량 불러오기
            // 사용 가능한 수량 얻음
            final int availableQuantity = usableReservation(key);
            log.info("현재 가능 수량 : {}", availableQuantity);
            if (availableQuantity <= EMPTY) {
                log.info("<<요청 예약>>");
                log.info("storeId : {} / reqDate: {} / reqTime : {} / reqTableNum : {}", regStore.getStoreId(), regDate, reqTime, reqTableNum);
                log.info("threadName : {} / 해당 예약 불가 - 대기하세요.", threadName);

                throw new EntityNotFoundException(reqDate+" => 예약 불가");
//                return;
            }

            log.info("threadName : {} / 사용 가능 예약 : {}개", threadName, availableQuantity);
            // 락 걸때, 사용 가능한 해당 키의 사용가능한 수량을 1으로 세팅
            setUsableReservation(key, availableQuantity - 1);

            //======================== 예약 등록 로직 진행 ========================
            Reservation reservation = Reservation.builder()
                    .store(regStore)
                    .user(regUser)
                    .diningTable(regDiningTable)
                    .reservationDate(regDate)
                    .reservationTime(regTime)
                    .reservationUserCount(reservationRequestDto.getReservationUserCount())
                    .status(Status.ACTIVE)
                    .build();
            reservationRepository.save(reservation);

            log.info(">>> 예약 완료 이메일 전송");
            emailConfig.sendMail(regUser.getEmail(),
                    regUser.getNickname() + "님의 예약이 완료되었습니다.",
                    regUser.getNickname() + "님의 " + regDate + "/" + regTime +
                            " " + regDiningTable.getDiningTableNumber() + " 번 테이블의 예약이 완료되었습니다.");
//            log.info(">>> 예약 패스 존재 여부 : {}", reservationRequestDto.getZpassId());
//            if(reservationRequestDto.getZpassId() != null) {
//                log.info("#### 예약 패스 사용 ####");
//                zPassService.useZPass(reservationRequestDto.getZpassId());
//            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lock != null && lock.isLocked()) {
                // 락 해제
                lock.unlock();
            }
        }
    }

    /**
     * ZPASS 유효성 검사
     * - 요청 유저와 소유 유저 동일성 검사
     *
     * @param : zpassId, userId
     * @return : true or false
     */
    public Boolean validateZpass(Long zpassId, Long userId) {
        ZPass zpass = zPassRepository.findByZpassId(zpassId);
        User zpassOwner = zpass.getUser();
        if (zpassOwner != null && zpassOwner.getUserId().equals(userId)) {
            log.info("zpass {}의 유효한 사용자 입니다.", zpassId);
            return true;
        }
        log.info("zpass {}의 유효하지 않은 사용자 입니다.", zpassId);
        return false;
    }


    /**
     * 예약 등록하기 with ZPASS
     *
     * @param : ReservationRequestDto(예약자, 가게, 원하는 날짜와 시간, 테이블 번호, 인원 수) / zpassId
     * @return : 성공적으로 등록한 예약
     * redisson 사용으로 동시성 제어
     */
    public void registReservationWithZPass(Long zpassId, Long userId, ReservationRequestDto reservationRequestDto) {
        registReservation(reservationRequestDto, userId);
        // 예약 패스 사용
        zPassService.useZPass(zpassId);
    }

    /**
     * 예약 취소시 요청 유저 유효성 검사
     */
    public Boolean validateUser(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findByReservationId(reservationId);
        User user = reservation.getUser();
        if (user.getUserId().equals(userId)) {
            return true;
        }
        return false;
    }

    /**
     * 예약 취소하기
     */
    public Reservation reomveReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).get();

        if (reservation == null) {
            throw new EntityNotFoundException("Reservation not found with id: " + reservationId);
        }

        // 예약 취소시, 삭제 처리
        reservation.setStatus(Status.CANCEL);
//        reservation.setDeleted(true);
//        log.info(">>> 예약 취소 처리 완료 in DB");
        // redis에서 삭제
        Store regStore = reservation.getStore();
        DiningTable diningTable = reservation.getDiningTable();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String reqDate = reservation.getReservationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String reqTime = reservation.getReservationTime();
        Integer reqTableNum = diningTable.getDiningTableNumber();
        String code = regStore.getStoreId()
                + "/" + reqDate
                + "/" + reqTime
                + "/" + reqTableNum;

        String key = keyResolver(code);
        log.info(">> 삭제할 key : {}", key);
        redissonClient.getKeys().delete(key);

        return reservation;
    }

    /**
     * 예약 종료 요청 유효성 확인
     * - 요청한 사람이 해당 예약의 사업자인지 확인
     */
    public Boolean validateStoreOwner(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findByReservationId(reservationId);
        Store store = reservation.getStore();
        if (store.getUser().getUserId().equals(userId)) {
            log.info("요청 사업자와 예약 사업자 일치");
            return true;
        }
        return false;
    }

    /**
     * 예약 종료 처리 (방문 완료)
     */
    public Reservation expireReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).get();

        if (reservation == null) {
            throw new EntityNotFoundException("Reservation not found with id: " + reservationId);
        }

        reservation.setStatus(Status.INACTIVE);
//        reservation.setDeleted(true);
        // redis에서 삭제
        Store regStore = reservation.getStore();
        DiningTable diningTable = reservation.getDiningTable();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String reqDate = reservation.getReservationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String reqTime = reservation.getReservationTime();
        Integer reqTableNum = diningTable.getDiningTableNumber();
        String code = regStore.getStoreId()
                + "/" + reqDate
                + "/" + reqTime
                + "/" + reqTableNum;

        String key = keyResolver(code);
        log.info(">> 삭제할 key : {}", key);
        redissonClient.getKeys().delete(key);

        return reservation;
    }

    /**
     * 예약 확정 처리
     * STANDBY 상태를 ACTIVE로 업데이트
     */
    public Reservation allowReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).get();

        if (reservation == null) {
            throw new EntityNotFoundException("Reservation not found with id: " + reservationId);
        }

        reservation.setStatus(Status.ACTIVE);
        return reservation;
    }


    public void setUsableReservation(String key, int quantity) {
        log.info("setUsableReservation >> key : {} | quantity : {}", key, quantity);
        redissonClient.getBucket(key).set(quantity);
    }

    public int usableReservation(String key) {
        try {
            int result = (int) redissonClient.getBucket(key).get();
            return result;
        } catch (NullPointerException e) {
            log.info("<< 첫 시도 예약 >> key 등록 후 반환");
            setUsableReservation(key, 1);
            return (int) redissonClient.getBucket(key).get();
        }
    }


}
