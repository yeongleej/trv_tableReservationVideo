package com.ssafy.jariyo.domain.s3image.entity;

import com.ssafy.jariyo.domain.board.entity.Board;
import com.ssafy.jariyo.domain.store.entity.Store;
import com.ssafy.jariyo.global.entity.BaseObject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class StoreS3Image  extends BaseObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private S3Image s3Image;

    public StoreS3Image(Store store, S3Image s3Image) {
        this.store = store;
        this.s3Image = s3Image;
    }
}
