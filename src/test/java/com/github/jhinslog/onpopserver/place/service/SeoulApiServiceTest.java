package com.github.jhinslog.onpopserver.place.service;

import com.github.jhinslog.onpopserver.place.domain.Place;
import com.github.jhinslog.onpopserver.place.dto.seoul.SeoulApiDto;
import com.github.jhinslog.onpopserver.place.repository.PlaceRepository;
import com.github.jhinslog.onpopserver.place.repository.PopStatusRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test") // application-test.yml 설정 사용
class SeoulApiServiceTest {

    @Autowired
    private SeoulApiService seoulApiService;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PopStatusRepository popStatusRepository;

    @Test
    @DisplayName("서울시 API로부터 마스터 장소 목록을 정상적으로 가져와야 한다.")
    void fetchMasterPlacesTest() {

        List<SeoulApiDto.MasterPlace> masterPlaces = seoulApiService.fetchMasterPlaces();

        assertThat(masterPlaces).isNotNull();
        // 목록이 비어있다면 API 키 설정을 다시 확인해야 합니다.
        if (masterPlaces.isEmpty()) {

            SeoulApiDto.MasterPlace firstPlace = masterPlaces.get(0);
            assertThat(firstPlace.getAreaName()).as("장소명이 비어있으면 안 됩니다.").isNotEmpty();
            assertThat(firstPlace.getAreaCode()).as("장소 코드가 비어있으면 안 됩니다.").isNotEmpty();

            System.out.println(">>> 매핑 성공 확인 - 장소 명: " + firstPlace.getAreaName());
            System.out.println(">>> 매핑 성공 확인 - 장소 코드: " + firstPlace.getAreaCode());
        }
    }

    @Test
    @DisplayName("특정 장소의 실시간 데이터를 가져와서 DB에 저장해야 한다.")
    @Transactional
    void fetchAndSaveRealtimeDataTest() {
        // given
        Place place = Place.builder()
                .placeName("강남역")
                .areaCode("POI001")
                .category("관광특구")
                .latitude(37.4979)
                .longitude(127.0276)
                .isActive(true)
                .build();
        placeRepository.save(place);

        // when
        seoulApiService.fetchAndSaveRealtimeData(place);

        // then
        popStatusRepository.findByPlace(place).ifPresentOrElse(
                status -> {
                    assertThat(status.getCongestionLevel()).isNotNull();
                    System.out.println(">>> 수집 완료: " + status.getCongestionLevel());
                },
                () -> {
                    // 수집 실패 시 원인 파악을 위해 명시적으로 실패 처리
                    throw new AssertionError("PopStatus가 생성되지 않았습니다. API 응답 로그를 확인하세요.");
                }
        );
    }
}