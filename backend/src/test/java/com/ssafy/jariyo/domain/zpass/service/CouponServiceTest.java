package com.ssafy.jariyo.domain.zpass.service;

import com.ssafy.jariyo.domain.zpass.entity.Coupon;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class CouponServiceTest {

    @Autowired
    private CouponService couponService;
    private String couponKey;
    private Coupon coupon;

    @BeforeEach
    void 쿠폰_세팅() {
        final String name = "name";
        final String code = "Q2LJ5GZ52OUNW9BV";
        final int quantity = 1;
        final Coupon coupon = new Coupon(name, code, quantity);

        this.couponKey = couponService.keyResolver(coupon.getCode());
        this.coupon = coupon;
        couponService.setUsableCoupon(this.couponKey, quantity);
    }

    @Test
    @Order(1)
    void 락사용_회원_50명이_쿠폰_사용() throws InterruptedException{
        final int numberOfMember = 5;
        final CountDownLatch countDownLatch = new CountDownLatch(numberOfMember);

        List<Thread> threadList = Stream
                .generate(() -> new Thread(new UsingLockMember(this.couponKey, countDownLatch)))
                .limit(numberOfMember)
                .collect(Collectors.toList());

        threadList.forEach(Thread::start);
        countDownLatch.await();
    }

    @Test
    @Order(2)
    void 락사용안함_회원_50명함_쿠폰_사용() {
        final int numberOfMember = 50;
        final CountDownLatch countDownLatch = new CountDownLatch(numberOfMember);

        List<Thread> threadList = Stream
                .generate(() -> new Thread(new NoLockMember(this.couponKey, countDownLatch)))
                .limit(numberOfMember)
                .collect(Collectors.toList());

        threadList.forEach(Thread::start);
    }

    private class UsingLockMember implements Runnable {
        private final String couponKey;
        private final CountDownLatch countDownLatch;

        public UsingLockMember(String couponKey, CountDownLatch countDownLatch) {
            this.couponKey = couponKey;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            couponService.useCouponWithLock(this.couponKey);
            countDownLatch.countDown();
        }
    }

    private class NoLockMember implements Runnable {
        private final String couponKey;
        private final CountDownLatch countDownLatch;

        public NoLockMember(String couponKey, CountDownLatch countDownLatch) {
            this.couponKey = couponKey;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            couponService.useCouponNoLock(this.couponKey);
            countDownLatch.countDown();
        }
    }
}