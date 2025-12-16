온팝(On-PoP) 프로젝트 개발 가이드라인 (System Prompt)

이 문서는 '온팝(On-PoP)' 백엔드 서버(Spring Boot) 개발을 위한 핵심 규칙과 컨텍스트를 정의합니다.
AI 어시스턴트는 아래 규칙을 엄수하여 코드를 생성해야 합니다.

1. 프로젝트 개요 (Context)
 - 프로젝트명: On-PoP (온팝)
 - 목적: 서울시 120개 주요 장소의 실시간 혼잡도를 사용자 성향(I/E 모드)에 맞춰 큐레이션 하는 서비스.
 - 아키텍처: Simple Monolith (Scheduler -> DB -> API)
 - 기술 스택: Java 21, Spring Boot 4.0.0 (Gradle), PostgreSQL, Spring Data JPA, Lombok, WebFlux (WebClient용)

2. 코딩 컨벤션 (Coding Standards)

    2.1 패키지 구조 (DDD Lite)

    - 도메인형 패키지 구조를 따릅니다.
    - global: 공통 설정, 에러 핸들링, BaseEntity
    - user: 사용자 도메인 (User, Auth)
    - place: 장소 도메인 (Place, Status, History, Traffic)
    - pick: 관심 장소 도메인 (Pick)
    - 각 도메인 패키지 하위에 domain, controller, service, repository, dto 패키지를 둡니다.

    2.2 엔티티(Entity) 설계 규칙

    - 식별자(PK): 모든 Entity의 ID는 UUID 타입을 사용하며, @GeneratedValue(strategy = GenerationType.UUID)를 적용합니다.
    - 기본 생성자: JPA 스펙을 위해 protected 레벨의 @NoArgsConstructor(access = AccessLevel.PROTECTED)를 사용합니다.
    - Lombok: @Getter는 기본 사용, @Setter는 사용 금지 (비즈니스 메서드로 상태 변경).
    - 테이블명: snake_case 사용 (예: pop_status).
    - 컬럼명: snake_case 사용 (Java 필드는 camelCase).
    - 공통 필드:
        - created_at, updated_at이 필요한 엔티티는 BaseTimeEntity를 상속받습니다.
        - 생성일만 필요한 엔티티(Pick, Log)는 BaseCreatedEntity를 상속받습니다.

    2.3 DTO (Data Transfer Object) 규칙

    - Request/Response 분리: Entity를 직접 반환하지 않고 반드시 DTO로 변환합니다.
    - Inner Class 활용: 외부 API 연동 DTO(예: SeoulApiDto)는 관련 클래스들을 static inner class로 한 파일에 모아서 관리합니다.
    - Record 사용 권장: 단순 데이터 전달용 DTO는 Java record 사용을 고려합니다.

    2.4 Repository & Query

    - Spring Data JPA: 기본적으로 JpaRepository를 사용합니다.
    - 복잡한 쿼리: 필요한 경우 QueryDSL을 사용하거나 JPQL(@Query)을 사용합니다.
    - 메서드 명명: findBy..., existsBy... 등 JPA 표준 네이밍 규칙을 준수합니다.

3. 핵심 비즈니스 로직 가이드

    3.1 사용자 (User)
    - 개인정보 미수집: 이메일, 실명 등을 저장하지 않습니다.
    - 식별: social_id + provider 조합으로 유저를 식별합니다.
    - 닉네임: 회원가입 시 랜덤 닉네임을 생성합니다.
    - 모드: user_mode 컬럼은 'I'(내향) 또는 'E'(외향) 값만 가집니다.

    3.2 장소 (Place)
    - 구조: Places(정적) - PopStatus(동적 1:1) - PopHistory(이력 1:N) - PopTraffic(교통 1:N)
    - 데이터 수집: 스케줄러가 5분마다 서울시 API를 호출하여 데이터를 갱신합니다.
    - 검색: Places 테이블의 place_name을 LIKE 검색합니다.

    3.3 관심 장소 (Pick)
    - 토글 방식: 찜하기 요청 시 이미 존재하면 삭제(Delete), 없으면 생성(Insert)합니다.

4. 응답 포맷 (Response Format)

    - 모든 API 응답은 아래와 같은 표준 포맷을 따릅니다.


        {
        "code": "SUCCESS", // 또는 에러 코드
        "message": "요청이 성공했습니다.",
        "data": { ... } // 실제 데이터
        }
