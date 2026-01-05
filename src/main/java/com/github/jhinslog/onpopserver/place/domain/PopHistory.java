package com.github.jhinslog.onpopserver.place.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "POP_HISTORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PopHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) //UUID 전략 사용.
    @Column(name = "history_id")
    private UUID historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "congestion_level", nullable = false, length = 20)
    private String congestionLevel;

    @Column(name = "population", nullable = false)
    private int population;

    @Column(name = "data_time", nullable = false)
    private LocalDateTime dataTime;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

}
