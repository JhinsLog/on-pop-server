package com.github.jhinslog.onpopserver.place.repository;

import com.github.jhinslog.onpopserver.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlaceRepository extends JpaRepository<Place, UUID> {

    //지역 코드로 장소 찾기
    Optional<Place> findByAreaCode(String areaCode);
}
