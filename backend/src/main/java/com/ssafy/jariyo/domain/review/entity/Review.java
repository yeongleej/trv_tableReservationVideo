package com.ssafy.jariyo.domain.review.entity;

import com.ssafy.jariyo.domain.board.entity.BoardDomain;
import com.ssafy.jariyo.domain.store.entity.Store;
import com.ssafy.jariyo.domain.user.entity.User;
import com.ssafy.jariyo.global.entity.BaseObject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class Review extends BaseObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", referencedColumnName = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @Lob
    @Column(name = "review_content")
    private String reviewContent;

    @Setter
    @Column(name = "review_likes")
    private Integer reviewLikes;

    @Setter
    @Column(name = "review_star")
    private Integer reviewStar;

    public Review(User user, Store store, String reviewContent, Integer reviewStar) {
        this.user = user;
        this.store = store;
        this.reviewContent = reviewContent;
        this.reviewStar = reviewStar;
    }

    // Setter
}
