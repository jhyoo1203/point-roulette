---
name: test
description: "프로젝트의 테스트 코드를 작성합니다. 각 모듈(backend, admin-web, user-web)의 README.md를 참조하여 해당 모듈의 테스트 전략과 컨벤션에 맞는 테스트를 작성합니다."
argument-hint: "[테스트할 파일 또는 기능]"
user-invocable: true
disable-model-invocation: false
allowed-tools: "Read, Glob, Grep, Write, Edit, Bash"
---

# 테스트 코드 작성 스킬

## 개요

이 스킬은 Point Roulette 프로젝트의 테스트 코드를 작성합니다.
각 모듈의 테스트 전략과 컨벤션을 준수하여 일관된 테스트 코드를 생성합니다.

---

## 작업 프로세스

### 1. 모듈 식별

테스트 대상 파일의 경로를 분석하여 어느 모듈에 속하는지 식별합니다.

- `backend/` → Spring Boot Kotlin 백엔드
- `admin-web/` → React 관리자 웹 (추후 추가)
- `user-web/` → Next.js/React 사용자 웹 (추후 추가)
- `flutter-app/` → Flutter 모바일 앱 (추후 추가)

### 2. 테스트 전략 파악

해당 모듈의 `README.md` 파일을 읽어 테스트 전략을 확인합니다.

**필수 확인 사항**:
- 테스트 프레임워크 및 도구
- 테스트 구조 및 파일 위치
- 계층별 테스트 가이드
- 테스트 작성 규칙
- 명명 규칙

**참조 경로**:
- Backend: `backend/README.md` → "## 테스트" 섹션
- Admin Web: `admin-web/README.md` (추후 추가)
- User Web: `user-web/README.md` (추후 추가)
- Flutter App: `flutter-app/README.md` (추후 추가)
