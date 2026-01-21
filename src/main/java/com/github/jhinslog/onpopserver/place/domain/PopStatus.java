package com.github.jhinslog.onpopserver.place.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "POP_STATUS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) //빌더가 내부적으로 사용할 생성자
@Builder
public class PopStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "status_id")
    private UUID statusId;

    @OneToOne(fetch = FetchType.LAZY)//지연 로딩 : 불필요한 테이블 정보 조회 지연.
    @JoinColumn(name = "place_id", unique = true, nullable = false, updatable = false)  //1:1 관계, FK
    private Place place;

    @Column(name = "congestion_level", nullable = false, length = 20)
    private String congestionLevel; //혼잡도 등급

    @Column(name = "congestion_msg")
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

    public PopStatus(Place place) {
        this.place = place;
        //초기 생성시 데이터 불일치 방지용 기본값 설정
        this.congestionLevel = "PENDING";
    }
    
    /*PoP 업데이트 메서드*/
    public void updateStatus(String congestionLevel, String congestionMsg,
                             Integer populationMin, Integer populationMax,
                             LocalDateTime dataTime, String weatherStatus, Double temperature) {
        this.congestionLevel = congestionLevel;
        this.congestionMsg = congestionMsg;
        this.populationMin = populationMin;
        this.populationMax = populationMax;
        this.dataTime = dataTime;
        this.weatherStatus = weatherStatus;
        this.temperature = temperature;
    }
}
