# Shoot App

Kotlin Multiplatform (KMP)로 구현한 크로스 플랫폼 모바일 앱

## 프로젝트 개요

Shoot App은 Kotlin Multiplatform과 Compose Multiplatform을 사용하여 Android와 iOS에서 동작하는 크로스 플랫폼 앱입니다.

## 기술 스택

### 핵심 기술
- **Kotlin** 2.2.20
- **Compose Multiplatform** 1.7.1
- **Gradle** 8.13
- **JDK** 21 (LTS)

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

이 프로젝트는 **표준 KMP (Kotlin Multiplatform) 구조**를 따릅니다:

```
shoot-app/
├── composeApp/               # 📦 KMP 공유 라이브러리 (90-95% 코드)
│   ├── src/
│   │   ├── commonMain/       # 🌍 공통 코드 (Android + iOS)
│   │   │   ├── kotlin/
│   │   │   │   └── com/shoot/app/
│   │   │   │       ├── App.kt         # 앱 진입점 UI
│   │   │   │       ├── di/            # 의존성 주입
│   │   │   │       ├── data/          # 데이터 레이어
│   │   │   │       │   ├── network/   # 네트워크 클라이언트
│   │   │   │       │   └── repository/# 리포지토리
│   │   │   │       ├── domain/        # 도메인 레이어
│   │   │   │       ├── presentation/  # 프레젠테이션 레이어
│   │   │   │       │   ├── screens/   # 화면 (Compose UI)
│   │   │   │       │   └── viewmodel/ # ScreenModel
│   │   │   │       └── util/          # 유틸리티
│   │   │   └── sqldelight/            # 데이터베이스 스키마
│   │   ├── androidMain/       # 🤖 Android 전용 구현 (5%)
│   │   │   └── kotlin/
│   │   │       └── com/shoot/app/di/
│   │   │           └── PlatformModule.android.kt
│   │   └── iosMain/           # 🍎 iOS 전용 구현 (5%)
│   │       └── kotlin/
│   │           └── com/shoot/app/
│   │               ├── di/PlatformModule.ios.kt
│   │               └── MainViewController.kt
│   └── build.gradle.kts      # KMP 라이브러리 빌드 설정
│
├── androidApp/               # 📱 Android 진입점 (5% 코드)
│   ├── src/main/             # 표준 Android 구조
│   │   ├── kotlin/
│   │   │   └── com/shoot/app/
│   │   │       ├── MainActivity.kt     # 앱 시작점
│   │   │       └── ShootApplication.kt # Application 클래스
│   │   ├── res/
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   └── themes.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts      # 순수 Android 앱 빌드 설정
│
├── iosApp/                   # 🍎 iOS 진입점 (5% 코드)
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

### 📋 모듈별 역할

| 모듈 | 역할 | 플러그인 | 코드 비중 |
|------|------|----------|-----------|
| **composeApp** | 공유 비즈니스 로직 & UI | `kotlinMultiplatform` | 90-95% |
| **androidApp** | Android 앱 진입점 | `kotlin("android")` | 5% |
| **iosApp** | iOS 앱 진입점 | - | 5% |

### ⚠️ 중요: androidApp은 KMP 모듈이 아닙니다!

`androidApp`은 **순수 Android Application 모듈**입니다:
- `kotlinMultiplatform` 플러그인을 사용하지 않습니다
- `composeApp` 모듈을 의존성으로 가져와 사용합니다
- MainActivity와 Application 클래스만 포함합니다
- 표준 Android 프로젝트 구조(`src/main/`)를 사용합니다

## 아키텍처

이 프로젝트는 **Clean Architecture** 패턴을 따릅니다:

- **Presentation Layer**: Compose UI + ViewModel
- **Domain Layer**: Use Cases, Models
- **Data Layer**: Repository Pattern, Data Sources

## 빌드 및 실행

### 사전 요구사항

- **JDK 21** (최신 LTS)
- **Android Studio Ladybug (2024.2.1)** 이상
- **Xcode 15.0** 이상 (iOS 빌드 시, macOS 필요)

### 🚀 Android Studio에서 실행

#### Android 앱 실행
1. Run Configuration에서 **`androidApp`** 선택
2. Android 에뮬레이터 또는 실제 기기 선택
3. ▶️ Run 버튼 클릭

#### iOS 앱 실행 (macOS만 가능)
1. Run Configuration에서 **`iosApp`** 선택
2. iOS 시뮬레이터 선택
3. ▶️ Run 버튼 클릭

### 📱 터미널에서 실행

#### Android 빌드 및 설치
```bash
# Debug 빌드 및 설치
./gradlew :androidApp:installDebug

# Release 빌드
./gradlew :androidApp:assembleRelease
```

#### iOS 빌드
```bash
# iOS 시뮬레이터용 프레임워크 빌드
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64

