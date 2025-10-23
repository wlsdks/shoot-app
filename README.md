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

#### 📱 androidMain (5-10%의 코드)
**Android 전용 구현만 작성합니다.**

```kotlin
composeApp/src/androidMain/kotlin/com/shoot/app/
└── di/PlatformModule.android.kt  # Android 플랫폼 DI
```

예시: Android SQLite 드라이버, Android 전용 센서 접근 등

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

## 라이선스

이 프로젝트는 개인 프로젝트입니다.

## 기여

현재는 비공개 프로젝트입니다.

## 문의

프로젝트 관련 문의사항이 있으시면 이슈를 등록해주세요.
