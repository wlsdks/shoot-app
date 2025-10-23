# Shoot App

Kotlin Multiplatform (KMP)로 구현한 크로스 플랫폼 모바일 앱

## 프로젝트 개요

Shoot App은 Kotlin Multiplatform과 Compose Multiplatform을 사용하여 Android와 iOS에서 동작하는 크로스 플랫폼 앱입니다.

## 기술 스택

### 핵심 기술
- **Kotlin** 2.1.0
- **Compose Multiplatform** 1.7.1
- **Gradle** 8.10

### 주요 라이브러리
- **Ktor** 3.0.3 - HTTP 클라이언트 및 네트워킹
- **SqlDelight** 2.0.2 - 멀티플랫폼 데이터베이스
- **Koin** 4.0.1 - 의존성 주입
- **Voyager** 1.1.0-beta03 - 네비게이션
- **Coil** 3.0.4 - 이미지 로딩
- **Kotlinx Serialization** 1.7.3 - JSON 직렬화
- **Kotlinx Coroutines** 1.10.1 - 비동기 처리
- **Kotlinx DateTime** 0.6.1 - 날짜/시간 처리

## 프로젝트 구조

```
shoot-app/
├── composeApp/               # 공통 로직 및 UI 모듈
│   ├── src/
│   │   ├── commonMain/       # 공통 코드
│   │   │   ├── kotlin/
│   │   │   │   └── com/shoot/app/
│   │   │   │       ├── di/           # 의존성 주입
│   │   │   │       ├── data/         # 데이터 레이어
│   │   │   │       │   ├── network/  # 네트워크 클라이언트
│   │   │   │       │   └── repository/# 리포지토리
│   │   │   │       ├── domain/       # 도메인 레이어
│   │   │   │       ├── presentation/ # 프레젠테이션 레이어
│   │   │   │       │   ├── screens/  # 화면
│   │   │   │       │   └── viewmodel/# 뷰모델
│   │   │   │       └── util/         # 유틸리티
│   │   │   └── sqldelight/           # 데이터베이스 스키마
│   │   ├── androidMain/      # Android 전용 코드
│   │   └── iosMain/          # iOS 전용 코드
│   └── build.gradle.kts
│
├── androidApp/               # Android 앱 모듈
│   ├── src/main/
│   │   ├── kotlin/
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
│
├── iosApp/                   # iOS 앱 모듈
│   ├── Configuration.xcconfig
│   └── Info.plist
│
├── gradle/
│   ├── wrapper/
│   └── libs.versions.toml    # 버전 카탈로그
│
├── build.gradle.kts          # 루트 빌드 설정
├── settings.gradle.kts       # 프로젝트 설정
└── README.md
```

## 아키텍처

이 프로젝트는 **Clean Architecture** 패턴을 따릅니다:

- **Presentation Layer**: Compose UI + ViewModel
- **Domain Layer**: Use Cases, Models
- **Data Layer**: Repository Pattern, Data Sources

## 빌드 및 실행

### 사전 요구사항

- JDK 17 이상
- Android Studio Hedgehog (2023.1.1) 이상
- Xcode 15.0 이상 (iOS 빌드 시, macOS 필요)

### Android 빌드

```bash
./gradlew :androidApp:assembleDebug
```

### iOS 빌드

```bash
# iOS 프레임워크 빌드
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64

# Xcode에서 iosApp 프로젝트 열기
open iosApp/iosApp.xcodeproj
```

### 프로젝트 클린

```bash
./gradlew clean
```

## 개발 가이드

### 의존성 추가

`gradle/libs.versions.toml` 파일에서 버전 카탈로그를 사용하여 의존성을 관리합니다.

### 데이터베이스 스키마 수정

`composeApp/src/commonMain/sqldelight/com/shoot/app/database/ShootDatabase.sq` 파일에서 SQL 스키마를 수정합니다.

### 새로운 화면 추가

1. `presentation/screens/` 디렉토리에 새 Screen 클래스 생성
2. 필요한 경우 `presentation/viewmodel/` 디렉토리에 ViewModel 생성
3. Voyager Navigator를 통해 화면 이동

## 환경 설정

### BuildKonfig

빌드 설정은 `composeApp/build.gradle.kts`의 `buildkonfig` 블록에서 관리됩니다:

```kotlin
buildkonfig {
    packageName = "com.shoot.app"
    defaultConfigs {
        buildConfigField("String", "APP_NAME", "Shoot App")
        buildConfigField("String", "VERSION_NAME", "1.0.0")
    }
}
```

## 라이선스

이 프로젝트는 개인 프로젝트입니다.

## 기여

현재는 비공개 프로젝트입니다.

## 문의

프로젝트 관련 문의사항이 있으시면 이슈를 등록해주세요.
