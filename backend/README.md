# Point Roulette Backend

> Spring Boot 기반 포인트 룰렛 시스템 백엔드 API 서버

---

## 목차

1. [기술 스택](#-기술-스택)
2. [프로젝트 구조](#-프로젝트-구조)
3. [아키텍처](#-아키텍처)
4. [데이터베이스 스키마](#-데이터베이스-스키마)
5. [코딩 컨벤션](#-코딩-컨벤션)
7. [테스트](#-테스트)
8. [API 명세](#-api-명세)

---

## 기술 스택

### Core
- **Language**: Kotlin 2.1.10
- **Framework**: Spring Boot 3.5.10
- **JDK**: OpenJDK 21

### Database
- **RDBMS**: PostgreSQL
- **ORM**: Spring Data JPA (Hibernate)

### Libraries
- **Validation**: Spring Boot Starter Validation
- **Testing**: JUnit 5, Mockito

---

## 프로젝트 구조

```
backend/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com.pointroulette/
│   │   │       ├── PointRouletteApplication.kt
│   │   │       │
│   │   │       ├── domain/              # 도메인 계층 (도메인별 분리)
│   │   │       │   ├── user/
│   │   │       │   │   ├── User.kt
│   │   │       │   │   └── UserRepository.kt
│   │   │       │   ├── point/
│   │   │       │   ├── order/
│   │   │       │   ├── roulette/
│   │   │       │   └── budget/
│   │   │       │
│   │   │       ├── application/         # 응용 계층 (도메인별 분리)
│   │   │       │   ├── user/
│   │   │       │   │   ├── UserService.kt
│   │   │       │   │   └── dto/
│   │   │       │   ├── point/
│   │   │       │   ├── order/
│   │   │       │   ├── roulette/
│   │   │       │   └── budget/
│   │   │       │
│   │   │       ├── infrastructure/      # 인프라 계층
│   │   │       │   ├── persistence/     # Repository 구현체 (필요시)
│   │   │       │   └── config/          # 공통 설정
│   │   │       │       ├── JpaConfig.kt
│   │   │       │       └── SecurityConfig.kt
│   │   │       │
│   │   │       └── presentation/        # 표현 계층 (도메인별 분리)
│   │   │           ├── user/
│   │   │           │   ├── UserController.kt
│   │   │           │   └── dto/
│   │   │           ├── point/
│   │   │           ├── order/
│   │   │           ├── roulette/
│   │   │           ├── budget/
│   │   │           └── exception/       # 공통 예외 처리
│   │   │               └── GlobalExceptionHandler.kt
│   │   │
│   │   └── resources/
│   │       ├── application.yaml
│   │       └── application-{profile}.yaml
│   │
│   └── test/
│       └── kotlin/
│           └── com.pointroulette/
│               ├── domain/
│               │   ├── user/
│               │   └── point/
│               ├── application/
│               │   ├── user/
│               │   └── point/
│               └── integration/
│
├── build.gradle
└── settings.gradle
```

### 패키지별 역할

#### domain (도메인 계층)
- **구조**: 도메인별로 패키지 분리 (`domain/user/`, `domain/point/`)
- **포함**: JPA Entity, Repository Interface, Domain Service
- **특징**: 핵심 비즈니스 로직, 외부 의존성 최소화
- **예시**: `domain/user/User.kt`, `domain/point/PointRepository.kt`

#### application (응용 계층)
- **구조**: 도메인별로 패키지 분리 (`application/user/`, `application/point/`)
- **포함**: Service, DTO
- **특징**: Use Case 구현, 도메인 로직 조합, 트랜잭션 관리
- **예시**: `application/user/UserService.kt`, `application/user/dto/UserDto.kt`

#### infrastructure (인프라 계층)
- **구조**:
  - `persistence/`: Repository 구현체 (필요시)
  - `config/`: 공통 설정 파일
- **포함**: JPA Config, Security Config, 외부 시스템 연동
- **특징**: 기술적 세부사항, 공통 인프라 설정
- **예시**: `infrastructure/config/JpaConfig.kt`

#### presentation (표현 계층)
- **구조**: 도메인별로 패키지 분리 (`presentation/user/`, `presentation/point/`)
- **포함**: Controller, Request/Response DTO
- **공통**: `exception/` 패키지에 전역 예외 처리
- **특징**: REST API 엔드포인트, 요청/응답 변환
- **예시**: `presentation/user/UserController.kt`, `presentation/exception/GlobalExceptionHandler.kt`

### 설계 원칙

#### 1. 도메인 중심 구조
- 각 계층 내에서 도메인별로 패키지 분리
- 도메인 경계가 명확하여 검색 및 유지보수 용이
- 도메인 간 독립성 보장

#### 2. 계층 간 의존성 규칙 (DIP)
```
Presentation → Application → Domain
                ↓
         Infrastructure
```
- 상위 계층은 하위 계층에만 의존
- Domain은 다른 계층에 의존하지 않음
- Infrastructure는 Domain의 인터페이스를 구현

---

## 아키텍처

### Layered Architecture
```
┌─────────────────────────────────────┐
│      Presentation Layer             │  ← REST Controller, DTO
├─────────────────────────────────────┤
│      Application Layer              │  ← Service, Use Case
├─────────────────────────────────────┤
│      Domain Layer                   │  ← Entity, Domain Logic
├─────────────────────────────────────┤
│      Infrastructure Layer           │  ← Repository, Config
└─────────────────────────────────────┘
```

### 주요 설계 원칙

#### 1. JPA 연관관계 활용
- 양방향 연관관계 매핑
- **Lazy Loading** 기본 전략
- N+1 문제 해결: **Fetch Join** 적극 활용
- OSIV(Open Session In View) 비활성화

#### 2. 포인트 만료 관리 (FIFO)
- 포인트는 개별 건으로 관리 (`Point` 엔티티)
- 각 포인트는 만료일(`expiresAt`) 보유
- 사용 시 **오래된 순서대로 차감** (First-In-First-Out)
- 만료된 포인트는 배치 작업으로 정리

#### 3. 동시성 제어
- 주요 적용 대상
  - `DailyBudget`: 일일 예산 차감 시 동시성 보장
  - `Point`: 포인트 사용 시 잔액 정합성 보장

#### 4. 트랜잭션 관리
- Service 계층에서 `@Transactional` 관리
- 읽기 전용: `@Transactional(readOnly = true)`
- 격리 수준: READ_COMMITTED (기본)

---

## 데이터베이스 스키마

### ERD 개요
> ERD는 obsidian 에서 확인

---

## 코딩 컨벤션

### Kotlin 스타일 가이드
[공식 Kotlin 코딩 컨벤션](https://kotlinlang.org/docs/coding-conventions.html) 준수

### 명명 규칙

#### 클래스/인터페이스
- **Entity**: 명사형, 단수 (예: `User`, `Point`)
- **Repository**: `{Entity}Repository` (예: `UserRepository`)
- **Service**: `{Entity}Service` (예: `PointService`)
- **Controller**: `{Entity}Controller` (예: `RouletteController`)
- **DTO**: `{Entity}{Action}Request/Response` (예: `UserCreateRequest`)

### 주석 규칙
- Public API에는 KDoc 작성
- 복잡한 비즈니스 로직에 설명 추가
- TODO, FIXME 태그 활용

### 서비스 계층 의존성 규칙

#### 도메인 간 의존성 관리
- **같은 도메인**: Repository를 직접 주입받아 사용
  - e.g. `PointService`는 `PointRepository`, `PointHistoryRepository` 직접 주입 가능
- **다른 도메인**: 반드시 Service를 통해 접근
  - e.g. `PointService`에서 User 정보 필요 시 `UserService` 주입받아 사용
  - 금지: 다른 도메인의 Repository 직접 주입 (`UserRepository` 등)

#### 목적
- 도메인 경계 명확화
- 도메인 로직의 응집도 향상
- 변경 영향 범위 최소화
- 순환 의존성 방지

---

## 테스트

### 테스트 전략

#### 1. 테스트 범위 및 목표
- **Unit Test**: 도메인 로직, 서비스 계층
- **Integration Test**: Repository, API 엔드포인트
- **Test Coverage**: 80% 이상 목표

#### 2. 테스트 프레임워크 및 도구
- **JUnit 5**: 테스트 실행 프레임워크
- **Mockito**: 모킹 라이브러리
- **MockMvc**: REST API 테스트 (Controller 계층)
- **TestContainers**: 실제 PostgreSQL 환경 테스트

---

### 테스트 구조

```
src/test/
├── kotlin/com.pointroulette/
│   ├── application/         # 응용 계층 테스트
│   │   ├── user/
│   │   │   └── UserServiceTest.kt       # Service 단위 테스트
│   │   └── point/
│   │
│   └── presentation/        # 표현 계층 테스트
│       └── user/
│           └── UserControllerTest.kt    # API 통합 테스트
│
└── resources/
    └── application-test.yaml            # 테스트 환경 설정
```

---

### 계층별 테스트 가이드

#### Application Layer 테스트

**목적**: Service 로직, Use Case 검증

**테스트 대상**:
- 비즈니스 로직 흐름
- 트랜잭션 처리
- 도메인 객체 조합
- 예외 처리

**테스트 환경**:
- `@ExtendWith(MockitoExtension::class)` 사용
- 의존성(Repository 등)은 Mock 객체로 주입
- 순수 단위 테스트 (외부 의존성 없음)

**테스트 작성 방식**:
- `@Mock`으로 의존성 모킹
- `@InjectMocks`로 테스트 대상 주입
- `given().willReturn()` 패턴으로 Mock 동작 정의
- `verify()`로 메서드 호출 검증
- **@Nested**로 각 메서드별 테스트 케이스 그룹화

---

#### Presentation Layer 테스트

**목적**: REST API 엔드포인트, 요청/응답 검증

**테스트 대상**:
- HTTP 요청/응답 처리
- 요청 유효성 검증 (Validation)
- 응답 포맷 확인
- 예외 처리 및 에러 응답

**테스트 환경**:
- `@SpringBootTest` + `@AutoConfigureMockMvc` 사용
- **MockMvc**로 HTTP 요청 시뮬레이션
- **TestContainers**로 실제 PostgreSQL 환경 구성
- 실제 Spring Context 로드 (통합 테스트)

**테스트 작성 방식**:
- `mockMvc.post()`, `mockMvc.get()` 등으로 요청 전송
- `andExpect()`로 상태 코드, 응답 본문 검증
- `jsonPath()`로 JSON 응답 필드 검증
- 각 테스트 전 데이터베이스 초기화 (`@BeforeEach`)
- **@Nested**로 각 API 엔드포인트별 테스트 그룹화

---

### 테스트 환경 설정

#### TestContainers 설정

**목적**: 실제 PostgreSQL 환경에서 테스트하여 프로덕션과 동일한 환경 보장

**적용 대상**:
- Repository 테스트 (`@DataJpaTest`)
- Controller 통합 테스트 (`@SpringBootTest`)

**설정 방법**:
1. `build.gradle`에 TestContainers 의존성 추가
2. 테스트 클래스에 `@Testcontainers` 어노테이션 추가
3. PostgreSQL 컨테이너 정의 및 자동 시작

**장점**:
- H2와 PostgreSQL의 SQL 방언 차이 문제 해결
- 실제 DB 제약조건 및 인덱스 동작 검증
- 프로덕션 환경과 일치하는 테스트

---

#### application-test.yaml

**위치**: `src/test/resources/application-test.yaml`

**목적**: 테스트 전용 프로퍼티 설정

**설정 내용**:
- TestContainers 데이터소스 설정
- JPA 로깅 레벨 조정 (SQL 쿼리 확인)
- 테스트용 트랜잭션 설정
- Hibernate DDL 설정 (create-drop)

**활성화 방법**:
- `@ActiveProfiles("test")` 어노테이션 사용
- 테스트 실행 시 자동으로 test 프로파일 활성화

---

### 테스트 작성 규칙

#### 1. 명명 규칙
- **테스트 클래스**: `{대상클래스명}Test.kt`
- **테스트 메서드**: Backtick 활용한 한글 설명
- **DisplayName**: 계층 및 기능 명시

#### 2. Given-When-Then 패턴 준수
- **Given**: 테스트 초기 상태 준비 (데이터 생성, Mock 설정)
- **When**: 테스트할 동작 수행 (메서드 호출, API 요청)
- **Then**: 결과 검증 (assert, verify)

#### 3. @Nested를 활용한 테스트 구조화
- 관련된 테스트를 논리적으로 그룹화
- 각 메서드별, API 엔드포인트별, 시나리오별로 구분
- 중첩 클래스 내부에서 공통 설정 공유 가능
- 테스트 리포트의 가독성 향상

#### 4. 테스트 독립성 보장
- 각 테스트는 다른 테스트에 영향을 주지 않아야 함
- `@BeforeEach`로 테스트 전 초기화
- 테스트용 데이터는 각 테스트 메서드 내에서 생성

#### 5. DisplayName 활용
- 테스트 클래스와 메서드에 `@DisplayName` 추가
- 테스트 의도를 명확히 표현
- 테스트 실패 시 원인 파악 용이

---

### 동시성 테스트 가이드

#### 목적
- 동시성 이슈 사전 검증 (Race Condition, 데이터 정합성)
- 낙관적 락, 비관적 락 등 동시성 제어 메커니즘 검증
- 중복 요청 방지 로직 검증

#### 테스트 대상
- **예산 차감**: 여러 사용자가 동시에 예산을 사용할 때 정합성 보장
- **포인트 사용**: 동시에 포인트를 사용할 때 잔액 정확성 보장
- **중복 참여 방지**: 같은 사용자의 동시 요청 시 한 번만 처리
- **재시도 메커니즘**: 낙관적 락 충돌 시 재시도 로직 검증

#### 테스트 환경 설정
- `@SpringBootTest`: 실제 Spring Context 로드 (통합 테스트)
- `@Testcontainers`: 실제 PostgreSQL 환경에서 동시성 검증
- `@ActiveProfiles("test")`: 테스트 프로파일 활성화

#### 동시성 테스트 도구
- **CountDownLatch**: 여러 스레드의 동시 실행 보장
- **ExecutorService**: 고정된 스레드 풀 관리
- **AtomicInteger**: 스레드 안전한 카운터 (성공/실패 집계)

#### 테스트 작성 패턴

**1. 스레드 풀 생성 및 동시 실행**
- `Executors.newFixedThreadPool()`로 고정 크기 스레드 풀 생성
- `CountDownLatch`로 모든 스레드가 작업 완료할 때까지 대기
- 각 스레드에서 테스트 대상 메서드 호출

**2. 결과 집계**
- `AtomicInteger`로 성공/실패 횟수 추적
- 스레드 안전한 방식으로 결과 누적

**3. 스레드 풀 정리**
- `executorService.shutdown()`: 새 작업 거부
- `awaitTermination()`: 실행 중인 작업 완료 대기 (타임아웃 설정 필수)

**4. 검증 항목**
- 성공한 작업 수와 실제 데이터 변경 수 일치
- 데이터 정합성 (예: 예산 차감 정확성)
- 예외 발생 횟수 (예: 중복 참여 예외)
- 최종 상태의 일관성 (예: 잔액이 음수가 아님)

#### 테스트 데이터 격리
- `@BeforeEach`에서 관련 테이블 전체 삭제 (외래키 순서 고려)
- 각 테스트마다 독립적인 데이터 생성
- 테스트 간 간섭 방지

#### 주의사항

**1. 타임아웃 설정**
- `latch.await(timeout, TimeUnit)`: 무한 대기 방지
- `awaitTermination(timeout, TimeUnit)`: 스레드 풀 종료 대기 시간 설정

**2. 예외 처리**
- 예상되는 예외(예: 예산 부족, 중복 참여)는 카운터로 집계
- 예상치 못한 예외는 로깅 또는 테스트 실패 처리

**3. 스레드 수 선택**
- 너무 많은 스레드는 테스트 시간 증가 및 불안정성 유발
- 동시성 이슈를 재현할 수 있는 적절한 수 선택 (보통 5~50개)
- 충돌 확률이 높은 시나리오는 적은 수로도 충분

**4. 테스트 안정성**
- 동시성 테스트는 타이밍에 민감하여 불안정할 수 있음
- 여러 번 실행하여 일관된 결과 확인
- CI/CD 환경에서도 안정적으로 실행되도록 타임아웃 여유 있게 설정

**5. 성능 vs 정확성**
- 동시성 테스트는 정확성 검증이 목적 (성능 테스트 아님)
- 스레드 수를 늘리는 것보다 정확한 검증 로직에 집중
