package com.github.jhinslog.onpopserver.place.repository;

import com.github.jhinslog.onpopserver.pick.domain.Pick;
import com.github.jhinslog.onpopserver.place.domain.PopHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PopHistoryRepository extends JpaRepository<Pick, UUID> {

    //특정 장소의 이력을 시간순으로 조회.
    List<PopHistory> findByPlace_PlaceIdOrderByDataTimeAsc(UUID placeId);
}
