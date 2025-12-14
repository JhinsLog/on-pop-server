package com.github.jhinslog.onpopserver.pick.repository;

import com.github.jhinslog.onpopserver.pick.domain.Pick;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PickRepository extends JpaRepository<Pick, UUID> {

    //특정 유저의 픽 조회.
    List<Pick> findByUser_UserId(UUID userId);

    //이미 픽한 장소인지 확인. (토글 기능 구현)
    Optional<Pick> findByUser_UserIdAndPlace_PlaceId(UUID userId, UUID placeId);

    //픽 여부 확인
    boolean existsByUser_UserIdAndPlace_PlaceId(UUID userId, UUID placeId);
}
