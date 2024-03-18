package com.ssafy.jariyo.domain.reservation.service;

import com.ssafy.jariyo.domain.reservation.dto.request.ReservationRequestDto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@SpringBootTest
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;
    private ReservationRequestDto reservationRequestDto;
    private String reservationKey;

    @BeforeEach
    void 예약_세팅() {

        final int quantity = 1;
        String reqDate = "2024-03-09";
        String reqTime = "12:30";
        Integer reqTableNum = 1;
        String code = 36L
                +"/"+reqDate
                +"/"+reqTime
                +"/"+reqTableNum;

        // 키 존재 여부 확인
        this.reservationKey = reservationService.keyResolver(code);
        reservationService.usableReservation(this.reservationKey);

        // 예약 요청 생성
        this.reservationRequestDto = ReservationRequestDto.builder()
                .storeId(36L)
                .reservationDate(reqDate)
                .reservationTime(reqTime)
                .diningTableNumber(2)
                .reservationUserCount(2)
                .build();

//        reservationService.setUsableReservation(this.reservationKey, quantity);

//        final String name = "name";
//        final String code = "Q2LJ5GZ52OUNW9BV";
//        final int quantity = 30;
//        final Coupon coupon = new Coupon(name, code, quantity);
//
//        this.couponKey = couponService.keyResolver(coupon.getCode());
//        this.coupon = coupon;
//        couponService.setUsableCoupon(this.couponKey, quantity);

    }

    @Test
    @Order(1)
    void 락사용_회원_10명이_예약_사용() throws InterruptedException{
        final int numberOfMember = 50;
        final CountDownLatch countDownLatch = new CountDownLatch(numberOfMember);

        List<Thread> threadList = Stream
                .generate(() -> new Thread(new ReservationServiceTest.UsingLockMember(this.reservationRequestDto, countDownLatch)))
                .limit(numberOfMember)
                .collect(Collectors.toList());

        threadList.forEach(Thread::start);
        countDownLatch.await();
    }

    private class UsingLockMember implements Runnable {
        private final ReservationRequestDto dto;
        private final CountDownLatch countDownLatch;

        public UsingLockMember(ReservationRequestDto dto, CountDownLatch countDownLatch) {
            this.dto = dto;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            reservationService.registReservation(dto, 14L); // userId
            countDownLatch.countDown();
        }
    }
}