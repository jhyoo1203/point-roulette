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

---

## 테스트

### 테스트 전략
- **Unit Test**: 도메인 로직, 서비스 계층
- **Integration Test**: Repository, API 엔드포인트
- **Test Coverage**: 80% 이상 목표

---

## API 명세

> 상세 API 명세는 Swagger/OpenAPI 문서 참조
