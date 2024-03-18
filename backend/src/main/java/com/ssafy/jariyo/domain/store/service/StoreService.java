package com.ssafy.jariyo.domain.store.service;

import com.ssafy.jariyo.domain.board.dto.BoardResponseGetDto;
import com.ssafy.jariyo.domain.playroom.entity.Playroom;
import com.ssafy.jariyo.domain.playroom.repository.PlayroomRepository;
import com.ssafy.jariyo.domain.playroom.service.PlayroomService;
import com.ssafy.jariyo.domain.reservation.dto.request.DiningTableRequestDto;
import com.ssafy.jariyo.domain.reservation.dto.response.DiningTableResponseDto;
import com.ssafy.jariyo.domain.reservation.entity.DiningTable;
import com.ssafy.jariyo.domain.reservation.repository.DiningTableRepository;
import com.ssafy.jariyo.domain.s3image.entity.BoardS3Image;
import com.ssafy.jariyo.domain.s3image.entity.StoreS3Image;
import com.ssafy.jariyo.domain.s3image.repository.StoreS3ImageRepository;
import com.ssafy.jariyo.domain.s3image.service.S3ImageService;
import com.ssafy.jariyo.domain.store.dto.request.OpenApiRequestDto;
import com.ssafy.jariyo.domain.store.dto.request.StoreRequestInfoDto;
import com.ssafy.jariyo.domain.store.dto.request.StoreRequestRegistDto;
import com.ssafy.jariyo.domain.store.dto.request.StoreRequestReservationInfoDto;
import com.ssafy.jariyo.domain.store.dto.response.StoreResponseAvailableReservationDto;
import com.ssafy.jariyo.domain.store.dto.response.StoreResponseDefaultDto;
import com.ssafy.jariyo.domain.store.dto.response.StoreResponseGetDto;
import com.ssafy.jariyo.domain.store.entity.Store;
import com.ssafy.jariyo.domain.store.repository.StoreRepository;

