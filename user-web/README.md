# Point Roulette User Web

> 포인트 룰렛 서비스 사용자 웹 애플리케이션 — 일일 룰렛 참여, 포인트 획득, 상품 구매

---

## 목차

1. [기술 스택](#-기술-스택)
2. [프로젝트 구조](#-프로젝트-구조)
3. [아키텍처](#-아키텍처)
4. [코딩 컨벤션](#-코딩-컨벤션)
5. [테스트](#-테스트)
6. [빌드 및 실행](#-빌드-및-실행)

---

## 기술 스택

### Core
- **Language**: TypeScript 5.9+
- **Framework**: Next.js 15+ (App Router)
- **Package Manager**: pnpm
- **Runtime**: Node.js 20+

### UI
- **Styling**: Tailwind CSS
- **Icons**: Lucide React
- **Animations**: Framer Motion (룰렛 애니메이션)

### 상태 관리 & 데이터 페칭
- **Server State**: TanStack Query (React Query)
- **Client State**: React Context 또는 Zustand (최소한으로 사용)
- **Server Actions**: Next.js Server Actions (인증, 룰렛 참여 검증)

### 개발 도구
- **Lint**: ESLint (typescript-eslint, next)
- **Formatter**: Prettier
- **Build**: Next.js Compiler (Turbopack)

---

## 프로젝트 구조

```
user-web/
├── src/
│   ├── app/                         # Next.js App Router
│   │   ├── layout.tsx               # 루트 레이아웃 (Provider, Metadata)
│   │   ├── page.tsx                 # 홈 (로그인 후 리다이렉트)
│   │   │
│   │   ├── (auth)/                  # 인증 라우트 그룹
│   │   │   └── login/
│   │   │       └── page.tsx         # 로그인 페이지 (닉네임 입력)
│   │   │
│   │   ├── (main)/                  # 메인 앱 라우트 그룹 (인증 필요)
│   │   │   ├── layout.tsx           # 공통 레이아웃 (네비게이션)
│   │   │   ├── roulette/            # 룰렛 페이지
│   │   │   ├── points/              # 내 포인트 페이지
│   │   │   ├── products/            # 상품 목록 페이지
│   │   │   └── orders/              # 주문 내역 페이지
│   │   │
│   │   └── api/                     # API Routes (필요 시)
│   │
│   ├── features/                    # 기능 단위 모듈 (도메인별)
│   │   ├── auth/
│   │   │   ├── components/          # 로그인 폼
│   │   │   ├── hooks/               # useLogin 등
│   │   │   └── types.ts
│   │   ├── roulette/
│   │   │   ├── components/          # 룰렛 UI, 애니메이션
│   │   │   ├── hooks/               # useRouletteSpin 등
│   │   │   ├── actions.ts           # Server Actions
│   │   │   └── types.ts
│   │   ├── points/
│   │   │   ├── components/          # 포인트 타임라인
│   │   │   ├── hooks/               # usePoints 등
│   │   │   └── types.ts
│   │   ├── products/
│   │   │   ├── components/          # 상품 카드
│   │   │   ├── hooks/               # useProducts 등
│   │   │   └── types.ts
│   │   └── orders/
│   │       ├── components/          # 주문 목록
│   │       ├── hooks/               # useOrders 등
│   │       └── types.ts
│   │
│   ├── shared/                      # 공통 모듈
│   │   ├── components/              # 공통 UI 컴포넌트 (Button, Modal 등)
│   │   ├── hooks/                   # 공통 훅
│   │   ├── lib/                     # 유틸리티, API 클라이언트 설정
│   │   ├── types/                   # 공통 타입 정의
│   │   └── constants/               # 상수
│   │
│   ├── middleware.ts                # Next.js Middleware (인증 Guard)
│   └── assets/                      # 정적 리소스
│
├── public/
├── next.config.ts
├── tailwind.config.ts
├── tsconfig.json
├── eslint.config.js
└── package.json
```

### 디렉토리별 역할

#### app (페이지 라우팅)
- **역할**: Next.js App Router 기반 페이지 라우팅
- **특징**:
  - Route Groups `(auth)`, `(main)`으로 레이아웃 분리
  - Server Components 기본, Client Components는 `"use client"` 명시
  - Server Actions을 통한 서버 로직 처리
- **구성**: `layout.tsx`, `page.tsx`, `loading.tsx`, `error.tsx`

#### features (기능 도메인 계층)
- **역할**: 도메인별 컴포넌트, 훅, Server Actions, 타입을 응집하여 관리
- **특징**: 각 feature는 독립적으로 동작하며, feature 간 직접 import 지양
- **구성**: 도메인별로 `components/`, `hooks/`, `actions.ts`, `types.ts` 분리

#### shared (공통 계층)
- **역할**: 2개 이상의 feature에서 공유하는 코드
- **특징**: 도메인에 종속되지 않는 범용 코드만 배치
- **구성**: `components/` (공통 UI), `hooks/` (공통 훅), `lib/` (API 클라이언트, 유틸리티), `types/` (공통 타입)

### 설계 원칙

#### 1. Feature 기반 구조
- 도메인(인증, 룰렛, 포인트, 상품, 주문)별로 코드를 응집
- feature 간 의존성을 최소화하여 독립적 개발 및 유지보수 가능

#### 2. 의존성 방향
```
app → features → shared
```
- app은 features를 조합
- features는 shared만 참조
- shared는 외부 라이브러리만 의존
- feature 간 직접 참조 금지 (필요 시 shared로 추출)

#### 3. Server/Client Components 분리
- **Server Components**: 데이터 페칭, 초기 상태 설정 (기본값)
- **Client Components**: 인터랙션, 애니메이션, 상태 관리 (`"use client"` 명시)
- Server Actions를 통한 서버 로직 처리로 API 라우트 최소화

---

## 아키텍처

### 데이터 흐름

```
┌─────────────────────────────────────────────┐
│            Next.js App Router Pages          │  ← 라우팅, Server Components
├─────────────────────────────────────────────┤
│          Server Actions (actions.ts)         │  ← 서버 로직, 인증 검증
├─────────────────────────────────────────────┤
│          Client Components (features)        │  ← 도메인 UI, 이벤트 핸들링
├─────────────────────────────────────────────┤
│        Custom Hooks (TanStack Query)         │  ← API 호출, 서버 상태 관리
├─────────────────────────────────────────────┤
│           API Client (Fetch/Axios)           │  ← HTTP 통신
├─────────────────────────────────────────────┤
│            Backend REST API                  │  ← Spring Boot 서버
└─────────────────────────────────────────────┘
```

### 주요 설계 결정

#### 1. Next.js App Router 전략

**Server Components 우선**
- 초기 페이지 로드 시 Server Components에서 데이터 페칭
- 서버에서 HTML 생성하여 빠른 FCP(First Contentful Paint)
- SEO 최적화 (메타데이터)

**Client Components 최소화**
- 인터랙션이 필요한 부분만 `"use client"` 적용
- 룰렛 애니메이션, 폼 입력, 모달 등

**Server Actions 활용**
- 룰렛 참여 검증, 상품 구매 등 서버 로직을 Server Actions로 구현
- API 라우트 대신 Server Actions로 백엔드 호출
- 간단한 로직은 Server Actions, 복잡한 로직은 API Routes 사용

#### 2. 인증 및 세션 관리 (Mock)

**로그인 Mock 전략**
- 닉네임만 입력받아 백엔드 로그인 API 호출
- 백엔드에서 받은 userId를 Cookie에 저장

**간단한 세션 관리**
- Cookie에 userId만 저장 (Flutter WebView 호환)
- API 요청 시 Cookie의 userId를 헤더에 포함
- 로그아웃 시 Cookie 삭제

#### 3. TanStack Query 기반 서버 상태 관리

**Query Key 체계**: 도메인과 액션을 명확히 구분
- `['roulette', 'status']` — 오늘 참여 여부, 잔여 예산
- `['points', 'balance']` — 내 포인트 잔액
- `['points', 'list']` — 포인트 내역
- `['products', 'list']` — 상품 목록
- `['orders', 'list', { page }]` — 주문 내역 (페이지네이션)

**Stale/Cache 전략**:
- 룰렛 상태: `staleTime: 0` — 항상 최신 상태 유지 (예산 실시간 반영)
- 포인트 잔액: `staleTime: 5000` (5초) — 빈번한 갱신 필요
- 상품 목록: `staleTime: 60000` (1분) — 변경 빈도 낮음
- 주문 내역: `staleTime: 30000` (30초)

**자동 갱신**: `refetchOnWindowFocus: true` 로 탭 전환 시 최신 데이터 보장

**캐시 무효화**: 룰렛 참여, 상품 구매 등 Mutation 성공 후 `invalidateQueries` 필수 호출

**Optimistic Updates**: 상품 구매 시 낙관적 업데이트로 즉각적인 UI 피드백

#### 4. 룰렛 핵심 로직

**1일 1회 참여 제약 조건**
1. 페이지 로드 시 Server Component에서 오늘 참여 여부 조회
2. 참여 완료 시 룰렛 버튼 비활성화 (`disabled`)
3. 클라이언트에서 스핀 시도 시 Server Action으로 재검증

**중복 참여 방지 (Race Condition)**
- 버튼 `Debouncing` (300ms)
- 요청 진행 중 전역 Loading Overlay
- Server Action에서 트랜잭션 기반 중복 참여 차단

**예산 소진 처리**
- 스핀 시작 전 잔여 예산 확인
- 백엔드에서 "예산 소진" 에러 반환 시 즉시 팝업 표시
- 애니메이션은 "꽝" 위치에 멈춤

**Spin Animation**
- CSS `transform: rotate()` 애니메이션
- 백엔드 결과(100p~1000p)에 따라 최종 각도 동적 계산
- Easing 함수로 자연스러운 감속 효과

#### 5. 포인트 타임라인 및 유효기간 관리

**포인트 상태**
- **유효**: 획득일 + 30일 이내
- **만료 예정**: 7일 이내 만료 (경고 표시)
- **만료됨**: 30일 경과 (회색 처리)

**트랜잭션 타입**
- `EARN`: 룰렛 당첨
- `USE`: 상품 구매
- `RECOVERY`: 주문 취소로 인한 포인트 복구
- `WITHDRAWAL`: 어드민에 의한 포인트 회수

**UI 시각화**
- 타임라인 형태로 포인트 이력 표시
- 만료 예정 포인트는 상단에 알림 배지로 강조

#### 6. 파괴적 작업 안전장치 (Double-Lock)

상품 구매 등 포인트 차감 액션에는 **2단계 확인 UX** 적용:
1. 구매 버튼 클릭 → 확인 모달 표시 (차감 포인트, 잔액 안내)
2. 모달 내 최종 확인 → API 호출 실행

#### 7. 전역 에러 처리

- **Error Boundary**: 예상치 못한 렌더링 에러 포착 및 폴백 UI
- **Toast 알림**: API 실패 시 사용자에게 즉시 피드백
- **Loading State**: 모든 데이터 요청 중 스켈레톤/스피너 표시 → 중복 클릭 방지

#### 8. Flutter WebView 최적화

**고정 뷰포트**
- `h-screen overflow-hidden` 레이아웃으로 스크롤 바운스 방지
- 모든 인터랙션 요소 터치 대상 44px 이상

**네이티브 Back Button 처리**
- Next.js Router와 Flutter 간 브릿지 통신
- WebView 히스토리 있으면 뒤로가기, 없으면 앱 종료

**Loading Indicators**
- WebView `onPageStarted`/`onPageFinished` 이벤트 활용
- 페이지 전환 시 네이티브 Spinner 표시

**세션 유지**
- Cookie 기반 세션이 Flutter WebView에서도 유지됨
- `SameSite=Lax` 설정으로 호환성 확보

---

## 코딩 컨벤션

### TypeScript 스타일

- **strict 모드** 활성화 (tsconfig `strict: true`)
- `any` 사용 금지 — 포인트, 금액 등 재무 데이터는 반드시 명시적 타입 정의
- **Interface** vs **Type**: API 응답/요청 스키마는 `interface`, 유니온/유틸리티 타입은 `type`

### 명명 규칙

| 대상 | 규칙 | 예시 |
|------|------|------|
| **컴포넌트** | PascalCase | `RouletteWheel`, `ProductCard` |
| **컴포넌트 파일** | PascalCase.tsx | `RouletteWheel.tsx` |
| **Hook** | camelCase (`use` 접두사) | `useRouletteSpin`, `usePoints` |
| **Hook 파일** | camelCase.ts | `useRouletteSpin.ts` |
| **Server Actions** | camelCase | `spinRouletteAction`, `purchaseProductAction` |
| **Server Actions 파일** | actions.ts | `features/roulette/actions.ts` |
| **유틸/상수 파일** | camelCase.ts | `formatPoint.ts`, `queryKeys.ts` |
| **타입 파일** | camelCase.ts | `types.ts` |
| **디렉토리** | kebab-case 또는 camelCase | `components/`, `hooks/` |
| **이벤트 핸들러** | `handle` + 동사 | `handleSpin`, `handlePurchase` |
| **Props 콜백** | `on` + 동사 | `onSpin`, `onPurchase` |

### 컴포넌트 작성 규칙

- **함수형 컴포넌트**만 사용 (클래스 컴포넌트 금지)
- Props는 `interface`로 정의하고, 컴포넌트와 같은 파일 또는 `types.ts`에 배치
- 한 파일에 하나의 export 컴포넌트 원칙 (헬퍼 컴포넌트는 같은 파일 내 비공개)

**Server vs Client Components**
- Server Components: 기본값, `"use client"` 없음
- Client Components: `"use client"` 파일 최상단 명시
- Server Components에서 Client Components import 가능 (역은 불가)

### 성능 및 React 패턴

> 상세 규칙은 **`/react-best-practices`** Skill을 참조.

주요 적용 항목:
- **리렌더 최적화**: 파생 상태 계산, 함수형 setState, `memo` 적절한 사용
- **번들 최적화**: 동적 import를 통한 코드 스플리팅, barrel import 지양
- **비동기 처리**: 병렬 fetch, Suspense 경계 활용
- **클라이언트 데이터 페칭**: SWR 중복 제거 패턴, TanStack Query 캐시 전략
- **Next.js 최적화**: Server Components 우선, Client Components 최소화

### API 연동 규칙

- API 호출은 반드시 **Custom Hook** 내부에서 TanStack Query를 통해 수행
- 컴포넌트에서 직접 `fetch`/`axios` 호출 금지 (Server Actions 또는 Custom Hook 사용)
- Query Key는 `shared/lib/queryKeys.ts`에서 중앙 관리
- Mutation 성공 후 관련 Query 캐시 `invalidateQueries` 필수

### Server Actions 규칙

- 파일명: `actions.ts` (각 feature 디렉토리 내)
- 함수명: `{동작}Action` 형식 (예: `spinRouletteAction`, `purchaseProductAction`)
- `"use server"` 지시어 필수
- 에러 처리: `try-catch`로 에러 객체 반환
- 반환값: `{ success: boolean, data?: T, error?: string }` 형식 권장
- Cookie에서 userId를 읽어 백엔드 API 호출 시 전달

---

## 테스트

### 테스트 전략

#### 1. 테스트 범위 및 목표
- **Unit Test**: Custom Hook, 유틸리티 함수, Server Actions
- **Component Test**: 주요 Feature 컴포넌트의 렌더링 및 인터랙션
- **Integration Test**: 페이지 단위 API 연동 시나리오
- **E2E Test**: 룰렛 참여 → 포인트 획득 → 상품 구매 플로우

#### 2. 테스트 프레임워크 및 도구
- **Vitest**: 테스트 러너 (Next.js 호환)
- **React Testing Library**: 컴포넌트 렌더링 및 DOM 상호작용
- **MSW (Mock Service Worker)**: API 모킹
- **Playwright**: E2E 테스트 (선택)

### 테스트 구조

```
src/
├── features/
│   ├── roulette/
│   │   ├── hooks/
│   │   │   ├── useRouletteSpin.ts
│   │   │   └── useRouletteSpin.test.ts        # Hook 테스트
│   │   ├── components/
│   │   │   ├── RouletteWheel.tsx
│   │   │   └── RouletteWheel.test.tsx         # 컴포넌트 테스트
│   │   ├── actions.ts
│   │   └── actions.test.ts                    # Server Actions 테스트
│   └── ...
│
├── shared/
│   └── lib/
│       ├── formatPoint.ts
│       └── formatPoint.test.ts                # 유틸리티 테스트
│
└── __tests__/
    ├── setup.ts                               # 테스트 환경 설정
    ├── mocks/                                 # MSW 핸들러
    │   └── handlers.ts
    └── e2e/                                   # E2E 테스트
        └── roulette-flow.spec.ts
```

### 테스트 작성 규칙

#### 1. 명명 규칙
- 테스트 파일: `{대상파일명}.test.ts(x)` — 대상 파일과 같은 디렉토리에 배치
- 테스트 블록: `describe`로 기능 단위 그룹화, `it`으로 개별 케이스 기술

#### 2. Arrange-Act-Assert 패턴 준수
- **Arrange**: 컴포넌트 렌더링, Mock 데이터 준비
- **Act**: 사용자 인터랙션 수행 (클릭, 입력 등)
- **Assert**: 기대 결과 검증 (DOM 상태, API 호출 등)

#### 3. 사용자 관점 테스트
- 구현 세부사항이 아닌 **사용자가 보고 상호작용하는 요소** 기준으로 테스트
- `getByRole`, `getByText` 등 접근성 기반 쿼리 우선 사용
- `data-testid`는 최후의 수단으로만 사용

#### 4. Server Actions 테스트
- Mock 함수로 백엔드 응답 시뮬레이션
- 성공/실패 케이스 모두 테스트
- userId 전달 여부 확인

---

## 주요 페이지 및 기능

### 1. 로그인 (`/login`)
- 닉네임만 입력 (간단한 Mock)
- 백엔드 로그인 API 호출하여 userId 받기
- Cookie에 userId 저장
- 로그인 후 `/roulette`로 리다이렉트

### 2. 룰렛 (`/roulette`)
- 오늘 참여 여부 확인
- 잔여 예산 표시
- 룰렛 스핀 애니메이션
- 당첨 결과 표시 (100p ~ 1000p)
- 예산 소진 시 "꽝" 처리

### 3. 내 포인트 (`/points`)
- 포인트 잔액 표시
- 포인트 타임라인 (획득, 사용, 복구, 회수 이력)
- 유효기간 표시 (만료 예정 경고)
- 만료된 포인트 회색 처리

### 4. 상품 목록 (`/products`)
- 상품 카드 리스트
- 내 포인트로 구매 가능 여부 표시
- 포인트 부족 시 버튼 비활성화
- 구매 확인 모달

### 5. 주문 내역 (`/orders`)
- 주문 목록 (페이지네이션)
- 주문 상태 표시
- 포인트 차감 내역
