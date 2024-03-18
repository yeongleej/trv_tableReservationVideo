package com.ssafy.jariyo.domain.waiting.Serializable;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Waiting implements Serializable {
    private Long storeId;
    private String storeName;
    private Long userId;
    private String userName;
    private Integer waitingUserCount;
    private Boolean waitingIsPostpone;
    private LocalDateTime registeredTime;
    private Integer waitingUserOrder;
    private String storeImage;

    @Override
    public String toString() {
        return storeId + "," + storeName + "," + userId + "," + userName + "," +
                waitingUserCount + "," + waitingIsPostpone + "," + registeredTime + "," +
                waitingUserOrder + "," + storeImage;
    }
}
