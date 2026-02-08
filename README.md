# Point Roulette

> 매일 룰렛을 돌려 포인트를 획득하고, 획득한 포인트로 상품을 구매하는 포인트 기반 이벤트 서비스

---

## 배포 URL

| 서비스 | URL                                                              |
|--------|------------------------------------------------------------------|
| 사용자 웹 | `https://point-roulette-web.vercel.app`                          |
| 어드민 웹 | `https://point-roulette-admin.vercel.app`                        |
| 백엔드 API Swagger | `https://point-roulette-api.up.railway.app/swagger-ui/index.html` |
| Flutter 앱 | 구글 폼으로 제출 |


---

## 프로젝트 구조

```
point-roulette/
├── backend/                # Spring Boot 백엔드 API 서버
├── admin-web/              # 관리자 웹 (React + Vite)
├── user-web/               # 사용자 웹 (Next.js)
├── flutter-app/            # Flutter 모바일 앱 (WebView)
├── .github/workflows/      # CI/CD 파이프라인
├── docker-compose.yml      # 로컬 개발 환경 (PostgreSQL)
├── CLAUDE.md               # AI 컨텍스트 가이드
└── PROMPT.md               # AI 활용 리포트
```

---

## 1. Backend

**경로**: [`backend/`](./backend/)

### 아키텍처
Layered Architecture (Presentation → Application → Domain ← Infrastructure)

```
com.pointroulette/
├── presentation/    # REST Controller, Request/Response DTO
├── application/     # Service, Use Case, Application DTO
├── domain/          # Entity, Repository Interface, Domain Logic
├── infrastructure/  # Config, Repository 구현체
└── common/          # 공통 유틸리티
```

### 주요 API

| 분류 | 기능 |
|------|------|
| **인증** | 닉네임 간편 로그인 |
| **룰렛** | 참여 (1일 1회, 100~1000p), 참여 여부 확인, 잔여 예산 조회 |
| **포인트** | 잔액 조회, 포인트 내역 (만료 상태 포함), 7일 내 만료 예정 조회 |
| **상품** | 목록 조회 |
| **주문** | 상품 주문 (포인트 차감), 주문 내역 조회 |
| **어드민** | 예산 설정/조회, 상품 CRUD, 주문 취소 (환불), 룰렛 참여 취소 (회수) |

### 동시성 제어
- **일일 예산 차감 — 낙관적 락**
    - 단일 행 대상이고 1일 1회 제약 조건이 걸려있기 때문에 충돌 빈도가 낮다고 생각했습니다.
    - 따라서 락 대기 없이 처리량을 확보하였고 충돌 시 Spring Retry 3회 재시도 하였습니다.
- **포인트 차감 — 비관적 락**
    - 여러 행을 FIFO 순서로 순차 차감하는 구조이므로, 낙관적 락 사용 시 부분 차감 롤백과 재시도 비용이 과도하다고 판단했습니다.
    - 따라서 비관적 락으로 트랜잭션 단위의 정합성을 보장하였습니다.
- **중복 참여 방지**: 유니크 제약 + 트랜잭션 제어

---

## 2. Admin Web

**경로**: [`admin-web/`](./admin-web/)

### 주요 화면

| 화면 | 기능 |
|------|------|
| **대시보드** | 오늘 예산 현황, 참여자 수, 지급 포인트 |
| **예산 관리** | 일일 예산 설정/조회, 룰렛 참여 취소 (포인트 회수) |
| **상품 관리** | 상품 CRUD, 재고 관리 |
| **주문 내역** | 주문 목록, 주문 취소 (포인트 환불) |

---

## 3. User Web

**경로**: [`user-web/`](./user-web/)

### 주요 화면

| 화면 | 기능 |
|------|------|
| **로그인** | 닉네임 입력 (간편 로그인) |
| **홈 (룰렛)** | 룰렛 UI + 애니메이션, 잔여 예산 표시 |
| **내 포인트** | 포인트 목록 (유효기간, 만료 상태), 7일 내 만료 예정 알림 |
| **상품 목록** | 구매 가능 상품, 포인트 잔액 기반 구매 가능 여부 |
| **주문 내역** | 내 주문 목록 |

---

## 4. Flutter App

**경로**: [`flutter-app/`](./flutter-app/)

### 주요 기능
- User Web을 WebView로 렌더링 (iOS/Android)
- 뒤로가기 처리 (Android 물리 버튼 대응)
- 로그인 상태 유지
- 커스텀 앱 아이콘 & 앱 이름
- 네이티브 스플래시 스크린
- 네트워크 에러 처리 (커스텀 에러 페이지 + 재시도 버튼)
- WebView 로딩 인디케이터

---

## AI 활용 리포트

개발 과정에서의 AI 협업 경험은 [`PROMPT.md`](./PROMPT.md)에 기록되어 있습니다.

### 관점별 요약

1. **설계**: 프로젝트 아키텍처 설계, 데이터베이스 스키마 설계, API 설계 시 AI와 협업
2. **문제 고민과 해결**: 동시성 제어, 포인트 만료 관리 등 핵심 로직 구현 시 AI 활용
3. **생산성 향상**: 반복적인 CRUD 코드 생성, 테스트 코드 작성, 설정 파일 구성 등에 AI 활용
