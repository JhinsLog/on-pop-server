package com.github.jhinslog.onpopserver.place.domain;

import com.github.jhinslog.onpopserver.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name = "PLACES")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Place extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "place_id")
    private UUID placeId;

    @Column(name = "area_code", nullable = false, unique = true, length = 100)
    private String areaCode;

    @Column(name = "place_name", nullable = false, length = 100)
    private String placeName;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "address")
    private String address;

    @Column(name = "latitude", nullable = false) //위도
    private Double latitude;

    @Column(name = "longitude", nullable = false) //경도
    private Double longitude;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;


}
