서울 실시간 도시데이터 API 개발 가이드

이 문서는 '서울 실시간 도시데이터 API'를 연동하기 위한 핵심 정보를 요약한 개발자용 매뉴얼입니다.

1. API 개요

    API 명: 서울 실시간 도시데이터 API (Seoul Real-time Urban Data API)

    제공처: 서울 열린데이터광장

    데이터 갱신 주기: 5분 내외

    주요 기능: 서울시 주요 120개 장소의 실시간 인구, 교통, 환경(날씨), 문화 행사 정보를 제공.

    요청 URL: http://openapi.seoul.go.kr:8088/{KEY}/{TYPE}/citydata/{START_INDEX}/{END_INDEX}/{AREA_NM}

        KEY: 인증키

        TYPE: json 또는 xml (본 프로젝트는 json 사용)

        START_INDEX / END_INDEX: 페이징 (보통 1/5)

        AREA_NM: 장소명 (예: 강남역, 서울숲공원)

2. 응답 데이터 구조 (JSON Schema)

API 응답의 최상위 키는 CITYDATA이며, 그 아래에 각 카테고리별 데이터가 객체 또는 리스트 형태로 포함됩니다.

2.1 최상위 구조 (Root)

    {
        "CITYDATA": {
            "AREA_NM": "강남역",             // 장소명
            "LIVE_PPLTN_STTS": [ ... ],     // [1] 실시간 인구 현황
            "ROAD_TRAFFIC_STTS": { ... },   // [2] 실시간 도로 소통 현황
            "SUB_STTS": [ ... ],            // [3] 실시간 지하철 승하차 (Optional)
            "BUS_STN_STTS": [ ... ],        // [4] 실시간 버스 정류장 (Optional)
            "WEATHER_STTS": [ ... ]         // [5] 실시간 날씨
        }
    }


2.2 실시간 인구 현황 (LIVE_PPLTN_STTS)

가장 핵심적인 데이터입니다. POP_STATUS 및 POP_HISTORY 테이블에 매핑됩니다.

- 타입: List (보통 1개의 객체만 포함)

  - 주요 필드:

  필드명 (Key)   타입  설명  매핑 (Entity Field)

      AREA_CONGEST_LVL    String  장소 혼잡도 지표 (여유, 보통, 붐빔, 매우 붐빔)   congestionLevel

      AREA_CONGEST_MSG    String  장소 혼잡도 관련 안내 메시지    congestionMsg

      AREA_PPLTN_MIN      Integer 실시간 인구 지표 최소값   populationMin

      AREA_PPLTN_MAX      Integer 실시간 인구 지표 최대값   populationMax

      MALE_PPLTN_RATE     Double  남성 인구 비율    (사용 안 함)

      FEMALE_PPLTN_RATE   Double  여성 인구 비율    (사용 안 함)

      PPLTN_TIME          String  실시간 인구 데이터 업데이트 시간 (YYYY-MM-DD HH:MM)   dataTime

      FCST_PPLTN          List    (구) 인구 예측 데이터 (사용 안 함)



2.3 실시간 도로 소통 현황 (ROAD_TRAFFIC_STTS)

주변 도로의 소통 원활 정도를 나타냅니다.

- 타입: Object

  - 주요 필드:

      - AVG_ROAD_DATA: 도로별 평균 속도 데이터 객체

      - ROAD_MSG: 전체적인 도로 소통 메시지 (예: "서초대로가 정체입니다.") -> POP_TRAFFIC 또는 POP_STATUS에 요약 저장 가능.

      - ROAD_TRAFFIC_IDX: 소통 지표 (원활, 정체 등)

2.4 대중교통 정보 (SUB_STTS, BUS_STN_STTS)

POP_TRAFFIC 테이블에 매핑될 데이터입니다.

A. 지하철 (SUB_STTS)

- 타입: List

- 주요 필드:

    - SUB_STN_NM: 지하철역 이름 (예: 강남역) -> stationName

    - SUB_ROUTE_NM: 호선명 (예: 2호선) -> lineInfo

B. 버스 정류장 (BUS_STN_STTS)

- 타입: List

- 주요 필드:

    - BUS_STN_NM: 정류소 이름 -> stationName

    - BUS_STN_ID: 정류소 ID

2.5 실시간 날씨 (WEATHER_STTS)

POP_STATUS 테이블에 매핑됩니다.

- 타입: List

- 주요 필드:

    - SKY_STTS: 하늘 상태 (맑음, 구름많음 등) -> weatherStatus (간략화)

    - TEMP: 기온 -> temperature

    - PM10: 미세먼지 지표

    - PM25: 초미세먼지 지표

3. 개발 참고 사항 (Tips for AI)

    1. JSON 파싱: Jackson 라이브러리를 사용하세요.

    2. 필드 매핑: API의 SNAKE_CASE 키를 Java의 camelCase 필드로 매핑하기 위해 @JsonProperty("KEY_NAME") 어노테이션을 사용해야 합니다.

    3. 예외 처리: API 응답에 특정 필드(SUB_STTS 등)가 아예 없을 수도 있습니다. ignoreUnknown = true 설정과 Null Check가 필수입니다.

    4. 인코딩: 한글 장소명(AREA_NM)을 URL Path에 넣을 때 반드시 UTF-8 인코딩을 해야 합니다.