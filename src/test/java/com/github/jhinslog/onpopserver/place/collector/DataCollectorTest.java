package com.github.jhinslog.onpopserver.place.collector;

import com.github.jhinslog.onpopserver.place.domain.Place;
import com.github.jhinslog.onpopserver.place.repository.PlaceRepository;
import com.github.jhinslog.onpopserver.place.repository.PopStatusRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class DataCollectorTest {

    @Autowired
    private DataCollector dataCollector;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PopStatusRepository popStatusRepository;

    @Test
    @DisplayName("스케줄러 실행 시 활성화된 모든 장소의 데이터를 수집해야 한다.")
    @Transactional
    void collectTest() {
        // given: 실제 API에서 데이터를 가져올 수 있는 정확한 장소명 설정
        Place place1 = Place.builder()
                .placeName("서울숲공원")
                .areaCode("TEST_001")
                .category("공원")
                .latitude(37.5443)
                .longitude(127.0374)
                .isActive(true)
                .build();

        Place place2 = Place.builder()
                .placeName("잠실종합운동장")
                .areaCode("TEST_002")
                .category("문화시설")
                .latitude(37.5117)
                .longitude(127.0728)
                .isActive(true)
                .build();

        placeRepository.save(place1);
        placeRepository.save(place2);

        // when: 스케줄링 대기 없이 수동 호출
        dataCollector.collect();

        // then: 수집 성공 여부 확인
        // [CS 지식: Persistence Context Reflection]
        // save 이후 영속성 컨텍스트에 데이터가 반영되었는지 검증합니다.
        assertThat(popStatusRepository.findByPlace(place1)).isPresent();
        assertThat(popStatusRepository.findByPlace(place2)).isPresent();
    }
}