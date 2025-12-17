package com.github.jhinslog.onpopserver.place.dto.seoul;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true) //정의하지 않은 필드는 무시
public class SeoulApiDto {

    @JsonProperty("CITYDATA")
    private CityData cityData;

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CityData {

        @JsonProperty("AREA_NM")
        private String areaNm;                          //장소명 (예: 강남역)

        @JsonProperty("AREA_CODE")
        private String areaCode;                        //장소 코드

        @JsonProperty("LIVE_PPLTN_STTS")
        private List<LivePpltnStatus> livePpltnStatus;  //실시간 인구 현황

        @JsonProperty("ROAD_TRAFFIC_STTS")
        private RoadTrafficStatus roadTrafficStatus;    //실시간 도로 현황

        @JsonProperty("SUB_STTS")
        private List<SubwayStatus> subwayStatus;        //실시간 지하철 정보

        @JsonProperty("BUS_STN_STTS")
        private List<BusStationStatus> busStationStatus;//실시간 버스 정류장 정보

        @JsonProperty("WEATHER_STTS")
        private List<WeatherStatus> weatherStatus;      //실시간 날씨 정보

    }

    /*실시간 인구 현황 DTO (POP_STATUS, POPHISTORY)*/
    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LivePpltnStatus {
        @JsonProperty("AREA_CONGEST_LVL")
        private String congestionLevel;     //장소 혼잡도 지표 (여유, 보통, 붐빔, 매우 붐빔)

        @JsonProperty("AREA_CONGEST_MSG")
        private String congestionMsg;       //장소 혼잡도 상세 메시지

        @JsonProperty("AREA_PPLTN_MIN")
        private int populationMin;          //실시간 인구 최소값

        @JsonProperty("AREA_PPLTN_MAX")
        private int populationMax;          //실시간 인구 최대값

        @JsonProperty("PPLTN_TIME")
        private String populationTime;      //실시간 인구 데이터 업데이트 시각
    }

    /*실시간 도로 소통 현황 DTO (POP_STATUS)*/
    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RoadTrafficStatus {
        @JsonProperty("AVG_ROAD_DATA")
        private AvgRoadData avgRoadData;
    }

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AvgRoadData {
        @JsonProperty("ROAD_MSG")
        private String roadMsg;             //전체도로소통평균현황 메세지

        @JsonProperty("ROAD_TRAFFIC_IDX")
        private String roadTrafficIdx;      //도로 소통 지표 (원활, 서행, 정체)
    }

    /*실시간 지하철 정보 DTO*/
    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SubwayStatus {
        @JsonProperty("SUB_STN_NM")
        private String stationName;     //지하철역 명

        @JsonProperty("SUB_ROUTE_NM")
        private String routeName;       //호선 명 (예: 9호선)
    }

    /*실시간 버스 정류장 정보 DTO*/
    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BusStationStatus {
        @JsonProperty("BUS_STN_NM")
        private String stationName;     //정류소 명

        @JsonProperty("BUS_STN_ID")
        private String stationId;       //정류소 ID
    }

    /*실시간 날씨 정보 DTO*/
    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherStatus {
        @JsonProperty("TEMP")
        private Double temp;            //기온

        @JsonProperty("SKY_STTS")
        private String skyStatus;       //하늘 상태

        @JsonProperty("PCP_MSG")
        private String pcpMsg;          //강수관련 메시지

        @JsonProperty("PM10")
        private String pm10;            //미세먼지 농도
    }

}
