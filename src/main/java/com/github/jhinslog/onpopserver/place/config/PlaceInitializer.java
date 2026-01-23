package com.github.jhinslog.onpopserver.place.config;

import com.github.jhinslog.onpopserver.place.service.PlaceSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/*Spring Bean Lifecycle*/
@Configuration
@RequiredArgsConstructor
@Slf4j
public class PlaceInitializer implements CommandLineRunner {

    private final PlaceSyncService placeSyncService;

    @Override
    public void run(String... args) {
        log.info("[On-PoP] 시스템 가동: 장소 마스터 데이터 동기화를 시도합니다.");
        try {
            //서버 시작 시 DB를 최신 장소 목록으로 업데이트
            placeSyncService.syncPlaces();
        } catch (Exception e) {
            log.error("[On-PoP] 초기 장소 동기화 중 치명적 오류 발생: {}", e.getMessage());
        }
    }
}