# iOS 실제 기기용 프레임워크 빌드
./gradlew :composeApp:linkDebugFrameworkIosArm64

# Xcode에서 iosApp 프로젝트 열기 (프로젝트 생성 필요)
open iosApp/iosApp.xcodeproj
```

### 🧹 프로젝트 클린

```bash
./gradlew clean
```

## 개발 가이드

### 💡 어디에 코드를 작성하나요?

#### 📦 commonMain (90-95%의 코드)
**대부분의 코드는 여기에 작성합니다!**

```kotlin
composeApp/src/commonMain/kotlin/com/shoot/app/
├── App.kt                    # 앱 진입점 UI
├── di/                       # 의존성 주입 설정
├── data/                     # 데이터 레이어
│   ├── network/              # API 클라이언트 (Ktor)
│   └── repository/           # 리포지토리
├── domain/                   # 비즈니스 로직
├── presentation/             # UI 레이어
│   ├── screens/              # 화면 (Compose UI)
│   └── viewmodel/            # ScreenModel/ViewModel
└── util/                     # 유틸리티
```

**commonMain에서 작성 가능한 것:**
- ✅ Compose UI (화면, 컴포넌트)
- ✅ ViewModel/ScreenModel (비즈니스 로직)
- ✅ Repository 인터페이스 및 구현
- ✅ 네트워킹 (Ktor)
- ✅ 데이터베이스 쿼리 (SqlDelight)
- ✅ JSON 직렬화 (kotlinx.serialization)
- ✅ 대부분의 비즈니스 로직

#### 📱 androidMain (2-3%의 코드)
**Android 전용 구현만 작성합니다.**

```kotlin
composeApp/src/androidMain/kotlin/com/shoot/app/
└── di/PlatformModule.android.kt  # Android 플랫폼 DI (SQLite 드라이버 등)
```

**예시**: Android SQLite 드라이버, Android Context 필요한 기능

#### 📱 androidApp (2-3%의 코드)
**Android 앱 진입점만 작성합니다.**

```kotlin
androidApp/src/main/kotlin/com/shoot/app/
├── MainActivity.kt          # 앱 시작 Activity
└── ShootApplication.kt      # Application 클래스 (Koin 초기화)
```

**역할**: Android 앱 시작, Koin DI 초기화
**중요**: 비즈니스 로직은 여기에 작성하지 않습니다!

#### 🍎 iosMain (5-10%의 코드)
**iOS 전용 구현만 작성합니다.**

```kotlin
composeApp/src/iosMain/kotlin/com/shoot/app/
├── di/PlatformModule.ios.kt      # iOS 플랫폼 DI
└── MainViewController.kt         # iOS 진입점
```

예시: iOS SQLite 드라이버, iOS 전용 API 접근 등

### 🔧 개발 워크플로우

#### 1. 새로운 화면 추가

```kotlin
// composeApp/src/commonMain/kotlin/com/shoot/app/presentation/screens/

class NewScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<NewViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        // UI 구현
        Column {
            Text("새로운 화면")
            // ...
        }
    }
}
```

#### 2. ViewModel/ScreenModel 추가

```kotlin
// composeApp/src/commonMain/kotlin/com/shoot/app/presentation/viewmodel/

class NewViewModel(
    private val repository: Repository
) : ScreenModel {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    // 비즈니스 로직
}
```

#### 3. Repository 추가

```kotlin
// composeApp/src/commonMain/kotlin/com/shoot/app/data/repository/

interface NewRepository {
    suspend fun getData(): Result<Data>
}

class NewRepositoryImpl(
    private val httpClient: HttpClient
) : NewRepository {
    override suspend fun getData(): Result<Data> {
        // 구현
    }
}
```

#### 4. DI에 등록

```kotlin
// composeApp/src/commonMain/kotlin/com/shoot/app/di/AppModule.kt

val commonModule = module {
    // Repository
    singleOf(::NewRepositoryImpl) bind NewRepository::class

    // ViewModel
    single<NewViewModel> { NewViewModel(get()) }
}
```

### 📊 데이터베이스 스키마 수정

`composeApp/src/commonMain/sqldelight/com/shoot/app/database/ShootDatabase.sq` 파일에서 SQL 스키마를 수정합니다.

```sql
-- 새 테이블 추가
CREATE TABLE IF NOT EXISTS Message (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    content TEXT NOT NULL,
    userId INTEGER NOT NULL,
    createdAt INTEGER NOT NULL
);

-- 쿼리 추가
selectAllMessages:
SELECT * FROM Message;
```

### 📦 의존성 추가

`gradle/libs.versions.toml` 파일에서 버전 카탈로그를 사용하여 의존성을 관리합니다.

```toml
[versions]
newLibrary = "1.0.0"