import com.ssafy.jariyo.domain.user.dto.UserResponseDto;
import com.ssafy.jariyo.domain.user.entity.Role;
import com.ssafy.jariyo.domain.user.entity.User;
import com.ssafy.jariyo.domain.user.repository.UserRepository;
import com.ssafy.jariyo.global.entity.Status;
import com.ssafy.jariyo.global.jwt.service.JwtService;
import jakarta.activation.DataContentHandler;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StoreService {
    private final JwtService jwtService;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final DiningTableRepository diningTableRepository;
    private final S3ImageService s3ImageService;
    private final StoreS3ImageRepository storeS3ImageRepository;
    private final PlayroomService playroomService;

    @Value("${openApi.serviceKey}")
    private String serviceKey;

    @Value("${openApi.callBackUrl}")
    private String callBackUrl;

    @Value("${openApi.dataType}")
    private String dataType;

    /**
     * 상졈 등룍 1 - 사업자 등록
     */

    public List<String> addStore(StoreRequestRegistDto storeRegistDto, Long userId, String atk, MultipartFile storeBusinessFile, MultipartFile[] images, MultipartFile menu) {

        //JWT 토큰을 이용해서 유저 정보 얻어오고 이걸 토대로 상점 추가
        User user = userRepository.findByUserId(userId);

        String fileUrl = s3ImageService.uploadEmployFile(storeBusinessFile);

        String menuImage = s3ImageService.uploadEmployFile(menu);

        Store regStore = Store.builder()
                .user(user)
                .storeName(storeRegistDto.getStoreName())
                .storeAddress(storeRegistDto.getStoreAddress())
                .storeBusinessNumber(storeRegistDto.getStoreBusinessNumber())
                .storePositionX(storeRegistDto.getStorePositionX())
                .storePositionY(storeRegistDto.getStorePositionY())
                .storeReservationStatus(Status.INACTIVE)
                .storeFollowCount(0)
                .storeIsWaiting(false)
                .storeBusinessFile(fileUrl)
                .storeMenuImage(menuImage)
                .build();

        s3ImageService.uploadFilesOnStore(regStore, images[0]);

        storeRepository.save(regStore);

        // Playroom 생성
        playroomService.createPlayroom(regStore);

        // 상점 등록한 사용자를 사업자로 변경
        log.info("========= 상점 등록 사용자 -> 사업자(OWNER) 변경 ==========");
        user.setRole(Role.OWNER);

        // 엑세스 토큰 & 리프레시 토큰 새발급 진행
        log.info(">>> 상점 등록 유저 ATK / RTK 재발급 진행");
        List<String> newTokens = new ArrayList<>();
//        String email = jwtService.extractEmail(atk).get();
        // 이전 atk 로그아웃 처리
        log.info("========= 이전 atk 로그아웃 처리 진행 =========");
        jwtService.expireAccessToken(atk);
        // 엑세스 토큰 재발급
        String reIssuedATK = jwtService.createAccessToken(user, user.getEmail());
        newTokens.add(reIssuedATK);
        // 리프레시 토큰 재발급
        String reIssuedRTK = jwtService.createRefreshToken();
        Long refreshExpiration = jwtService.extractExpiration(reIssuedRTK).get().getTime();
        jwtService.updateRefreshToken(user.getEmail(), reIssuedRTK, refreshExpiration);
        newTokens.add(reIssuedRTK);

        return newTokens;
    }

    /**
     * 상점 등록 1(사업자 등록) "수정"
     */
    public void updateStore(StoreRequestRegistDto storeRegistDto, Long storeId, Long userId, MultipartFile storeBusinessFile, MultipartFile[] images, MultipartFile menu) {
        User user = userRepository.findByUserId(userId);
        Store modStore = storeRepository.findByStoreId(storeId);
        String fileUrl = s3ImageService.uploadEmployFile(storeBusinessFile);

        user.setName(storeRegistDto.getOwnerName());
        modStore.setStoreName(storeRegistDto.getStoreName());
        modStore.setStoreAddress(storeRegistDto.getStoreAddress());
        modStore.setStoreBusinessNumber(storeRegistDto.getStoreBusinessNumber());
        modStore.setStorePositionX(storeRegistDto.getStorePositionX());
        modStore.setStorePositionY(storeRegistDto.getStorePositionY());
        modStore.setStoreBusinessFile(fileUrl);

        s3ImageService.uploadFilesOnStore(modStore, images[0]);
    }


    public StoreResponseDefaultDto getStoreById(Long storeId) {
        Store store = storeRepository.findByStoreId(storeId);
        User user = store.getUser();
        String ownerName = user.getName();

        List<StoreS3Image> images = storeS3ImageRepository.findAllByStore(store);
        List<String> imageUrls = images.stream()
                .map(image -> image.getS3Image().getFileUrl()) // S3Image 엔티티에서 파일 URL 추출
                .collect(Collectors.toList());

        StoreResponseDefaultDto defaultDto = StoreResponseDefaultDto.builder()
                .storeId(storeId)
                .ownerName(ownerName)
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
                .storeMenu(store.getStoreMenu())
                .build();

        return defaultDto;
    }

    // [사업자] 내 상점 보기
    public StoreResponseDefaultDto getMyStoreByOwner(Long userId) {
        User user = userRepository.findByUserId(userId);
        Store store = storeRepository.findByUser(user);
        String ownerName = user.getName();

        List<StoreS3Image> images = storeS3ImageRepository.findAllByStore(store);
        List<String> imageUrls = images.stream()
                .map(image -> image.getS3Image().getFileUrl()) // S3Image 엔티티에서 파일 URL 추출
                .collect(Collectors.toList());

        StoreResponseDefaultDto defaultDto = StoreResponseDefaultDto.builder()
                .storeId(store.getStoreId())
                .ownerName(ownerName)
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
                .storeMenu(store.getStoreMenu())
                .build();

        return defaultDto;
    }

    public StoreResponseGetDto getStoreByUser(Long userId) {
        User user = userRepository.findByUserId(userId);
        Store store = storeRepository.findByUser(user);
        return toDto(store);
    }

    public List<StoreResponseGetDto> filterStores(String location, Pageable pageable) {
        Page<Store> stores = storeRepository.findAllByStoreAddressContaining(location, pageable);

        return toDtoList(stores.getContent());
    }

    public List<StoreResponseGetDto> searchStoresWithMultipleKeywords(List<String> keywords) {
        Specification<Store> spec = StoreSpecifications.hasKeywords(keywords);
        Sort sort = Sort.unsorted();
        List<Store> stores = storeRepository.findAll(spec, sort);

        return stores.stream().map(store -> {
            StoreResponseGetDto dto = toDto(store);

            List<StoreS3Image> images = storeS3ImageRepository.findAllByStore(store);
            dto.setStoreImage(images.get(0).getS3Image().getFileUrl());

            return dto;
        }).collect(Collectors.toList());
    }


    public StoreResponseGetDto searchStoresByAddress(String address) {
        Store store = storeRepository.findByStoreAddress(address);
        if (store == null) {
            return null;
        }

        return toDto(store);
    }


    public static class StoreSpecifications {
        public static Specification<Store> hasKeywords(List<String> keywords) {
            return (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                for (String keyword : keywords) {
                    Predicate namePredicate = cb.like(root.get("storeName"), "%" + keyword + "%");
                    Predicate addressPredicate = cb.like(root.get("storeAddress"), "%" + keyword + "%");
                    predicates.add(cb.or(namePredicate, addressPredicate));
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            };
        }
    }

    /**
     * 상점의 테이블 정보 조회
     */
    public List<DiningTableResponseDto> searchStoreDiningTables(Long storeId) {
        Store store = storeRepository.findByStoreId(storeId);
        if (store != null) {
            List<DiningTable> diningTables = diningTableRepository.findByStore(store);
            List<DiningTableResponseDto> responseDtoList = new ArrayList<>();

            for (DiningTable diningTable : diningTables) {
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
                responseDtoList.add(diningTableResponseDto);
            }
            return responseDtoList;
        } else {
            throw new EntityNotFoundException("Store with ID " + storeId + " not found");
        }
    }

    /**
     * 상점 상세 정보 수정 (상세설명, 운영시간, 영업일 등등)
     */
    public void updateStoreDetailInfo(Long storeId, StoreRequestInfoDto storeRequestInfoDto) {
        Store store = storeRepository.findById(storeId).orElse(null);

        if (store != null) {
            store.setStoreDetail(storeRequestInfoDto.getStoreDetail());
            store.setStoreOperationHours(storeRequestInfoDto.getStoreOperationHours());
            store.setStoreOperationDates(storeRequestInfoDto.getStoreOperationDates());
            store.setStorePhone(storeRequestInfoDto.getStorePhone());
            store.setStoreMenu(storeRequestInfoDto.getStoreMenu());
        } else {
            throw new EntityNotFoundException("Store with ID " + storeId + " not found");
        }
    }

    /**
     * 상점 예약 정보 수정 (예약일, 예약 가능 시간 등등)
     */
    public void updateStoreReservationInfo(Long storeId, StoreRequestReservationInfoDto reservationInfoDto) {
        Store store = storeRepository.findByStoreId(storeId);
        if (store != null) {
            // String to LocalDate
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//            LocalDate searchDate = LocalDate.parse(search_date, formatter);

            // Dates 저장 (LocalDateTime -> String)
            StringBuilder resultDates = new StringBuilder();
            for (LocalDateTime reservationDates : reservationInfoDto.getStoreReservationAvailableDates()) {
                String strReservationDates = reservationDates.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                resultDates.append(strReservationDates);
                resultDates.append(" ");
            }
            resultDates.deleteCharAt(resultDates.length() - 1);

            // Hours 저장
            StringBuilder resultHours = new StringBuilder();
            for (String reservationHours : reservationInfoDto.getStoreReservationAvailableHours()) {
                resultHours.append(reservationHours);
                resultHours.append(" ");
            }
            resultHours.deleteCharAt(resultHours.length() - 1);

            store.setStoreReservationAvailableDates(resultDates.toString());
            store.setStoreReservationAvailableHours(resultHours.toString());
            store.setStoreReservationStatus(Status.ACTIVE);
        } else {
            throw new EntityNotFoundException("Store with ID " + storeId + " not found");
        }

    }

    /**
     * 상점 예약 정보 조회
     */
    public StoreResponseAvailableReservationDto searchStoreReservationInfo(Long storeId) {
        Store store = storeRepository.findByStoreId(storeId);
        if (store != null) {
            List<LocalDateTime> availableDateList = new ArrayList<>();
            List<String> availableHourList = new ArrayList<>();

            // 가능 날짜 리스트 생성
            String[] storeDates = store.getStoreReservationAvailableDates().split(" ");
            for (String date : storeDates) {
                // String to LocalDateTime
                log.info("변환전 stringdate : {}", date);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                LocalDateTime realDate = LocalDateTime.parse(date + " 00:00:00.000", formatter);
                log.info("변환된 localdate : {}", realDate);
                availableDateList.add(realDate);
            }
            // 가능 시간 리스트 생성
            String[] storeHours = store.getStoreReservationAvailableHours().split(" ");
            for (String hour : storeHours) {
                availableHourList.add(hour);
            }

            StoreResponseAvailableReservationDto availableReservationDto = StoreResponseAvailableReservationDto.builder()
                    .storeReservationAvailableDates(availableDateList)
                    .storeReservationAvailableHours(availableHourList)
                    .build();

            return availableReservationDto;
        } else {
            throw new EntityNotFoundException("Store with ID " + storeId + " not found");
        }
    }


    /**
     * 상점 테이블 생성
     */
    public void addStoreDiningTables(Long storeId, List<DiningTableRequestDto> diningTables) {
        Store store = storeRepository.findByStoreId(storeId);
        // 해당 상점의 원래 있던 테이블들 모두 삭제
        diningTableRepository.deleteByStore(store);
        if (store != null) {
            for (DiningTableRequestDto diningTableRequestDto : diningTables) {
                DiningTable diningTable = DiningTable.builder()
                        .store(store)
                        .diningTableNumber(diningTableRequestDto.getDiningTableNumber())
                        .diningTableCapacity(diningTableRequestDto.getDiningTableCapacity())
                        .diningTableType(diningTableRequestDto.getDiningTableType())
                        .diningTableIsAvailable(diningTableRequestDto.getDiningTableIsAvailable())
                        .diningTableX(diningTableRequestDto.getDiningTableX())
                        .diningTableY(diningTableRequestDto.getDiningTableY())
                        .diningTableWidth(diningTableRequestDto.getWidth())
                        .diningTableHeight(diningTableRequestDto.getHeight())
                        .build();
                diningTableRepository.save(diningTable);
            }
        } else {
            throw new EntityNotFoundException("Store with ID " + storeId + " not found");
        }
    }

    /**
     * 상점 테이블 수정
     */
    public void updateStoreDiningTable(Long storeId, List<DiningTableRequestDto> diningTableRequestDtoList) {
        Store store = storeRepository.findByStoreId(storeId);
        if (store != null) {
            for (DiningTableRequestDto tableRequestDto : diningTableRequestDtoList) {
                Integer dNum = tableRequestDto.getDiningTableNumber();
                DiningTable diningTable = diningTableRepository.findByStoreAndDiningTableNumber(store, dNum);
                // 다이닝테이블 수정
                diningTable.updateDiningTable(tableRequestDto);
            }

        } else {
            throw new EntityNotFoundException("Store with ID " + storeId + " not found");
        }
    }

    public void deleteStore(Long storeId) {
        Store store = storeRepository.findByStoreId(storeId);
        store.setDeleted(true); // 유저를 삭제 상태로 표시
    }

    /**
     * 상점의 예약 가능한 시간 출력
     */
    public String getAvailableOperationHours(Long storeId) {
        Store store = storeRepository.findByStoreId(storeId);
        if (store != null) {
            return store.getStoreReservationAvailableHours();
        } else {
            throw new EntityNotFoundException("Store with ID " + storeId + " not found");
        }
    }

    public Boolean checkBusinessNumber(String businessNum) {
        log.info("사업자 번호 체크 서비스 진입");
        log.info("sk > " + serviceKey);
        URI uri = UriComponentsBuilder
                .fromUriString(callBackUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("dataType", dataType)
                .encode()
                .build()
                .toUri();
        log.info("공공 api 요청 uri>> " + uri.toString());

        List<String> bList = new ArrayList<>();
        bList.add(businessNum);
        OpenApiRequestDto openApiRequestDto = new OpenApiRequestDto();
        openApiRequestDto.setB_no(bList);
        System.out.println(openApiRequestDto.toString());

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(uri, openApiRequestDto, Map.class);

        log.info("응답 데이터 ++> " + response.getBody());
        Map resBody = response.getBody();
        if (resBody != null) {
            if (resBody.get("status_code").equals("OK")) {
                List<Map> datas = (List<Map>) resBody.get("data");
                log.info("data : " + resBody.get("data"));
                log.info("b_stt : " + datas.get(0).get("b_stt"));
                if (!datas.get(0).get("b_stt").equals("")) {
                    log.info("인증된 사업자 : {}", businessNum);
                    return true;
                } else {
                    log.info("인증되지 않은 사업자 : {}", businessNum);
                }
//                log.info("b_sst : "+ (() resBody.get("data"))[0]);
            } else {
                throw new EntityNotFoundException("BusinessNumber OpenApi Test Failed");
            }
        }
        return false;
    }

//    /* URLConnection 을 전달받아 연결정보 설정 후 연결, 연결 후 수신한 InputStream 반환 */
//    private InputStream getNetworkConnection(HttpURLConnection urlConnection, JSONObject json) throws IOException {
//        urlConnection.setConnectTimeout(3000);
//        urlConnection.setReadTimeout(3000);
//        urlConnection.setRequestMethod("POST");
//        urlConnection.setDoInput(true);
//        urlConnection.setDoOutput(true);
//
//        // POST 요청에 body 담기
//        OutputStream os = urlConnection.getOutputStream();
//        OutputStreamWriter streamWriter = new OutputStreamWriter(os, "UTF-8");
////        os.write(body.getBytes("utf-8"));
//        streamWriter.write(json.toJSONString());
//        os.flush();
//        os.close();
//
//
//        if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
//            throw new IOException("HTTP error code : " + urlConnection.getResponseCode());
//        }
//
//        return urlConnection.getInputStream();
//    }


    public static List<StoreResponseGetDto> toDtoList(List<Store> stores) {
        if (stores == null) {
            return null;
        }

        return stores.stream()
                .map(StoreService::toDto)
                .collect(Collectors.toList());
    }

    public static StoreResponseGetDto toDto(Store store) {
        if (store == null) {
            return null;
        }

        StoreResponseGetDto dto = new StoreResponseGetDto();
        dto.setStoreId(store.getStoreId());
        dto.setStoreName(store.getStoreName());
        dto.setStorePhone(store.getStorePhone());
        dto.setStoreDetail(store.getStoreDetail());
        dto.setStoreAddress(store.getStoreAddress());
        dto.setStoreIsWaiting(store.getStoreIsWaiting());
        dto.setStoreFollowCount(store.getStoreFollowCount());
        dto.setStoreOperationHours(store.getStoreOperationHours());
        dto.setStoreReservationStatus(store.getStoreReservationStatus().name());
        dto.setStoreReservationAvailableDates(store.getStoreReservationAvailableDates());
        dto.setStorePositionX(store.getStorePositionX());
        dto.setStorePositionY(store.getStorePositionY());

        return dto;
    }


}