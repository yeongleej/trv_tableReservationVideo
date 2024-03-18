package com.ssafy.jariyo.domain.reservation.entity;

import com.ssafy.jariyo.domain.reservation.dto.request.DiningTableRequestDto;
import com.ssafy.jariyo.domain.store.entity.Store;
import com.ssafy.jariyo.domain.user.entity.User;
import com.ssafy.jariyo.global.entity.BaseObject;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="dining_table", uniqueConstraints = {
        @UniqueConstraint(
                name="STORE_DININGTABLE_NUMBER_UNIQUE",
                columnNames={"store_id","dining_table_number"}
        )}
)
public class DiningTable extends BaseObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dining_table_id", nullable = false)
    private Long diningTableId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", referencedColumnName = "store_id", nullable = false)
    private Store store;

    @Column(name = "dining_table_number")
    private Integer diningTableNumber;

    @Column(name = "dining_table_capacity")
    private Integer diningTableCapacity;

    @Column(name = "dining_table_is_available")
    private Boolean diningTableIsAvailable;

    @Column(name = "dining_table_type")
    private String diningTableType;

    @Column(name = "dining_table_x")
    private Integer diningTableX;

    @Column(name = "dining_table_y")
    private Integer diningTableY;

    @Column(name = "dining_table_width")
    private Integer diningTableWidth;

    @Column(name = "dining_table_height")
    private Integer diningTableHeight;

    public void updateDiningTable(DiningTableRequestDto diningTableRequestDto){
        this.diningTableNumber = diningTableRequestDto.getDiningTableNumber();
        this.diningTableType = diningTableRequestDto.getDiningTableType();
        this.diningTableCapacity = diningTableRequestDto.getDiningTableCapacity();
        this.diningTableIsAvailable = diningTableRequestDto.getDiningTableIsAvailable();
        this.diningTableX = diningTableRequestDto.getDiningTableX();
        this.diningTableY = diningTableRequestDto.getDiningTableY();
        this.diningTableWidth = diningTableRequestDto.getWidth();
        this.diningTableHeight = diningTableRequestDto.getHeight();

    }

}
