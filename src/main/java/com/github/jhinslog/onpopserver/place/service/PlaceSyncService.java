package com.github.jhinslog.onpopserver.place.service;

import com.github.jhinslog.onpopserver.place.domain.Place;
import com.github.jhinslog.onpopserver.place.dto.seoul.SeoulApiDto;
import com.github.jhinslog.onpopserver.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceSyncService {

    private final PlaceRepository placeRepository;
    private final SeoulApiService seoulApiService;

    /*장소 마스터 데이터 동기화*/

    @Transactional
    public void syncPlaces() {
        log.info("[Sync] 서울시 장소 마스터 목록 동기화를 시작합니다.");

        //1. 마스터 API로부터 전체 장소 리스트 조회
        List<SeoulApiDto.MasterPlace> apiMasterList = seoulApiService.fetchMasterPlaces();

        if (apiMasterList == null || apiMasterList.isEmpty()) {
            log.error("[Sync] API 응답 데이터가 비어있어 동기화를 중단합니다.");
            return;
        }

        //2. 모든장소 데이터를 AreaCode 기준으로 Map 생성
        Map<String, Place> dbPlaceMap = placeRepository.findAll().stream()
                .collect(Collectors.toMap(Place::getAreaCode, Function.identity()));


        for(SeoulApiDto.MasterPlace apiData : apiMasterList) {
            String areaCode = apiData.getAreaCode();

            if (dbPlaceMap.containsKey(areaCode)) {
                Place existingPlace = dbPlaceMap.get(areaCode);
                existingPlace.updateInfo(
                        apiData.getAreaName(),
                        apiData.getCategory(),
                        apiData.getAddress(),
                        apiData.getLatitude(),
                        apiData.getLongitude()
                );

                dbPlaceMap.remove(areaCode);

            } else {
                Place newPlace = Place.builder()
                        .areaCode(apiData.getAreaCode())
                        .placeName(apiData.getAreaName())
                        .category(apiData.getCategory())
                        .address(apiData.getAddress())
                        .latitude(apiData.getLatitude())
                        .longitude(apiData.getLongitude())
                        .isActive(true)
                        .build();

                placeRepository.save(newPlace);
                log.info("[Sync] 신규 장소 등록 완료: {}", newPlace.getPlaceName());
            }
        }

        if (!dbPlaceMap.isEmpty()) {
            int deactivatedCount = dbPlaceMap.size();
            dbPlaceMap.values().forEach(Place::deactivate);
            log.info("[Sync] 제공 중단된 {}개의 장소를 비활성화 처리 했습니다.", deactivatedCount);
        }

        log.info("[Sync] 서울시 장소 마스터 목록 동기화가 완료되었습니다.");
    }
}
