# Claude Context: Shoot App

> 이 문서는 Claude가 Shoot App 프로젝트를 올바르게 이해하고 개발을 지원하기 위한 컨텍스트 가이드입니다.

## 📋 프로젝트 개요

**Shoot App**은 Kotlin Multiplatform (KMP)으로 구현한 크로스 플랫폼 모바일 앱입니다.

### 기술 스택
- **Kotlin** 2.2.20
- **Compose Multiplatform** 1.7.1
- **Gradle** 8.13
- **JDK** 21 (LTS)
- **Android Gradle Plugin** 8.13.0

### 주요 라이브러리
- Ktor 3.0.3 (네트워킹)
- SqlDelight 2.0.2 (데이터베이스)
- Koin 4.0.1 (DI)
- Voyager 1.1.0-beta03 (네비게이션)
- Coil 3.0.4 (이미지 로딩)

---

## 🏗️ 프로젝트 구조 (중요!)

이 프로젝트는 **표준 KMP 구조**를 따릅니다:

```
shoot-app/
│
├── composeApp/              # 📦 KMP 공유 라이브러리 (90-95% 코드)
│   ├── build.gradle.kts
│   │   └── plugins:
│   │       - kotlinMultiplatform ✅
│   │       - androidLibrary ✅
│   │       - composeMultiplatform ✅
│   │
│   └── src/
│       ├── commonMain/      # 🌍 공통 코드 (Android + iOS)
│       │   ├── kotlin/
│       │   │   └── com/shoot/app/
│       │   │       ├── App.kt
│       │   │       ├── di/
│       │   │       ├── data/
│       │   │       │   ├── network/
│       │   │       │   └── repository/
│       │   │       ├── domain/
│       │   │       └── presentation/
│       │   │           ├── screens/
│       │   │           └── viewmodel/
│       │   ├── resources/
│       │   └── sqldelight/
│       │
│       ├── androidMain/     # 🤖 Android 전용 구현 (2-3%)
│       │   └── kotlin/com/shoot/app/di/
│       │       └── PlatformModule.android.kt
│       │
│       └── iosMain/         # 🍎 iOS 전용 구현 (2-3%)
│           └── kotlin/com/shoot/app/
│               ├── di/PlatformModule.ios.kt
│               └── MainViewController.kt
│
├── androidApp/              # 📱 Android 진입점만! (2-3% 코드)
│   ├── build.gradle.kts
│   │   └── plugins:
│   │       - androidApplication ✅
│   │       - kotlin("android") ✅  (NOT kotlinMultiplatform!)
│   │       - composeCompiler ✅
│   │
│   └── src/main/            # 표준 Android 구조
│       ├── kotlin/com/shoot/app/
│       │   ├── MainActivity.kt      # 앱 시작점
│       │   └── ShootApplication.kt  # Application 클래스
│       ├── res/
│       └── AndroidManifest.xml
│
└── iosApp/                  # 🍎 iOS 진입점만! (2-3% 코드)
```

---

## ⚠️ 중요: androidApp은 KMP 모듈이 아닙니다!

### androidApp의 역할
- **순수 Android Application 모듈**
- `kotlinMultiplatform` 플러그인 사용 ❌
- `kotlin("android")` 플러그인 사용 ✅
- `composeApp`을 의존성으로 가져와 사용
- **MainActivity와 ShootApplication만 포함**
- 소스 위치: `src/main/` (NOT `src/androidMain/`)

### 절대 하지 말아야 할 것
```kotlin
// ❌ androidApp/build.gradle.kts
plugins {
    alias(libs.plugins.kotlinMultiplatform)  // 절대 추가하지 말 것!
}

// ❌ androidApp에 비즈니스 로직 작성 금지
// ❌ androidApp에 Repository 작성 금지
// ❌ androidApp에 UI 컴포넌트 작성 금지
```

---

## 📦 코드 작성 위치 원칙

### ✅ commonMain에 작성 (90%+)
**대부분의 코드는 여기에 작성합니다!**

