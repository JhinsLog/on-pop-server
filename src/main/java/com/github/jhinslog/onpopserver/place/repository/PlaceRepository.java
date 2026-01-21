package com.github.jhinslog.onpopserver.place.repository;

import com.github.jhinslog.onpopserver.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlaceRepository extends JpaRepository<Place, UUID> {

    //지역 코드로 장소 찾기
    Optional<Place> findByAreaCode(String areaCode);

    //장소명으로 장소 찾기
    Optional<Place> findByPlaceName(String placeName);

    //현재 활성화된 장소 찾기(API 제공 장소)
    List<Place> findAllByIsActiveTrue();
}
