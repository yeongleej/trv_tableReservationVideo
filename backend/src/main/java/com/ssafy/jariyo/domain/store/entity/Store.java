package com.ssafy.jariyo.domain.store.entity;

import com.ssafy.jariyo.domain.store.dto.request.StoreRequestRegistDto;
import com.ssafy.jariyo.domain.user.entity.User;
import com.ssafy.jariyo.global.entity.BaseObject;
import com.ssafy.jariyo.global.entity.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store extends BaseObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @Column(name = "store_name", length = 100)
    private String storeName;

    @Setter
    @Column(name = "store_phone", length = 255)
    private String storePhone;

    @Lob
    @Setter
    @Column(name = "store_detail")
    private String storeDetail;

    @Setter
    @Column(name ="store_menu")
    private String storeMenu;

    @Setter
    @Column(name = "store_business_number", length = 100, unique = true)
    private String storeBusinessNumber;

    @Setter
    @Column(name = "store_address", length = 255)
    private String storeAddress;

    @Setter
    @Column(name = "store_is_wating")
    private Boolean storeIsWaiting;

    @Setter
    @Column(name = "store_rating")
    private Double storeRating;

    @Setter
    @Column(name = "store_follow_count")
    private Integer storeFollowCount;

    @Setter
    @Column(name = "store_operationHours", length = 255)
    private String storeOperationHours;

    @Setter
    @Column(name = "store_operationDates", length = 255)
    private String storeOperationDates;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "store_reservation_status")
    private Status storeReservationStatus;

    @Setter
    @Column(name = "store_reservation_available_dates", length = 255)
    private String storeReservationAvailableDates;

    @Setter
    @Column(name = "store_reservation_available_hours", length = 255)
    private String storeReservationAvailableHours;

    @Setter
    @Column(name = "store_position_x")
    private Double storePositionX;

    @Setter
    @Column(name = "store_position_y")
    private Double storePositionY;

    @Setter
    @Column(name = "store_business_file")
    private String storeBusinessFile;

    @Setter
    @Column(name = "store_menu_image")
    private String storeMenuImage;

    public Store(StoreRequestRegistDto dto, User user) {
        this.storeName = dto.getStoreName();
        this.storeBusinessNumber = dto.getStoreBusinessNumber();
        this.storeAddress = dto.getStoreAddress();
        this.storePositionX = dto.getStorePositionX();
        this.storePositionY = dto.getStorePositionY();
        this.user = user;
    }

    // Constructor , Setter
}
