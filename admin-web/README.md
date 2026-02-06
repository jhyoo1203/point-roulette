# Point Roulette Admin Web

> 포인트 룰렛 서비스 관리자 웹 애플리케이션 — 일일 예산 통제, 룰렛/상품/주문 관리

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
- **Framework**: React 18+ (Vite 7)
- **Package Manager**: pnpm

### UI
- **Component Library**: shadcn/ui
- **Styling**: Tailwind CSS

### 상태 관리 & 데이터 페칭
- **Server State**: TanStack Query (React Query)
- **Client State**: React Context 또는 Zustand (최소한으로 사용)

### 개발 도구
- **Lint**: ESLint (typescript-eslint, react-hooks, react-refresh)
- **Formatter**: Prettier
- **Build**: Vite

---

## 프로젝트 구조

```
admin-web/
├── src/
│   ├── main.tsx                    # 엔트리 포인트
│   ├── App.tsx                     # 루트 컴포넌트 (라우팅, Provider)
│   │
│   ├── pages/                      # 페이지 컴포넌트 (라우트 단위)
│   │   ├── dashboard/
│   │   ├── budget/
│   │   ├── product/
│   │   └── order/
│   │
│   ├── features/                   # 기능 단위 모듈 (도메인별)
│   │   ├── dashboard/
│   │   │   ├── components/         # 대시보드 전용 컴포넌트
│   │   │   ├── hooks/              # 대시보드 전용 훅
│   │   │   └── types.ts
│   │   ├── budget/
│   │   │   ├── components/
│   │   │   ├── hooks/
│   │   │   └── types.ts
│   │   ├── product/
│   │   │   ├── components/
│   │   │   ├── hooks/
│   │   │   └── types.ts
│   │   └── order/
│   │       ├── components/
│   │       ├── hooks/
│   │       └── types.ts
│   │
│   ├── shared/                     # 공통 모듈
│   │   ├── components/             # 공통 UI 컴포넌트 (Layout, Modal 등)
│   │   ├── hooks/                  # 공통 훅
│   │   ├── lib/                    # 유틸리티, API 클라이언트 설정
│   │   └── types/                  # 공통 타입 정의
│   │
│   └── assets/                     # 정적 리소스
│
├── public/
├── index.html
├── vite.config.ts
├── tsconfig.json
├── eslint.config.js
└── package.json
```

### 디렉토리별 역할

#### pages (페이지 계층)
- **역할**: 라우트와 1:1 매핑되는 페이지 컴포넌트
- **특징**: 레이아웃 구성과 feature 컴포넌트 조합에 집중, 직접적인 비즈니스 로직 최소화
- **구성**: `dashboard/`, `budget/`, `product/`, `order/`

#### features (기능 도메인 계층)
- **역할**: 도메인별 컴포넌트, 훅, 타입을 응집하여 관리
- **특징**: 각 feature는 독립적으로 동작하며, feature 간 직접 import 지양
- **구성**: 도메인별로 `components/`, `hooks/`, `types.ts` 분리

#### shared (공통 계층)
- **역할**: 2개 이상의 feature에서 공유하는 코드
- **특징**: 도메인에 종속되지 않는 범용 코드만 배치
- **구성**: `components/` (공통 UI), `hooks/` (공통 훅), `lib/` (API 클라이언트, 유틸리티), `types/` (공통 타입)

### 설계 원칙

#### 1. Feature 기반 구조
- 도메인(대시보드, 예산, 상품, 주문)별로 코드를 응집
- feature 간 의존성을 최소화하여 독립적 개발 및 유지보수 가능

#### 2. 의존성 방향
```
pages → features → shared
```
- pages는 features를 조합
- features는 shared만 참조
- shared는 외부 라이브러리만 의존
- feature 간 직접 참조 금지 (필요 시 shared로 추출)

---

## 아키텍처

### 데이터 흐름

```
┌─────────────────────────────────────────────┐
│                  Pages                       │  ← 라우트 매핑, 레이아웃
├─────────────────────────────────────────────┤
│              Feature Components              │  ← 도메인 UI, 이벤트 핸들링
├─────────────────────────────────────────────┤
│           Custom Hooks (TanStack Query)      │  ← API 호출, 서버 상태 관리
├─────────────────────────────────────────────┤
│              API Client (Axios/Fetch)        │  ← HTTP 통신, 인터셉터
├─────────────────────────────────────────────┤
│              Backend REST API                │  ← Spring Boot 서버
└─────────────────────────────────────────────┘
```

### 주요 설계 결정

#### 1. TanStack Query 기반 서버 상태 관리

- **Query Key 체계**: 도메인과 액션을 명확히 구분
  - `['budget', 'daily']` — 일일 예산 조회
  - `['products', 'list']` — 상품 목록
  - `['orders', 'list', { page, size }]` — 주문 목록 (페이지네이션 포함)
- **Stale/Cache 전략**:
  - 예산 데이터: `staleTime: 5000` (5초) — 운영 의사결정에 치명적이므로 짧은 주기
  - 상품/주문 목록: `staleTime: 30000` (30초) — 상대적으로 변경 빈도 낮음
