package com.github.jhinslog.onpopserver.place.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "POP_STATUS")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PopStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private UUID statusId;

    @OneToOne(fetch = FetchType.LAZY)//지연 로딩 : 불필요한 테이블 정보 조회 지연.
    @JoinColumn(name = "place_id")
    private Place place;

    @Column(name = "congestion_level", nullable = false, length = 20)
    private String congestionLevel; //혼잡도 등급

    @Column(name = "congestion_msg", columnDefinition = "TEXT")
    private String congestionMsg;   //혼잡도 메시지

    @Column(name = "population_min")
    private Integer populationMin;  //최소 인구

    @Column(name = "population_max")
    private Integer populationMax;  //최대 인구

    @Column(name = "data_time")
    private LocalDateTime dataTime; //관측 시각

    @Column(name = "weather_status", length = 20)
    private String weatherStatus;   //기상 상태 코드

    @Column(name = "temperature")
    private Double temperature;     //기온

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;//수집 시간
}
