# Point Roulette - Claude Code 컨텍스트 가이드

> **목적**: Claude Code가 프로젝트를 이해하고 효율적으로 작업할 수 있도록 프로젝트 구조, 컨벤션, 아키텍처 정보를 제공합니다.

---

## 프로젝트 개요
Point Roulette 시스템

### 기술 스택 개요
- **Backend**: Spring Boot 3.5.1, Kotlin, JPA, Swagger, PostgreSQL
- **Admin Web**: React 18+ (Vite), TypeScript, UI 라이브러리 (shadcn/ui, Ant Design 등)
- **User Web**: Next.js 14+ 또는 React 18+ (Vite), TypeScript, Tailwind CSS, TanStack Query
- **Mobile**: Flutter (WebView 기반)
- **CI/CD**: GitHub Actions
- **배포**: Vercel (Frontend/Admin), Render (Backend), Neon (Database)

---

## 프로젝트 구조

```
point-roulette/
├── backend/              # Spring Boot 백엔드 서버
│   └── README.md        # 백엔드 상세 문서 (컨벤션, 아키텍처)
├── admin-web/           # 관리자 웹 (개발 예정)
│   └── README.md        # (추후 추가)
├── user-web/            # 사용자 웹 (개발 예정)
│   └── README.md        # (추후 추가)
├── flutter-app/         # Flutter 모바일 앱 (개발 예정)
│   └── README.md        # (추후 추가)
├── docker-compose.yml   # 로컬 개발 환경
├── CLAUDE.md           # 이 문서
└── PROMPT.md           # AI 활용 프롬프트 기록
```

---

## 모듈별 상세 정보

### Backend
**경로**: `backend/`
**상세 문서**: [backend/README.md](./backend/README.md)

#### 백엔드 모듈의 상세 정보는 별도 README 참조
- 프로젝트 아키텍처
- 코딩 컨벤션
- API 명세
- 테스트 전략

#### 데이터베이스 스키마 (ERD)
> obsidian에서 확인

---

### Admin Web
**경로**: `admin-web/`
**상세 문서**: [admin-web/README.md](./admin-web/README.md) (추후 추가)

관리자 웹 애플리케이션
- 일일 예산 설정
- 룰렛 설정 관리
- 통계 대시보드
- (상세 내용은 추후 README 작성 시 추가)

---

### User Web
**경로**: `user-web/`
**상세 문서**: [user-web/README.md](./user-web/README.md) (추후 추가)

사용자 웹 애플리케이션
- 포인트 룰렛 UI
- 포인트 내역 조회
- 주문 및 포인트 사용
- (상세 내용은 추후 README 작성 시 추가)

---

### Flutter App
**경로**: `flutter-app/`
**상세 문서**: [flutter-app/README.md](./flutter-app/README.md) (추후 추가)

Flutter 모바일 애플리케이션
- 크로스 플랫폼 (iOS/Android)
- User Web과 동일한 기능 제공
- (상세 내용은 추후 README 작성 시 추가)

---

## 작업 가이드라인

### 모듈 README.md
각 모듈의 README.md는 다음을 포함하고 있습니다.
- 기술 스택
- 프로젝트 구조
- 코딩 컨벤션 (명명 규칙, 코드 스타일)
- 아키텍처 패턴 (레이어 구조, 디자인 패턴)
- 빌드 및 실행 방법
- 테스트 실행 방법
- 주요 의존성

---

## Claude Code 작업 시 참고사항

### 작업 전 확인사항
1. 해당 모듈의 README.md를 먼저 읽고 컨벤션 파악
2. README.md에 내용이 없다면 추론하지 않고 obsidian mcp 활용
3. 기존 코드 스타일과 일관성 유지
