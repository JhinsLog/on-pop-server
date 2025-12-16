package com.github.jhinslog.onpopserver.place.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name = "POP_TRAFFIC")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PopTraffic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "traffic_id")
    private UUID trafficId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "traffic_type", nullable = false, length = 10)
    private String trafficType; //SUBWAY, BUS

    @Column(name = "station_name", nullable = false, length = 50)
    private String stationName;

    @Column(name = "line_info", length = 50)
    private String lineInfo;

    @Column(name = "distance_meters")
    private Integer distanceMeters;

    @Column(name = "congestion_status", length = 20)
    private String congestionStatus;
}
