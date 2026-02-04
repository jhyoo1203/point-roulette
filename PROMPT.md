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

## 2. NotebookLM
_(이후 추가 예정)_

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
