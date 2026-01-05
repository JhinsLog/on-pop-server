package com.github.jhinslog.onpopserver.place.collector;

import com.github.jhinslog.onpopserver.place.domain.Place;
import com.github.jhinslog.onpopserver.place.domain.PopHistory;
import com.github.jhinslog.onpopserver.place.domain.PopStatus;
import com.github.jhinslog.onpopserver.place.domain.PopTraffic;
import com.github.jhinslog.onpopserver.place.dto.seoul.SeoulApiDto;
import com.github.jhinslog.onpopserver.place.repository.PlaceRepository;
import com.github.jhinslog.onpopserver.place.repository.PopHistoryRepository;
import com.github.jhinslog.onpopserver.place.repository.PopStatusRepository;
import com.github.jhinslog.onpopserver.place.repository.PopTrafficRepository;
import com.github.jhinslog.onpopserver.place.service.SeoulApiService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataCollector {

    private final SeoulApiService seoulApiService;
    private final PlaceRepository placeRepository;
    private final PopStatusRepository popStatusRepository;
    private final PopHistoryRepository popHistoryRepository;
    private final PopTrafficRepository popTrafficRepository;

    private static final DateTimeFormatter API_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Scheduled(fixedRate = 300000) //5분 단위로 실행
    public void collect() {
        log.info(">>> [On-PoP] 실시간 데이터 수집 시작");

        List<Place> targetPlaces = placeRepository.findAll();

        if (targetPlaces.isEmpty()) {
            log.warn("수집 대상 장소가 없습니다. DB에 초기 장소 데이터를 먼저 넣어주세요.");
            return;
        }

        for (Place place : targetPlaces) {
            try {
                syncSinglePlace(place);
            } catch (Exception e) {
                log.error("장소 수집 실패: {} - {}", place.getPlaceName(), e.getMessage());
            }
        }

        log.info(">>> [On-PoP] 데이터 수집 완료");
    }

    /*단일 장소 데이터 동기화*/
    @Transactional
    public void syncSinglePlace(Place place) {
        /*1. API 데이터 호출*/
        SeoulApiDto response = seoulApiService.fetchCityData(place.getPlaceName());

        if (response == null || response.getCityData() == null) {
            log.debug("API 응답 데이터가 없음: {}", place.getPlaceName());
            return;
        }

        SeoulApiDto.CityData data = response.getCityData();
        SeoulApiDto.LivePpltnStatus ppltn = data.getLivePpltnStatus();
        SeoulApiDto.WeatherStatus weather = data.getWeatherStatus();

        if (ppltn == null) return;

        //LocalDateTime으로 파싱
        LocalDateTime populationTime = LocalDateTime.parse(ppltn.getPopulationTime(), API_DATE_FORMAT);

        /*2. POP_STATUS 업데이트*/
        PopStatus status = popStatusRepository.findByPlace(place)
                .orElseGet(() -> PopStatus.builder()    //Builder 패턴 적용
                        .place(place)
                        .congestionLevel("PENDING")
                        .build());

        status.updateStatus(
                ppltn.getCongestionLevel(),
                ppltn.getCongestionMsg(),
                ppltn.getPopulationMin(),
                ppltn.getPopulationMax(),
                populationTime,
                weather != null ? weather.getSkyStatus() : null,
                weather != null ? weather.getTemp() : null
        );
        popStatusRepository.save(status);

        /*3. POP_HISTORY(인구 이력) 추가*/
        PopHistory history = PopHistory.builder()
                .place(place)
                .congestionLevel(ppltn.getCongestionLevel())
                .population(ppltn.getPopulationMax())
                .dataTime(populationTime)
                .build();
        popHistoryRepository.save(history);

        /*4. POP_TRAFFIC 동기화*/
        // 잦은 업데이트로 인해 기존 정보 삭제 후 새로 생성.
        popTrafficRepository.deleteAllByPlace(place);

        /*지하철 정보 저장*/
        if (data.getSubwayStatus() != null) {
            data.getSubwayStatus().forEach(s -> {
                popTrafficRepository.save(PopTraffic.builder()
                        .place(place)
                        .trafficType("SUBWAY")
                        .stationName(s.getStationName())
                        .lineInfo(s.getRouteName())
                        .build());
            });
        }

        /*버스 정보 저장*/
        if (data.getBusStationStatus() != null) {
            data.getBusStationStatus().forEach(b -> {
                popTrafficRepository.save(PopTraffic.builder()
                        .place(place)
                        .trafficType("BUS")
                        .stationName(b.getStationName())
                        .build());
            });
        }
    }
}
