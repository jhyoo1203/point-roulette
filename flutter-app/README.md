# Point Roulette - Flutter Mobile App

> **타입**: Mobile Application (Cross-platform)
> **플랫폼**: iOS, Android
> **아키텍처**: WebView 기반

---

## 개요

Point Roulette 모바일 앱은 Flutter를 사용하여 크로스 플랫폼으로 개발된 애플리케이션입니다. User Web을 WebView로 렌더링하여 웹과 동일한 사용자 경험을 제공합니다.

---

## 기술 스택

- **Framework**: Flutter 3.10.8+
- **Language**: Dart
- **주요 패키지**:
  - `webview_flutter`: WebView 렌더링
  - `webview_flutter_android`: Android WebView 구현
  - `webview_flutter_wkwebview`: iOS WKWebView 구현
  - `connectivity_plus`: 네트워크 연결 상태 확인
  - `shared_preferences`: 로컬 저장소 (로그인 상태 유지)
  - `flutter_launcher_icons`: 앱 아이콘 생성
  - `flutter_native_splash`: 네이티브 스플래시 화면

---

## 프로젝트 구조

```
flutter-app/
├── lib/
│   ├── main.dart                    # 앱 진입점
│   └── screens/
│       ├── webview_screen.dart      # WebView 메인 화면
│       └── error_screen.dart        # 에러 화면
├── assets/
│   └── icon/
│       └── point-roulette-logo.png  # 앱 아이콘
├── android/                          # Android 네이티브 설정
├── ios/                              # iOS 네이티브 설정
└── pubspec.yaml                      # 패키지 의존성
```

---

## 주요 기능

### 1. WebView 렌더링
- User Web을 WebView로 렌더링
- JavaScript 활성화
- 줌 기능 비활성화
- 쿠키 관리를 통한 로그인 상태 유지

### 2. 네비게이션
- Android 뒤로가기 버튼 지원
- iOS 스와이프 제스처 지원
- WebView 히스토리 관리

### 3. 에러 처리
- 네트워크 연결 실패 감지
- HTTP 에러 처리
- 커스텀 에러 페이지 표시
- 재시도 버튼 제공

### 4. 로딩 상태
- 페이지 로딩 중 네이티브 인디케이터 표시
- 로딩 메시지 표시

### 5. 스플래시 화면
- 앱 실행 시 네이티브 스플래시 화면
- 브랜드 로고 표시

### 6. 앱 아이콘 & 이름
- 앱 이름: "Point Roulette"
- 커스텀 아이콘 적용
- Android Adaptive Icon 지원
