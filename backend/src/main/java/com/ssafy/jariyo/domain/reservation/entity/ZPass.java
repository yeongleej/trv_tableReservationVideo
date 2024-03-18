package com.ssafy.jariyo.domain.reservation.entity;

import com.ssafy.jariyo.domain.store.entity.Store;
import com.ssafy.jariyo.domain.user.entity.User;
import com.ssafy.jariyo.global.entity.BaseObject;
import com.ssafy.jariyo.global.entity.Status;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZPass extends BaseObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zpass_id", nullable = false)
    private Long zpassId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "store_id", referencedColumnName = "store_id")
//    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "zpass_created_dates")
    private String zpassCreatedDates;

    @Column(name = "zpass_name", length = 255)
    private String zpassName;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "zpass_status")
    private Status zpassStatus;

//    @Column(name = "zpass_expiration_date")
//    private LocalDateTime zpassExpirationDate;


    // Constructor,Setter

}
