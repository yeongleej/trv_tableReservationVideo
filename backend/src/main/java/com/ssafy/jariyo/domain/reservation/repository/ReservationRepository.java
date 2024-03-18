package com.ssafy.jariyo.domain.reservation.repository;

import com.ssafy.jariyo.domain.reservation.entity.Reservation;
import com.ssafy.jariyo.domain.store.entity.Store;
import com.ssafy.jariyo.domain.user.entity.User;
import com.ssafy.jariyo.global.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // reservationId로 조회
    Reservation findByReservationId(Long reservationId);

    // 가게 정보로 조회
    List<Reservation> findAllByStore(Store store);

    // 사용자 정보로 조회
    List<Reservation> findAllByUser(User user);

    // 월(month)로 조회
    @Query("select r from Reservation r where month(r.reservationDate) =:month and r.store = :store")
    List<Reservation> findByMonthAndStore(int month, Store store);

    // 가게 + 날짜로 조회 (예: 2021-12-05)
    @Query("select r from Reservation r where (r.reservationDate = :searchDate) and r.store = :store")
    List<Reservation> findAllByDateAndStore(LocalDate searchDate, Store store);

    // 사용자 + 날짜로 조회
    @Query("select r from Reservation r where (r.reservationDate = :searchDate) and r.user = :user")
    List<Reservation> findAllByDateAndUser(LocalDate searchDate, User user);

    // 가게 + 날짜로 가능한 예약 조회
    @Query("select r from Reservation r where (r.reservationDate = :searchDate) and r.store = :store and r.status = :status")
    List<Reservation> findAllByDateAndStoreAndStatus(LocalDate searchDate, Store store, Status status);



}
