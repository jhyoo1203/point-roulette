# AI 활용 프롬프트 기록

> **목적**: Point Roulette 프로젝트 개발 과정에서 AI와 협업한 전체 프롬프트 기록
>
> **AI 도구**: Claude Code, NotebookLM, Gemini

---

## 📋 목차

1. [Claude Code](#1-claude-code)
2. [NotebookLM](#2-notebooklm)
3. [Gemini](#3-gemini)
> 관점: [설계], [문제 고민과 해결], [생산성 향상]  

---

## 1. Claude Code

### [설계] ERD 초안 설계

**프롬프트**
```
obsidian에서 '2026 상반기 유저서비스스쿼드 과제' 문서를 바탕으로 백엔드 ERD 설계를 Mermaid로 부탁해.

아래 전략을 반영해줬으면 좋겠어.
1. JPA 연관관계 활용: 기존에는 유연성을 위해 논리적인 ID 참조를 선호하지만 이번에는 JPA 매핑을 활용하려고 해. Lazy Loading + fetch join을 활용할 예정이야.
2. 포인트 만료 관리: 단순히 총액(total_point)만 관리하면 유효기간 처리가 안 되잖아. 포인트를 획득한 건별로 만료일을 기록하고 사용할 때 오래된 순(FIFO)으로 차감할 수 있는 구조가 필요해.
3. 동시성 제어: 일일 예산 테이블은 트래픽이 몰릴 때 비관적 락을 걸어서 정합성을 맞추기 좋은 구조로 잡아줘.
4. 내가 생각한 초안: User, Order, Product, DailyBudget, RouletteHistory, Point, PointHistory 테이블
5. 추가 의견: Point 테이블에는 e.g. expires_at와 같은 만료 시간을 같이 입력해두는 필드가 있으면 좋겠어. 또한 created_at, updated_at 필드는 필수
```

**설명**
- obsidian mcp 활용
- ERD 초안 설계
- 이후 휴먼 검수하며 ERD 완성

---

### [설계] Backend 프로젝트 구조 개선

**프롬프트**
```
@backend/README.md 프로젝트 구조 이렇게 가져가면 어떨지 리뷰해줘. 파일은 생성하지 말고 텍스트로 설명

[대화 진행]
Q: 근데 이렇게 하면 나중에 application에 너무 많은 service가 있지 않을까?
A: [도메인별 패키지 분리 구조 제안 - 3가지 옵션]
  1. 도메인별로 application도 분리 (추천)
  2. Use Case 기반 세분화 (CQRS)
  3. 혼합 전략

Q: // 1단계: 도메인별 패키지 분리부터 시작
   이거만 적용해도 충분할 것 같아

Q: 이 하위에 application, infrastructure, presentation 등 위치하는건 어떨까
   (도메인 최상위 구조 제안)
A: [장단점 비교 및 현실적인 절충안 제시]

최종 결정: 현실적인 절충안 (추천)
- 계층 구조는 유지 (DIP 원칙)
- 도메인별 분리도 적용
- 공통 코드 위치 명확
- Spring Boot 관례 준수

그럼 이거로 @backend/README.md 다시 정리해줘.
```

**변경 내용**
```
기존 구조:
application/
├── service/
│   ├── UserService.kt
│   ├── PointService.kt
│   └── RouletteService.kt  # 모든 Service가 한 폴더에
└── dto/
    ├── UserDto.kt
    └── PointDto.kt         # 모든 DTO가 한 폴더에

개선된 구조:
application/
├── user/                   # 도메인별 분리
│   ├── UserService.kt
│   └── dto/
│       ├── UserDto.kt
│       ├── UserCreateDto.kt
│       └── UserUpdateDto.kt
├── point/
│   ├── PointService.kt
│   └── dto/
└── roulette/
    ├── RouletteService.kt
    └── dto/
```

**주요 개선 사항**
1. **도메인 중심 구조**
   - domain/, application/, presentation/ 각 계층 내에서 도메인별 패키지 분리
   - 예: `application/user/`, `application/point/`, `presentation/user/`

2. **공통 코드 관리**
   - `infrastructure/config/`: JpaConfig, SecurityConfig 등
   - `presentation/exception/`: GlobalExceptionHandler 등

3. **의존성 규칙 명확화**
   - Presentation → Application → Domain
   - Infrastructure는 Domain 인터페이스 구현

4. **테스트 구조도 동일하게 적용**
   - `test/kotlin/.../domain/user/`, `test/kotlin/.../application/user/`

**설계 원칙**
- 계층 간 의존성 규칙(DIP) 유지
- 도메인 경계 명확화로 검색 및 유지보수 용이
- 단순 CRUD는 Service로 충분, 복잡한 유스케이스만 나중에 UseCase 분리
- Spring Boot 관례 준수하면서 확장 가능한 구조

**장점**
- 도메인별로 코드가 그룹화되어 검색 용이
- Service 파일이 늘어나도 도메인별로 관리되어 혼란 없음
- 마이크로서비스 전환 시 도메인 단위로 분리 가능

---

### [생산성 향상] PostgreSQL DDL 생성 및 엔티티 매핑

**프롬프트**
```
obsidian에서 ERD 확인해서 postgreSQL 기반 DDL을 생성해줘.
1. @backend/src/main/resources/ 하위에 {DDL}.sql 형식으로 정리.
2. 테이블 + 그 테이블에 해당하는 유니크까지는 같은 sql 파일에 넣어줘.
3. enum 같은 경우는 TEXT 자료형 사용
4. db 마이그레이션은 사용하지 말고 수동 쿼리 날릴 예정. 기록용으로 SQL 파일 생성하는 것을 인지해줘.
5. DDL 생성 완료 이후 엔티티 매핑(repository, 비즈니스 메서드 생성 금지)
```

**설명**
- 데드라인 안에 mvp를 빠르게 뽑는 것이 목적이기 때문에 외부 의존성을 사용하기보다는 DDL 버전 관리만 해두고 DB 마이그레이션 툴을 사용하지 않기 위함
- 보일러플레이트 코드를 AI Agent를 통해 빠르게 생성 후 휴먼 검수

---

### [생산성 향상] 테스트 전략 문서화 및 Claude Code 스킬 생성

**프롬프트**
```
테스트 코드를 추가하는 Claude Code 커스텀 명령어를 만들고 싶어.
- 커스텀 명령어 추가 (FE도 추가하기 쉽게 확장성 있는 문서 작성)
- [backend/README.md 테스트 섹션 추가]
   1. 코드 예시는 넣지 않고 텍스트로 정리
   2. controller는 mockMvc 사용 명시
   3. test container 사용 + test 프로퍼티 만들기 명시
   4. @Nested도 명시
- 로그인 API 테스트 코드 작성으로 Claude Code 스킬 테스트 검증
```

**설명**
- 테스트 전략 문서화
- Claude Code 스킬 생성
- 이후 테스트 코드 작성 시 스킬 활용

---

### [생산성 향상] 로그인 API 테스트 코드 작성

**프롬프트**
```
/test 스킬을 활용하여 백엔드 로그인 API 테스트 코드 작성
```

**설명**
- Claude Code의 `/test` 스킬 활용
- `backend/README.md`의 테스트 전략 참고

---

### [설계] + [문제 고민과 해결] admin 일일 예산 로직 구현 전 설계에 대한 고민 및 해결

**프롬프트**
```
admin 일일 예산 조회/설정 로직을 구현한다.
- 날짜 범위로 일일 예산 등록(100,000p) 고정
- bulk insert(e.g. 1년치를 한꺼번에 insert 한다면 db latency 부담 우려)
- 일일 예산 조회 admin api 구현(날짜 범위로 검색)

obsidian에서 "2026 상반기 유저서비스스쿼드 과제.md" 문서 확인해서 현재 구현 계획 어떨지 리뷰해줘.

[대화 진행]
Q: IDENTITY 전략의 batch insert 미동작할 것으로 예상하는데 SEQUENCE 전략으로 변경 필요하지 않아?
A: [IDENTITY 전략의 batch insert 미동작 문제 지적]
   - GenerationType.IDENTITY는 batch insert가 동작하지 않음
   - SEQUENCE 전략으로 변경 필요
   - allocationSize 설정으로 시퀀스 캐싱 최적화

Q: 모든 엔티티를 sequence 방식으로 변경할지, 예산만 sequence 방식으로 적용할지 검토해줘.
A: [2가지 전략 비교 및 엔티티별 분석]
   전략 1: DailyBudget만 SEQUENCE (최소 변경)
   전략 2: 모든 엔티티 SEQUENCE (완전 통일)
```

**설명**
- 설계한 구조대로 진행
- 상단에서 서술한 sequence 전략은 2번 방식 선택
- 구현 전 고민되던 부분들 질문 및 해결

---

### [문제 고민과 해결] 룰렛 참여 서비스 코드 리뷰 및 동시성 제어 개선

**프롬프트**
```
지금 커밋하지 않은 변경사항들 코드리뷰 해줘. 이것들 중점으로 리뷰해주면 좋겠어.
1. fetch join 사용하면 좋을 포인트
2. 동시성 제어가 되었는가?
3. 개선할 점

[리뷰 후 대화 진행]
Q: 1. 추후 확장 시 수정
   2. RouletteService에서 retry를 붙혀놓는건 어떨까?
   3.1. 트랜잭션 전파 설정은 기본값으로 되어있어서 롤백 가능
   3.2. 예산 부족은 "미참여"로 간주할 거라서 현재 로직 유지
   3.3. AOP 기반으로 모두 로깅하는 중
   3.4. 수정 필요
   3.5. 사용 중이므로 이 리뷰는 무시

A: [RouletteService에 @Retryable 적용 검토]
   - 권장하지 않음 (멱등성 위반 우려)
   - 랜덤 포인트 생성이 재시도마다 달라지는 문제
   - DailyBudgetService.deductBudget에만 재시도 유지하는 것이 적합

작업 진행
```

**설명**
- 룰렛 참여 서비스 코드 리뷰 (fetch join, 동시성 제어, 개선점 중점)
- 오늘 룰렛 참여 여부 체크와 저장 쿼리 사이 시간차로 동시 요청 시 unique constraint 위반 가능 문제 파악 및 해결
- RouletteRandomUtil을 인터페이스(RandomPointGenerator) 기반으로 리팩토링하여 테스트 용이성 개선
- @Retryable 적용 범위 논의: 멱등성 보장을 위해 DailyBudgetService.deductBudget에만 적용하는 것이 적합

---

### [생산성 향상] 동시성 테스트 컨벤션 문서화

**프롬프트**
```
@backend/src/test/kotlin/com/pointroulette/application/roulette/RouletteConcurrencyTest.kt

이 테스트 확인해서 @backend/README.md 에 동시성 테스트에 관한 컨벤션도 적립해줘.
더 나은 방식이 있으면 그 방식으로 수정. 문서 작성 시 코드 예시는 금지.
```

**설명**
- 동시성 테스트 코드 개선 (`awaitTermination()` 추가로 스레드 풀 정리 강화)
- backend/README.md에 "동시성 테스트 가이드" 섹션 추가
   - 테스트 목적, 대상, 환경 설정
   - 테스트 도구 (CountDownLatch, ExecutorService, AtomicInteger)
   - 테스트 작성 패턴 (스레드 풀 생성 → 동시 실행 → 결과 집계 → 검증)
   - 주의사항 (타임아웃, 예외 처리, 스레드 수, 안정성, 성능 vs 정확성)
- 코드 예시 없이 개념과 가이드라인으로만 문서화

---

### [설계] Admin Web 프로젝트 아키텍처 및 컨벤션 문서화

**프롬프트**
```
obsidian에서 '웹 어드민 테크스펙', '2026 상반기 유저서비스스쿼드 과제' 문서 확인해서
admin-web 모듈의 README.md 문서를 보강해줘.
@backend/README.md 처럼 아키텍쳐 구조랑 컨벤션 등 위주로 작성해.
컨벤션 및 성능 규칙에 대해서는 /react-best-practices Skills를 간단하게 작성하거나,
참조만 하는 식으로 해줘. 코드 예시는 작성 금지.
```

**설명**
- Obsidian MCP에서 '웹 어드민 테크스펙', '2026 상반기 유저서비스스쿼드 과제' 문서 참조
- backend/README.md의 구조(기술 스택 → 프로젝트 구조 → 아키텍처 → 컨벤션 → 테스트 → 빌드)를 기반으로 admin-web README.md 작성
- Vercel의 react-best-practices Skills 참조

---

### [설계] Admin Web 개발 순서 수립

**프롬프트**
```
@admin-web/README.md이랑, obsidian에서 '2026 상반기 유저서비스스쿼드 과제', '웹 어드민 테크스펙' 문서 확인해서
어드민 웹 개발 순서를 어떻게 가져가면 좋을지 제안해줘.

[답변 이후]
이 내용 obsidian vault에 기록해줘.
```

**결과: 개발 순서 제안**
```
Phase 0 (공통 인프라)
  → Phase 1 (상품 CRUD — 패턴 확립)
    → Phase 2 (예산/룰렛 — 핵심 비즈니스)
      → Phase 3 (주문 — 패턴 재활용)
        → Phase 4 (대시보드 — 집계)
          → Phase 5 (폴리싱)
```

- **Phase 0: 공통 인프라** — API Client, TanStack Query Provider, Query Keys, Layout, Router, 공통 타입
- **Phase 1: 상품 관리** — 가장 전형적인 CRUD로 테이블/폼/모달/useMutation 패턴 확립
- **Phase 2: 예산/룰렛 관리** — 핵심 비즈니스 로직, Double-Lock 패턴 구현
- **Phase 3: 주문 내역** — Phase 1~2 패턴 재활용으로 빠르게 구현
- **Phase 4: 대시보드** — 다른 도메인 데이터 집계, 백엔드 API 준비 후 마지막에 구현
- **Phase 5: 마무리** — Error Boundary, Toast, Loading State, 엣지 케이스

**설명**
- admin-web/README.md, 과제 문서, 웹 어드민 테크스펙 3개 문서를 종합 분석
- Phase 1에서 패턴을 확립하면 이후 Phase는 복사-변형 수준으로 가속 가능
- 대시보드를 마지막에 배치하여 리스크 최소화 (시간 부족 시 간소화 가능)

---

### [생산성 향상] Admin Web 공통 인프라 설정 (Phase 0)

**프롬프트**
```
GitHub Issue #35의 요구사항에 따라 admin-web의 공통 인프라를 설정.
현재 admin-web은 Vite + React 초기 템플릿 상태이며, 핵심 라이브러리 미설치 상태.
Apidog에서 확인한 API 응답 구조를 기반으로 타입 정의.

[답변] 
구현 계획을 plan mode로 설계 후 실행:
1단계: 의존성 설치 및 기본 설정 (Tailwind CSS, shadcn/ui, path alias)
2단계: 공통 타입 정의 (Apidog 스펙 기반)
3단계: API Client 설정 (Axios 인스턴스 + 인터셉터)
4단계: TanStack Query Provider 설정
5단계: Query Keys 중앙 관리
6단계: React Router 라우팅
7단계: 공통 Layout 구현 (Sidebar + Header)
```

**설명**
- Phase 0 공통 인프라 전체 구현 (개발 순서 수립에서 정의한 범위)
- Apidog MCP에서 ResponseData, PaginationResponse 스키마를 확인하여 타입 정의
- Plan mode로 구현 계획을 사전 설계 후 승인 후 실행
- 생성/수정 파일 19개, `pnpm build` 타입 에러 없이 성공 확인

**주요 구현 내용**
1. **의존성**: axios, @tanstack/react-query, react-router-dom, tailwindcss, @tailwindcss/vite, shadcn/ui
2. **설정**: path alias (`@/*`), Tailwind CSS v4, shadcn/ui (`src/shared/components/ui/`)
3. **공통 타입**: `ApiResponse<T>`, `ApiErrorResponse`, `PaginatedData<T>`, `PaginationParams`, `ErrorCode`
4. **API Client**: Axios 인스턴스 + 응답/에러 인터셉터 + 타입 안전 헬퍼 함수 (`apiGet`, `apiPost`, `apiPut`, `apiDelete`)
5. **TanStack Query**: QueryClient (staleTime: 5s, refetchOnWindowFocus), 중앙 관리 queryKeys
6. **라우팅**: BrowserRouter + AdminLayout 내 4개 라우트 (dashboard, budget, products, orders)
7. **레이아웃**: Sidebar (NavLink active 상태, 모바일 반응형) + Header (모바일 토글) + Outlet

---

## 2. NotebookLM

### [설계] 웹 어드민 테크스펙 생성

**소스**
- `2026 상반기 유저서비스스쿼드 과제.md`

**프롬프트**
```
제공된 과제 파일을 심층 분석해서 포인트 룰렛 서비스의 안정적인 운영을 보장하는
웹 어드민 (React) 테크 스펙을 작성해줘.
서론에서는 핵심 요약을 제시하고 본론에서는 다음 항목들을 객관적이고 분석적인 어조로
상세히 기술해줘.
첫째 시스템 아키텍처 섹션에서는 짧은 과제 기한을 고려한 React 기반의 프론트엔드
전략과 실시간 데이터 동기화를 위한 상태 관리 방안을 제안해줘. 상세 스펙은 문서 참고해.
둘째 기능 명세 섹션에서는 카테고리 별로 기능을 나열해줘.
마지막 섹션에서는 주의할 점과 고려해야할 점에 대해 작성해줘.
문서 전체적으로 백엔드에 관한 내용은 작성하지 않았으면 좋겠어.
```

**설명**
- NotebookLM에 과제 문서를 소스로 업로드 후 테크스펙 보고서 생성
- 생성된 '웹 어드민 테크스펙' 문서를 이후 admin-web README.md 작성 시 참고 자료로 활용

---

## 3. Gemini

### [생산성 향상] 포인트 룰렛 앱 아이콘 생성

**프롬프트**
```
포인트 룰렛 어플리케이션 아이콘 이미지 생성해줘.
1. 글자는 x
2. 컴팩트한 디자인
3. primary color는 파란색 or 빨간색(아직 색상 코드까지 정하지는 않은 상태)
4. 비율은 1:1
5. 여백은 최소화
```

**설명**
- Gemini Nano Banana 기능 활용
- 다양한 시안 생성 후 선택
