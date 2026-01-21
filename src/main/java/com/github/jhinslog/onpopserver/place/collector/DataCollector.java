package com.github.jhinslog.onpopserver.place.collector;

import com.github.jhinslog.onpopserver.place.domain.Place;
import com.github.jhinslog.onpopserver.place.repository.PlaceRepository;
import com.github.jhinslog.onpopserver.place.service.SeoulApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataCollector {

    private final SeoulApiService seoulApiService;
    private final PlaceRepository placeRepository;

    /*Deterministic (결정론적 스케쥴링)*/
    @Scheduled(cron = "0 0/5 * * * *") //00:00 기준 5분 단위로 실행
    public void collect() {
        log.info(">>> [On-PoP] 실시간 데이터 수집 시작");

        List<Place> targetPlaces = placeRepository.findAllByIsActiveTrue(); //유효한 데이터만 로드(오버헤드 방지)

        if (targetPlaces.isEmpty()) {
            log.warn("수집 대상 장소가 없습니다. DB에 초기 장소 데이터를 먼저 넣어주세요.");
            return;
        }

        log.info("수집 대상: {}개 장소", targetPlaces.size());

        for (Place place : targetPlaces) {
            try {
                seoulApiService.fetchAndSaveRealtimeData(place);
            } catch (Exception e) {
                log.error("장소 수집 실패: {} - {}", place.getPlaceName(), e.getMessage());
            }
        }

        log.info(">>> [On-PoP] 데이터 수집 완료");
    }
}
