package com.ssafy.jariyo.domain.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class StoreRequestRegistDto {

    // 사업자 대표명
    private String ownerName;

    @NotBlank(message = "Store name is required")
    @Size(max = 100, message = "Store name must not exceed 100 characters")
    private String storeName;

    @Size(max = 100, message = "Business number must not exceed 100 characters")
    private String storeBusinessNumber;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String storeAddress;

    private Double storePositionX;
    private Double storePositionY;

//    private List<DiningTableRequestDto> diningTables;

}
