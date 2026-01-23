package com.github.jhinslog.onpopserver.place.service;

import com.github.jhinslog.onpopserver.place.domain.Place;
import com.github.jhinslog.onpopserver.place.domain.PopHistory;
import com.github.jhinslog.onpopserver.place.domain.PopStatus;
import com.github.jhinslog.onpopserver.place.domain.PopTraffic;
import com.github.jhinslog.onpopserver.place.dto.seoul.SeoulApiDto;
import com.github.jhinslog.onpopserver.place.repository.PopHistoryRepository;
import com.github.jhinslog.onpopserver.place.repository.PopStatusRepository;
import com.github.jhinslog.onpopserver.place.repository.PopTrafficRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/*Layered Architecture(계층형 아키텍처)*/
@Slf4j
@Service
@RequiredArgsConstructor
public class SeoulApiService {

    private final WebClient.Builder webClientBuilder;
    private final PopStatusRepository popStatusRepository;
    private final PopHistoryRepository popHistoryRepository;
    private final PopTrafficRepository popTrafficRepository;


    @Value("${seoul.api.key}")
    private String apiKey;

    @Value("${seoul.api.url}")
    private String baseUrl;

    @Value("${seoul.api.type}")
    private String type;

    @Value("${seoul.api.service}")
    private String serviceName;

    @Value("${seoul.api.start-index}")
    private int startIndex;

    @Value("${seoul.api.end-index}")
    private int endIndex;

    private static final DateTimeFormatter API_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /*마스터 장소 목록 조회*/
    /*
    Asynchronous & Non-blocking I/O
    : 적은 수의 쓰레드로 네트워크 요청 처리량을 높이는 방법
    */
    public List<SeoulApiDto.MasterPlace> fetchMasterPlaces() {
        log.info("서울 API로 부터 장소 목록을 가져오는 중입니다...");

        URI uri = UriComponentsBuilder.fromUriString(String.valueOf(baseUrl))
                .pathSegment(apiKey, type, "citydata_master", "1", "200")
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();

        try {
            /*API Response Mapping*/
            SeoulApiDto response = webClientBuilder.build()
                    .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(SeoulApiDto.class)
                    .block();

            if (response != null && response.getMasterPlaces() != null) {
                log.info("성공적으로 {}개의 마스터 장소를 불러왔습니다.", response.getMasterPlaces().size());
                return response.getMasterPlaces();
            }

            return Collections.emptyList();
        } catch (Exception e) {
            log.info("불러오기 실패한 장소 목록: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /*Database Transaction (ACID)*/
    /*
    -원자성(Atomicity): 한 트랜잭션 내의 모든 작업은 모두 성공하거나 모두 실패해야 합니다.
    -일관성(Consistency): 트랜잭션 전후로 데이터의 제약 조건과 상태가 일관되어야 합니다.
    */
    @Transactional
    public void fetchAndSaveRealtimeData(Place place) {
        //1. API 데이터 호출
        SeoulApiDto response = fetchCityData(place.getPlaceName());

        if (response == null || response.getCityData() == null) {
            log.warn("API 장소 응답 데이터가 없음: {}", place.getPlaceName());
            return;
        }

        SeoulApiDto.CityData cityData = response.getCityData();
        SeoulApiDto.LivePpltnStatus ppltn = cityData.getLivePpltnStatus();
        SeoulApiDto.WeatherStatus weather = cityData.getWeatherStatus();

        if (ppltn != null) {    //Dirty Checking(변경 감지) : 트랜잭션 종료시 스냅샷과 현재 엔티티 차이를 분석하여 UPDATE 쿼리 발생.
            PopStatus status = popStatusRepository.findByPlace(place)
                    .orElse(new PopStatus(place));

            updatePopStatus(status, ppltn, weather);
            popStatusRepository.save(status);

            /*Space Complexity(공간 복잡도)*/
            savePopHistory(place, ppltn);

            syncPopTraffic(place, cityData);
        }
    }

    private void updatePopStatus(PopStatus status, SeoulApiDto.LivePpltnStatus ppltn, SeoulApiDto.WeatherStatus weather) {
        status.updateStatus(
                ppltn.getCongestionLevel(),
                ppltn.getCongestionMsg(),
                ppltn.getPopulationMin(),
                ppltn.getPopulationMax(),
                parseDateTime(ppltn.getPopulationTime()),
                weather != null ? weather.getSkyStatus() : null,
                weather != null ? weather.getTemp() : null
        );
    }

    /*Space Complexity(공간 복잡도)*/
    //인구 이력 수집이 5분 마다 진행 되기에 'O(T)'의 공간 복잡도를 가짐.
    private void savePopHistory(Place place, SeoulApiDto.LivePpltnStatus ppltn) {
        PopHistory history = PopHistory.builder()
                .place(place)
                .congestionLevel(ppltn.getCongestionLevel())
                .population((ppltn.getPopulationMin() + ppltn.getPopulationMax()) / 2)
                .dataTime(parseDateTime(ppltn.getPopulationTime()))
                .build();

        popHistoryRepository.save(history);
    }

    private void syncPopTraffic(Place place, SeoulApiDto.CityData data) {
        popTrafficRepository.deleteAllByPlace(place);

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

    /*Network Latency & Timeout*/
    public SeoulApiDto fetchCityData(String areaName) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("서울시 API 키가 설정되지 않았습니다.");
        }

        URI uri = UriComponentsBuilder.fromUriString(String.valueOf(baseUrl))
                .pathSegment(apiKey, type, serviceName, String.valueOf(startIndex), String.valueOf(endIndex), areaName)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();

        log.info("Fetching data from Seoul API: {}", areaName);
        log.debug("Request URI: {}", uri);

        try {
            return webClientBuilder.build()
                    .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(SeoulApiDto.class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to fetch data for area: {}. error: {}", areaName, e.getMessage());
            return null;
        }

    }

    /*Object Reuse(객체 재사용)*/
    // 매번 새로운 날짜 객체를 생성x, 재사용함에 따라 Garbage Collection 부하 감소.
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty())
            return LocalDateTime.now();

        return  LocalDateTime.parse(dateTimeStr, API_DATE_FORMAT);
    }
}