```kotlin
composeApp/src/commonMain/kotlin/com/shoot/app/

✅ 작성 가능:
- Compose UI (화면, 컴포넌트)
- ScreenModel/ViewModel
- Repository 인터페이스 및 구현
- 네트워크 클라이언트 (Ktor)
- 데이터베이스 쿼리 (SqlDelight)
- JSON 직렬화
- 비즈니스 로직
- Domain Models
- Use Cases
```

### ⚠️ androidMain/iosMain에만 작성 (5%)
**플랫폼 전용 구현만 작성합니다.**

```kotlin
composeApp/src/androidMain/kotlin/com/shoot/app/
composeApp/src/iosMain/kotlin/com/shoot/app/

✅ 작성 가능:
- SQLite 드라이버 (플랫폼별)
- Android Context 필요한 기능
- iOS 전용 API
- expect/actual 구현
```

### 🚫 androidApp에는 작성 금지 (2-3%)
**진입점만 포함합니다.**

```kotlin
androidApp/src/main/kotlin/com/shoot/app/

✅ 작성 가능:
- MainActivity (앱 시작)
- ShootApplication (Koin 초기화)

❌ 작성 금지:
- 비즈니스 로직
- UI 컴포넌트
- Repository
- ViewModel
- 네트워크 클라이언트
```

---

## 🎯 Clean Architecture 레이어 구조

```kotlin
composeApp/src/commonMain/kotlin/com/shoot/app/

presentation/          # UI Layer
├── screens/          # Compose UI (@Composable Screen)
├── viewmodel/        # ScreenModel (Voyager)
└── components/       # 재사용 가능한 UI 컴포넌트

domain/               # Domain Layer (선택사항)
├── usecase/          # Use Cases
└── model/            # Domain Models

data/                 # Data Layer
├── repository/       # Repository 구현
├── network/          # API 클라이언트 (Ktor)
├── local/            # 로컬 저장소 (SqlDelight)
└── model/            # DTO (Data Transfer Objects)

di/                   # Dependency Injection
├── AppModule.kt      # 공통 Koin 모듈
└── KoinInitializer.kt
```

---

## 🔧 개발 가이드라인

### DO (권장사항)

1. **commonMain 우선**
   - 90% 이상의 코드를 commonMain에 작성
   - 플랫폼 차이가 없다면 무조건 commonMain

2. **Voyager ScreenModel 사용**
   - 일반 ViewModel 대신 Voyager의 ScreenModel 사용
   - `screenModelScope` 사용 (viewModelScope 아님)

3. **Koin 의존성 주입**
   - Constructor Injection
   - `koinScreenModel<T>()` 사용

4. **Repository 패턴**
   - 인터페이스 정의 + 구현 분리
   - 데이터 소스 추상화

5. **sealed class로 State 관리**
   ```kotlin
   sealed interface UiState {
       data object Loading : UiState
       data class Success(val data: Data) : UiState
       data class Error(val message: String) : UiState
   }
   ```

### DON'T (금지사항)

1. **androidApp에 로직 작성 금지**
   - MainActivity, Application 클래스만
   - 비즈니스 로직 절대 금지

2. **kotlinMultiplatform 플러그인을 androidApp에 추가 금지**
   - androidApp은 순수 Android Application

3. **플랫폼별 API를 commonMain에서 직접 호출 금지**
   - expect/actual 패턴 사용

4. **Android Context를 commonMain에 전달 금지**
   - androidMain에서만 사용

---

## 🚀 개발 워크플로우

### 1. 새로운 기능 개발

```
1. commonMain에 Screen 작성
   └─> composeApp/src/commonMain/kotlin/com/shoot/app/presentation/screens/

2. commonMain에 ScreenModel 작성
   └─> composeApp/src/commonMain/kotlin/com/shoot/app/presentation/viewmodel/

3. commonMain에 Repository 작성
   └─> composeApp/src/commonMain/kotlin/com/shoot/app/data/repository/

4. AppModule.kt에 DI 등록
   └─> composeApp/src/commonMain/kotlin/com/shoot/app/di/AppModule.kt
```

### 2. 플랫폼별 기능 필요 시

