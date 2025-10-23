# Shoot App - KMP Project

> Kotlin Multiplatform 크로스 플랫폼 모바일 앱

## Tech Stack

- Kotlin 2.2.20, Compose Multiplatform 1.7.1, Gradle 8.13, JDK 21
- Ktor 3.0.3, SqlDelight 2.0.2, Koin 4.0.1, Voyager 1.1.0-beta03, Coil 3.0.4

## Project Structure

```
shoot-app/
├── composeApp/          # KMP 라이브러리 (90-95% 코드)
│   ├── commonMain/      # 공통 코드 (Android + iOS)
│   ├── androidMain/     # Android 전용 (2-3%)
│   └── iosMain/         # iOS 전용 (2-3%)
├── androidApp/          # Android 진입점 (2-3%)
│   └── src/main/        # MainActivity, Application만
└── iosApp/              # iOS 진입점 (2-3%)
```

### Module Roles

**composeApp** (KMP Library)
- Plugins: `kotlinMultiplatform`, `androidLibrary`, `composeMultiplatform`
- 모든 비즈니스 로직, UI, Repository, ViewModel

**androidApp** (Pure Android App)
- Plugins: `androidApplication`, `kotlin("android")`, `composeCompiler`
- **NOT** `kotlinMultiplatform`
- MainActivity, ShootApplication만 포함
- Source: `src/main/` (NOT `src/androidMain/`)
- Namespace: `com.shoot.app`

## Code Placement Rules

### commonMain (90%+)
- Compose UI, ScreenModel, Repository
- Network client (Ktor), Database (SqlDelight)
- Business logic, Domain models, Use cases

### androidMain/iosMain (5%)
- SQLite drivers
- Platform-specific APIs (Camera, Location)
- expect/actual implementations

### androidApp (2-3%)
- MainActivity (app entry)
- ShootApplication (Koin init)
- **NO business logic, UI, Repository**

## Architecture

Clean Architecture 레이어:
- `presentation/` - Compose Screens, ScreenModel, Components
- `domain/` - Use Cases, Domain Models (선택)
- `data/` - Repository, Network, Local DB, DTOs
- `di/` - Koin modules

## Development Rules

### DO
- commonMain에 90% 이상 코드 작성
- Voyager ScreenModel 사용 (`screenModelScope`)
- Koin Constructor Injection
- Repository pattern (interface + impl)
- sealed class/interface로 State 관리

### DON'T
- androidApp에 비즈니스 로직/UI/Repository 작성 금지
- androidApp에 `kotlinMultiplatform` 플러그인 추가 금지
- commonMain에서 플랫폼 API 직접 호출 금지
- Android Context를 commonMain에 전달 금지

## Workflow

1. 새 기능: commonMain에 Screen → ScreenModel → Repository 작성
2. 플랫폼 기능: expect/actual 패턴 사용
3. DI 등록: `AppModule.kt`
4. 빌드: `./gradlew :androidApp:installDebug`

## Build Commands

```bash
# Android
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:installDebug
./gradlew clean

# iOS
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

## Dependency Management

1. `gradle/libs.versions.toml` 수정
2. `composeApp/build.gradle.kts`의 `commonMain.dependencies`에 추가

## Common Issues

### ClassNotFoundException
- androidApp이 `kotlinMultiplatform` 사용 중 → 제거
- 소스가 `src/androidMain/` → `src/main/`으로 이동
- namespace 확인: `com.shoot.app`

### Unresolved reference
- Gradle Sync → Clean & Rebuild
- composeApp 의존성 확인

## Important Rules

1. ✅ 90%+ commonMain
2. ✅ androidApp = MainActivity + Application only
3. ✅ kotlinMultiplatform plugin → composeApp only
4. ❌ androidApp에 로직 금지
5. ❌ 플랫폼 API를 commonMain에서 직접 호출 금지

---

*Last updated: 2025-10-23*
