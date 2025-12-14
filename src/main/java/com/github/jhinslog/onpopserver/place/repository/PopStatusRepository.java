package com.github.jhinslog.onpopserver.place.repository;

import com.github.jhinslog.onpopserver.place.domain.PopStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PopStatusRepository extends JpaRepository<PopStatus, UUID> {

    // 랭킹 조회: 인구수 기준 오름차순 정렬 (I모드)
    @Query("SELECT ps FROM PopStatus ps JOIN FETCH ps.place ORDER BY ps.populationMax ASC")
    List<PopStatus> findAllOrderPopulationMaxAsc();

    // 랭킹 조회: 인구수 기준 내림차순 정렬 (E모드)
    @Query("SELECT ps FROM PopStatus ps JOIN FETCH ps.place ORDER BY ps.populationMax DESC")
    List<PopStatus> findAllOrderPopulationMaxDesc();
}
