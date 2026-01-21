package com.github.jhinslog.onpopserver.place.repository;

import com.github.jhinslog.onpopserver.place.domain.Place;
import com.github.jhinslog.onpopserver.place.domain.PopTraffic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PopTrafficRepository extends JpaRepository<PopTraffic, UUID> {

    /*Idempotency(멱등성)*/
    // 새로운 교통 정보를 저장하기 전에 기존 데이터를 전부 삭제 : 중복 데이터 방지
    void deleteAllByPlace(Place place);
}
