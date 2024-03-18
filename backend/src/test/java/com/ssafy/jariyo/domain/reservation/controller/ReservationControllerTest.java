package com.ssafy.jariyo.domain.reservation.controller;

import com.ssafy.jariyo.domain.reservation.entity.Reservation;
import com.ssafy.jariyo.domain.reservation.repository.ReservationRepository;
import com.ssafy.jariyo.domain.store.repository.StoreRepository;
import com.ssafy.jariyo.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.swing.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static reactor.core.publisher.Mono.when;

@ExtendWith(SpringExtension.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@WebMvcTest({ReservationController.class})
@Transactional
class ReservationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    StoreRepository storeRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    ReservationRepository reservationRepository;

    @Test
    void registReservation() throws Exception {
//        // given
//        String url = "/api/reservation/1/1"; // {storeId}/{userId}
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        String str1 = "2021-12-05 13:30";
//        String str2 = "2022-01-05 14:30";
//        LocalDateTime dateTime1 = LocalDateTime.parse(str1, formatter);
////        ZonedDateTime kstDateTime = ZonedDateTime.of(dateTime1, ZoneId.of("Asia/Seoul"));
////
////        // MySQL에 저장할 때는 LocalDateTime으로 변환하여 저장
////        LocalDateTime localDateTime1 = kstDateTime.toLocalDateTime();
//        System.out.println(dateTime1);
//        Reservation res1 = Reservation.builder()
//                .store(storeRepository.findByStoreId(1L))
//                .user(userRepository.findByUserId(1L))
//                .reservationDateTime(dateTime1)
//                .reservationUserCount(2)
//                .reservationTableNumber(5)
//                .build();
//        // when
//        ResultActions resultActions = mockMvc.perform(
//                MockMvcRequestBuilders.get(url).content(reservationRepository.save(res1)));
//        // then
////        reservationRepository.save(res1);
//        MvcResult mvcResult = resultActions
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andReturn();
//        System.out.println("mvcResult :: " + mvcResult.getResponse().getContentAsString());
    }

    @Test
    void getReservationByMonth() {
    }
}