```kotlin
// 1. commonMain에 expect 선언
expect class PlatformFeature {
    fun doSomething(): String
}

// 2. androidMain에 actual 구현
actual class PlatformFeature {
    actual fun doSomething(): String = "Android"
}

// 3. iosMain에 actual 구현
actual class PlatformFeature {
    actual fun doSomething(): String = "iOS"
}
```

### 3. 빌드 & 실행

```bash
# Android 빌드
./gradlew :androidApp:assembleDebug

# Android 설치
./gradlew :androidApp:installDebug

# Clean
./gradlew clean

# iOS 프레임워크 빌드
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

---

## 🔍 트러블슈팅

### ClassNotFoundException 발생 시

**증상**: `ClassNotFoundException: com.shoot.app.android.ShootApplication`

**원인**:
1. androidApp이 `kotlinMultiplatform` 플러그인 사용 중
2. 소스가 `src/androidMain/`에 위치
3. namespace가 잘못됨

**해결**:
```kotlin
// androidApp/build.gradle.kts
plugins {
    alias(libs.plugins.androidApplication)
    kotlin("android")  // NOT kotlinMultiplatform!
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "com.shoot.app"  // NOT com.shoot.app.android
}

// 소스 위치: src/main/ (NOT src/androidMain/)
```

### Unresolved reference 에러

**해결**:
1. Gradle Sync
2. Clean & Rebuild
3. composeApp 의존성 확인
4. 올바른 import 확인

### 앱이 실행되지만 크래시

**확인사항**:
1. Koin 초기화 확인 (ShootApplication.kt)
2. AndroidManifest의 Application 클래스 확인
3. ProGuard 규칙 확인 (Release 빌드)

---

## 📝 의존성 추가 방법

### 1. gradle/libs.versions.toml 수정

```toml
[versions]
newLibrary = "1.0.0"

[libraries]
new-library = { module = "com.example:library", version.ref = "newLibrary" }
```

### 2. composeApp/build.gradle.kts에 추가

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.new.library)
        }
    }
}
```

---

## 🎨 UI 개발 패턴

### Screen 작성

```kotlin
// composeApp/src/commonMain/kotlin/com/shoot/app/presentation/screens/

class NewScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<NewViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        NewScreenContent(
            uiState = uiState,
            onAction = viewModel::handleAction
        )
    }
}

@Composable
private fun NewScreenContent(
    uiState: NewUiState,
    onAction: (NewAction) -> Unit
) {
    // UI 구현
}
```

### ScreenModel 작성

```kotlin
// composeApp/src/commonMain/kotlin/com/shoot/app/presentation/viewmodel/

class NewViewModel(
    private val repository: NewRepository
) : ScreenModel {
    private val _uiState = MutableStateFlow(NewUiState())
    val uiState: StateFlow<NewUiState> = _uiState.asStateFlow()

    fun handleAction(action: NewAction) {
        screenModelScope.launch {
            // 비즈니스 로직
        }
    }
}

sealed interface NewAction {
    data object Load : NewAction
    data class Submit(val data: String) : NewAction
}

data class NewUiState(
    val isLoading: Boolean = false,
    val data: String? = null,
    val error: String? = null
)
```

---

## 🔐 중요 규칙 요약

1. ✅ **90% 이상 commonMain에 작성**
2. ✅ **androidApp은 진입점만** (MainActivity, Application)
3. ✅ **kotlinMultiplatform 플러그인은 composeApp에만**
4. ✅ **Voyager ScreenModel 사용**
5. ✅ **Repository 패턴 준수**
6. ❌ **androidApp에 비즈니스 로직 금지**
7. ❌ **플랫폼별 API를 commonMain에서 직접 호출 금지**

---

## 📚 참고 자료

- [Kotlin Multiplatform 공식 문서](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform 공식 문서](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Voyager 문서](https://voyager.adriel.cafe/)
- [Koin 문서](https://insert-koin.io/)
- [SqlDelight 문서](https://cashapp.github.io/sqldelight/)

---

**마지막 업데이트**: 2025-10-23