- **자동 갱신**: `refetchOnWindowFocus: true` 로 탭 전환 시 최신 데이터 보장
- **캐시 무효화**: 예산 설정, 포인트 회수, 주문 취소 등 Mutation 성공 후 `invalidateQueries` 필수 호출

#### 2. 파괴적 작업 안전장치 (Double-Lock)

일일 예산 수정, 포인트 회수, 주문 취소 등 유저 자산에 직결되는 액션에는 **2단계 확인 UX** 적용:
1. 버튼 클릭 → 확인 모달 표시 (영향 범위 안내)
2. 모달 내 최종 확인 → API 호출 실행

#### 3. 전역 에러 처리

- **Error Boundary**: 예상치 못한 렌더링 에러 포착 및 폴백 UI
- **Toast 알림**: API 실패 시 운영자에게 즉시 피드백
- **Loading State**: 모든 데이터 요청 중 스켈레톤/스피너 표시 → 중복 클릭 방지

---

## 코딩 컨벤션

### TypeScript 스타일

- **strict 모드** 활성화 (tsconfig `strict: true`)
- `any` 사용 금지 — 포인트, 금액 등 재무 데이터는 반드시 명시적 타입 정의
- **Interface** vs **Type**: API 응답/요청 스키마는 `interface`, 유니온/유틸리티 타입은 `type`

### 명명 규칙

| 대상 | 규칙 | 예시 |
|------|------|------|
| **컴포넌트** | PascalCase | `BudgetCard`, `OrderTable` |
| **컴포넌트 파일** | PascalCase.tsx | `BudgetCard.tsx` |
| **Hook** | camelCase (`use` 접두사) | `useBudget`, `useProducts` |
| **Hook 파일** | camelCase.ts | `useBudget.ts` |
| **유틸/상수 파일** | camelCase.ts | `formatPoint.ts`, `queryKeys.ts` |
| **타입 파일** | camelCase.ts | `types.ts` |
| **디렉토리** | kebab-case 또는 camelCase | `components/`, `hooks/` |
| **이벤트 핸들러** | `handle` + 동사 | `handleSubmit`, `handleCancel` |
| **Props 콜백** | `on` + 동사 | `onSubmit`, `onCancel` |

### 컴포넌트 작성 규칙

- **함수형 컴포넌트**만 사용 (클래스 컴포넌트 금지)
- Props는 `interface`로 정의하고, 컴포넌트와 같은 파일 또는 `types.ts`에 배치
- 한 파일에 하나의 export 컴포넌트 원칙 (헬퍼 컴포넌트는 같은 파일 내 비공개)

### 성능 및 React 패턴

> 상세 규칙은 **`/react-best-practices`** Skill을 참조.

주요 적용 항목:
- **리렌더 최적화**: 파생 상태 계산, 함수형 setState, `memo` 적절한 사용
- **번들 최적화**: 동적 import를 통한 코드 스플리팅, barrel import 지양
- **비동기 처리**: 병렬 fetch, Suspense 경계 활용
- **클라이언트 데이터 페칭**: SWR 중복 제거 패턴, TanStack Query 캐시 전략

### API 연동 규칙

- API 호출은 반드시 **Custom Hook** 내부에서 TanStack Query를 통해 수행
- 컴포넌트에서 직접 `fetch`/`axios` 호출 금지
- Query Key는 `shared/lib/queryKeys.ts`에서 중앙 관리
- Mutation 성공 후 관련 Query 캐시 `invalidateQueries` 필수

---

## 테스트

### 테스트 전략

#### 1. 테스트 범위 및 목표
- **Unit Test**: Custom Hook, 유틸리티 함수
- **Component Test**: 주요 Feature 컴포넌트의 렌더링 및 인터랙션
- **Integration Test**: 페이지 단위 API 연동 시나리오

#### 2. 테스트 프레임워크 및 도구
- **Vitest**: 테스트 러너 (Vite 네이티브 통합)
- **React Testing Library**: 컴포넌트 렌더링 및 DOM 상호작용
- **MSW (Mock Service Worker)**: API 모킹

### 테스트 구조

```
src/
├── features/
│   ├── budget/
│   │   ├── hooks/
│   │   │   ├── useBudget.ts
│   │   │   └── useBudget.test.ts        # Hook 테스트
│   │   └── components/
│   │       ├── BudgetCard.tsx
│   │       └── BudgetCard.test.tsx       # 컴포넌트 테스트
│   └── ...
│
├── shared/
│   └── lib/
│       ├── formatPoint.ts
│       └── formatPoint.test.ts           # 유틸리티 테스트
│
└── test/
    ├── setup.ts                          # 테스트 환경 설정
    └── mocks/                            # MSW 핸들러
        └── handlers.ts
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

---

## 빌드 및 실행

### 로컬 개발

```bash
cd admin-web
pnpm install     # 의존성 설치
pnpm dev         # 개발 서버 (http://localhost:5173)
```

### 빌드 및 프리뷰

```bash
pnpm build       # 프로덕션 빌드 (tsc + vite build)
pnpm preview     # 빌드 결과 로컬 프리뷰
```

### 린트

```bash
pnpm lint        # ESLint 실행
```

### 배포
- **플랫폼**: Vercel
- **방식**: GitHub 연동 자동 배포
