package com.github.jhinslog.onpopserver.pick.domain;

import com.github.jhinslog.onpopserver.place.domain.Place;
import com.github.jhinslog.onpopserver.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "PICKS", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "place_id"}) //1명의 유저가 같은 장소를 중복 픽 불가하도록 제약.
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pick {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pick_id")
    private UUID pickId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Pick(User user, Place place) {
        this.user = user;
        this.place = place;
    }
}