[libraries]
new-library = { module = "com.example:library", version.ref = "newLibrary" }
```

### 🎯 플랫폼별 구현이 필요한 경우 (expect/actual 패턴)

```kotlin
// commonMain - 인터페이스 선언
expect class PlatformSpecificFeature {
    fun doSomething(): String
}

// androidMain - Android 구현
actual class PlatformSpecificFeature {
    actual fun doSomething(): String = "Android Implementation"
}

// iosMain - iOS 구현
actual class PlatformSpecificFeature {
    actual fun doSomething(): String = "iOS Implementation"
}
```

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

## 🎯 베스트 프랙티스

### 1. 📦 코드 작성 위치 원칙

```
✅ commonMain에 작성:
- UI 컴포넌트 (Compose)
- ViewModel/ScreenModel
- Repository 인터페이스 & 구현
- 비즈니스 로직
- 네트워크 클라이언트
- 데이터 모델

❌ androidMain/iosMain에만 작성:
- 플랫폼별 SQLite 드라이버
- 플랫폼 Context 필요한 기능
- 플랫폼별 API (Camera, Location 등)

❌ androidApp에는 작성 금지:
- 비즈니스 로직
- UI 컴포넌트
- Repository
→ MainActivity와 Application 클래스만!
```

### 2. 🏗️ Clean Architecture 레이어 구분

```
📂 composeApp/src/commonMain/kotlin/com/shoot/app/

presentation/          # UI Layer
├── screens/           # Compose UI
├── viewmodel/         # ScreenModel
└── components/        # 재사용 컴포넌트

domain/                # Domain Layer (선택사항)
├── usecase/           # Use Cases
└── model/             # Domain Models

data/                  # Data Layer
├── repository/        # Repository 구현
├── network/           # API 클라이언트
├── local/             # 로컬 저장소
└── model/             # Data Transfer Objects (DTO)

di/                    # Dependency Injection
└── AppModule.kt       # Koin 모듈
```

### 3. 🔧 개발 시 주의사항

#### ✅ DO (권장사항)
- commonMain에 최대한 많은 코드 작성 (90%+ 목표)
- `expect/actual` 패턴은 정말 필요할 때만 사용
- Repository 패턴으로 데이터 소스 추상화
- Koin을 사용한 의존성 주입
- Voyager ScreenModel 사용 (일반 ViewModel 대신)
- sealed class/interface로 UI State 관리

#### ❌ DON'T (금지사항)
- androidApp에 비즈니스 로직 작성 금지
- Android/iOS 전용 API를 commonMain에서 직접 호출 금지
- 플랫폼별 Context를 commonMain에 전달 금지
- kotlinMultiplatform 플러그인을 androidApp에 추가 금지

### 4. 📱 모듈별 빌드 설정

#### composeApp (KMP 라이브러리)
```kotlin
plugins {
    alias(libs.plugins.kotlinMultiplatform)  ✅
    alias(libs.plugins.androidLibrary)        ✅
    alias(libs.plugins.composeMultiplatform)  ✅
}

kotlin {
    androidTarget { ... }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
}
```

#### androidApp (순수 Android)
```kotlin
plugins {
    alias(libs.plugins.androidApplication)   ✅
    kotlin("android")                         ✅
    alias(libs.plugins.composeCompiler)      ✅
}

dependencies {
    implementation(project(":composeApp"))   ✅
}
```

### 5. 🚀 개발 워크플로우

```
1. 새로운 기능 개발
   └─> commonMain에 Screen, ViewModel, Repository 작성

2. 플랫폼별 기능 필요 시
   └─> expect/actual 패턴 사용
   └─> androidMain/iosMain에 구현

3. Android 앱 설정 변경
   └─> androidApp/AndroidManifest.xml 수정
   └─> androidApp/MainActivity 수정 (최소화)

4. 빌드 & 실행
   └─> Android Studio Run Configuration 사용
   └─> 또는 ./gradlew :androidApp:installDebug
```

### 6. 🔍 트러블슈팅

#### "ClassNotFoundException" 발생 시
- androidApp이 `kotlinMultiplatform` 플러그인을 사용하고 있는지 확인
  → 사용하면 안 됨! `kotlin("android")` 사용
- 소스 위치 확인: `src/main/` (NOT `src/androidMain/`)

#### "Unresolved reference" 에러 시
- composeApp 의존성 확인
- Gradle sync 실행
- Clean & Rebuild

#### iOS 빌드 실패 시
- Xcode 버전 확인 (15.0+)
- `./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64`로 수동 빌드
- Podfile 업데이트 확인

## 라이선스

이 프로젝트는 개인 프로젝트입니다.

## 기여

현재는 비공개 프로젝트입니다.

## 문의

프로젝트 관련 문의사항이 있으시면 이슈를 등록해주세요.
