package com.github.jhinslog.onpopserver.place.repository;

import com.github.jhinslog.onpopserver.place.domain.Place;
import com.github.jhinslog.onpopserver.place.domain.PopTraffic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PopTrafficRepository extends JpaRepository<PopTraffic, UUID> {

    //데이터 수집시 기존 주변 교통 정보 삭제 후 재 생성
    void deleteAllByPlaceteAllByPlace(Place place);
}
