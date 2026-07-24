# AGENTS.md — Qlosir Android

## Project Overview

**Qlosir** is a native Android POS (Point of Sale) and stock management app designed for Indonesian warung/kelontong (small grocery store) owners. The app follows an **Offline-First** approach, ensuring full functionality without internet connectivity.

- **Package**: `com.qlosir.app`
- **Application ID**: `com.qlosir.app`
- **Version**: 1.0.0 (MVP)
- **Target Users**: Store owners & cashiers of warung kelontong in Indonesia

---

## Tech Stack & Build Configuration

| Component | Version / Detail |
|-----------|-----------------|
| Language | Kotlin 2.4.10 |
| Build System | Gradle 9.6.1 (Kotlin DSL) |
| Android Gradle Plugin | 9.3.1 |
| Java Target | 17 |
| Compile SDK | 37 |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 37 |
| UI Framework | Jetpack Compose (BOM 2026.06.00) |
| Design System | Material 3 |
| Navigation | Navigation Compose 2.9.8 |
| Lifecycle | 2.11.0 (ViewModel, Runtime KTX) |
| Splash Screen | Core Splash Screen 1.2.0 |
| DataStore | Preferences 1.2.1 |
| Version Catalog | `gradle/libs.versions.toml` |

### Test Dependencies

| Library | Version | Usage |
|---------|---------|-------|
| JUnit 4 | 4.13.2 | Unit test framework |
| kotlinx-coroutines-test | 1.11.0 | Coroutine testing (TestDispatcher, runTest) |
| MockK | 1.14.11 | Mocking framework for Kotlin |

---

## Architecture

### Pattern: MVVM (Model-View-ViewModel)

The project follows **MVVM** with clear separation:

```
Screen (Composable UI) → ViewModel (Business Logic) → UiState (Data) + NavigationEvent (Side Effects)
```

### State Management

- **UI State**: `MutableStateFlow` + `asStateFlow()` in ViewModels (NOT Compose's `mutableStateOf`)
- **Navigation Events**: `MutableSharedFlow` + `asSharedFlow()` for one-time navigation side effects
- **Collection in UI**: Use `collectAsStateWithLifecycle()` for lifecycle-aware observation

### Navigation

- **Library**: Jetpack Navigation Compose
- **Route Definition**: `sealed class Screen(val route: String)` in `navigation/NavGraph.kt`
- **Available Routes**: `Splash`, `Onboarding`, `Register`, `Login`, `Dashboard` (placeholder)
- **Pattern**: Each screen receives an `onNavigate: (NavigationEvent) -> Unit` callback; NavGraph handles actual navigation logic

---

## Project Structure

```
app/src/main/java/com/qlosir/app/
├── MainActivity.kt                     # Single Activity (entry point)
├── data/
│   └── OnboardingPreferences.kt        # DataStore for onboarding completion state
├── navigation/
│   └── NavGraph.kt                     # NavHost + Screen sealed class routes
└── ui/
    ├── components/
    │   └── QlosirLogo.kt               # Reusable brand logo composable
    ├── theme/
    │   ├── Color.kt                    # Color palette definitions
    │   ├── Theme.kt                    # QlosirTheme (Material 3 light scheme)
    │   └── Type.kt                     # Typography (Plus Jakarta Sans)
    ├── splash/
    │   ├── SplashScreen.kt             # Splash UI composable
    │   ├── SplashViewModel.kt          # Timer + onboarding check + navigation
    │   └── SplashNavigationEvent.kt    # Sealed interface for nav events
    ├── onboarding/
    │   ├── OnboardingScreen.kt         # 3-page onboarding carousel
    │   ├── OnboardingViewModel.kt      # Page state + actions
    │   ├── OnboardingUiState.kt        # Data class (currentPage, totalPages)
    │   └── OnboardingNavigationEvent.kt# Sealed interface for nav events
    ├── register/
    │   ├── RegisterScreen.kt           # Store registration form UI
    │   ├── RegisterViewModel.kt        # Form validation + submission
    │   ├── RegisterUiState.kt          # Data class (fields + errors + loading)
    │   └── RegisterNavigationEvent.kt  # Sealed interface for nav events
    └── login/
        ├── LoginScreen.kt              # Login form UI
        ├── LoginViewModel.kt           # Login validation + submission
        ├── LoginUiState.kt             # Data class (fields + errors + loading)
        └── LoginNavigationEvent.kt     # Sealed interface for nav events

app/src/main/res/
├── values/strings.xml                  # Indonesian (default locale)
├── values-en/strings.xml               # English translation
├── font/                               # Plus Jakarta Sans static TTF files
├── drawable/                           # Launcher foreground/background
└── mipmap-*/                           # App icons (all densities)

app/src/test/java/com/qlosir/app/ui/
├── onboarding/
│   └── OnboardingViewModelTest.kt      # Onboarding UiState tests
├── splash/
│   └── SplashViewModelTest.kt          # Splash navigation logic tests (MockK)
├── register/
│   └── RegisterViewModelTest.kt        # Register form validation + navigation tests
└── login/
    └── LoginViewModelTest.kt           # Login validation + navigation tests
```

---

## Per-Screen File Pattern

When creating a new screen, follow this exact structure:

```
ui/<feature_name>/
├── <Feature>Screen.kt              # @Composable screen (connects ViewModel)
├── <Feature>ViewModel.kt           # ViewModel with StateFlow + SharedFlow
├── <Feature>UiState.kt             # data class for UI state
└── <Feature>NavigationEvent.kt     # sealed interface for navigation events
```

### ViewModel Template

```kotlin
class <Feature>ViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(<Feature>UiState())
    val uiState: StateFlow<<Feature>UiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<<Feature>NavigationEvent>()
    val navigationEvent: SharedFlow<<Feature>NavigationEvent> = _navigationEvent.asSharedFlow()

    // Actions update _uiState via .update { it.copy(...) }
    // Navigation emits via viewModelScope.launch { _navigationEvent.emit(...) }
}
```

### Screen Composable Template

```kotlin
@Composable
fun <Feature>Screen(
    onNavigate: (<Feature>NavigationEvent) -> Unit,
    viewModel: <Feature>ViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            onNavigate(event)
        }
    }

    // UI content here, using uiState and viewModel actions
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun <Feature>ScreenPreview() {
    QlosirTheme {
        <Feature>Screen(onNavigate = {})
    }
}
```

### NavigationEvent Template

```kotlin
sealed interface <Feature>NavigationEvent {
    data object NavigateToX : <Feature>NavigationEvent
    data object NavigateBack : <Feature>NavigationEvent
}
```

### UiState Template

```kotlin
data class <Feature>UiState(
    // Fields with defaults
    // Use @StringRes Int? for error messages (allows i18n)
    val isLoading: Boolean = false
)
```

---

## Naming & Language Conventions

| Scope | Language | Example |
|-------|----------|---------|
| File names, classes, functions, variables | English | `RegisterViewModel`, `onSubmit()` |
| Code comments | English | `// Validate phone format` |
| Commit messages | English | `feat: add register screen validation` |
| Default user-facing strings (`values/strings.xml`) | Indonesian | `"Nama Warung"` |
| English user-facing strings (`values-en/strings.xml`) | English | `"Store Name"` |
| String resource keys | English (snake_case) | `register_field_store_name` |

### Kotlin Style

- Use `data class` for UI states
- Use `sealed interface` for navigation events (not sealed class)
- Use `data object` for event variants without parameters
- Error messages in UiState use `@StringRes Int?` for i18n support
- Private backing fields with underscore prefix: `_uiState` / `uiState`
- Use `.update { it.copy(...) }` for StateFlow mutations
- Use `viewModelScope.launch { }` for coroutine operations

---

## Theme & Design System

### Colors (defined in `ui/theme/Color.kt`)

| Token | Hex | Usage |
|-------|-----|-------|
| `BrandPrimaryBlue` | `#1B62E0` | Primary brand color, buttons, links |
| `AccentCheckmarkAmber` | `#FFB020` | Checkmarks, success accents |
| `DarkPrimaryText` | `#12203A` | Main text color |
| `SubtitleText` | `#6B7A91` | Secondary/subtitle text |
| `BackgroundCanvas` | `#EEF2F7` | App background |
| `SplashGradientStart/Middle/End` | `#3288F6`/`#1A5AD0`/`#103E9E` | Splash gradient |
| `SuccessGreen` | `#16A34A` | Success states |

### Typography (defined in `ui/theme/Type.kt`)

- **Font Family**: Plus Jakarta Sans (bundled static TTF: Regular 400, Medium 500, SemiBold 600, Bold 700, ExtraBold 800)
- **Material 3 scales configured**: `displayLarge`, `headlineLarge`, `headlineMedium`, `titleLarge`, `titleMedium`, `bodyLarge`, `bodyMedium`, `labelLarge`, `labelMedium`, `labelSmall`
- Always use `MaterialTheme.typography.*` tokens, not hardcoded `fontSize`/`fontWeight`

### Theme Mode

- **Light only** — no dark mode implementation currently
- Theme wrapper: `QlosirTheme { content }` in `MainActivity`
- Prefer `MaterialTheme.colorScheme.*` tokens over hardcoded `Color(...)` values in composables

---

## Internationalization (i18n)

- Default locale: **Indonesian** (`values/strings.xml`)
- Secondary locale: **English** (`values-en/strings.xml`)
- All user-visible text MUST be in string resources (never hardcoded in composables)
- Use `stringResource(id = R.string.key_name)` in Compose
- Both resource files MUST be kept in sync — any new key added to one must be added to the other

---

## Testing

### Unit Tests

- **Framework**: JUnit 4 + kotlinx-coroutines-test + MockK
- **Location**: `app/src/test/java/com/qlosir/app/ui/<feature>/`
- **Naming**: `<Feature>ViewModelTest.kt`
- **Pattern**: Test ViewModel public API (state changes, initial values, event emissions, validation logic)

### Test Template (with coroutines)

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class <Feature>ViewModelTest {

    private lateinit var viewModel: <Feature>ViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = <Feature>ViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `descriptive test name in backticks`() {
        // Given / When / Then
        viewModel.someAction()
        assertEquals(expected, viewModel.uiState.value.someField)
    }

    @Test
    fun `test navigation event emission`() = runTest {
        val events = mutableListOf<<Feature>NavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        viewModel.someNavigationAction()

        assertEquals(<Feature>NavigationEvent.NavigateToX, events.first())
        job.cancel()
    }
}
```

### Known Test Limitations

- `android.util.Patterns.EMAIL_ADDRESS` is `null` in JVM unit tests — email validation tests that use this Android framework class require Robolectric or should be tested via instrumented tests
- `AndroidViewModel` tests require MockK to mock `Application` context

### Build Verification

```bash
./gradlew assembleDebug    # Compile & build APK
./gradlew test             # Run unit tests
```

---

## Navigation Registration

When adding a new screen, register it in `navigation/NavGraph.kt`:

1. Add a route to the `Screen` sealed class:
   ```kotlin
   data object NewFeature : Screen("new_feature")
   ```

2. Add a `composable` block in `QlosirNavGraph`:
   ```kotlin
   composable(Screen.NewFeature.route) {
       NewFeatureScreen(
           onNavigate = { event ->
               when (event) {
                   is NewFeatureNavigationEvent.NavigateBack -> {
                       navController.popBackStack()
                   }
                   // ... other events
               }
           }
       )
   }
   ```

---

## Git & Commit Conventions

### Commit Message Format

Every commit MUST include a **subject line** and a **body description**:

```
<type>: <short summary under 70 chars>

- Detail of what was changed/added
- Another relevant point
- Why the change was made (if not obvious)
```

### Commit Types

| Type | Usage |
|------|-------|
| `feat` | New feature or screen |
| `fix` | Bug fix |
| `docs` | Documentation only (AGENTS.md, README, comments) |
| `style` | Formatting, no logic change (whitespace, imports) |
| `refactor` | Code restructuring without behavior change |
| `test` | Adding or updating tests |
| `chore` | Build config, dependencies, tooling |

### Rules

- Subject line: English, imperative mood, no period at end, max 70 chars
- Body: Explain **what** and **why** (not how — the code shows how)
- Separate subject from body with a blank line
- Reference related screen/module in body when relevant

### Example

```
feat: add store registration screen with form validation

- Implement RegisterScreen with 5 input fields (store name, owner, phone, email, password)
- Add RegisterViewModel with field-level validation using @StringRes errors
- Create RegisterUiState and RegisterNavigationEvent
- Wire up navigation from Onboarding → Register → Login in NavGraph
```

---

## Key Architectural Decisions

1. **No DI framework yet** — ViewModels are instantiated via `viewModel()` compose function (no Hilt/Koin). Add DI when Repository/UseCase layers are introduced.
2. **Single Activity** — `MainActivity` hosts the entire navigation graph via Compose.
3. **Edge-to-Edge** — `enableEdgeToEdge()` is called in `MainActivity.onCreate()`.
4. **No Room/Database yet** — PRD specifies Room for offline storage, but it hasn't been implemented. Add when data layer is built.
5. **Navigation uses string routes** — Not yet migrated to type-safe `@Serializable` routes (Navigation 2.8+). Consider migrating when complexity grows.
6. **SharedFlow for navigation events** — One-time events use SharedFlow (not Channel) to avoid missed events during configuration changes.

---

## Planned Features (from PRD, not yet implemented)

- Login with 2FA PIN (6-digit)
- Barcode scanning (CameraX + ML Kit)
- Product CRUD with Room database
- POS/Cashier module with cart
- Customer debt/kasbon management
- Financial reports (daily/monthly P&L)
- Thermal Bluetooth printer integration
- Encrypted local storage (Security-Crypto / EncryptedDataStore)
- App settings & language switcher UI

---

## Do's and Don'ts

### ✅ Do

- Use `StateFlow` in ViewModels for state, `SharedFlow` for one-time events
- Use `collectAsStateWithLifecycle()` in composables
- Use `MaterialTheme.colorScheme.*` and `MaterialTheme.typography.*` tokens
- Use `stringResource(R.string.*)` for all user-visible text
- Keep string resources in sync between `values/` and `values-en/`
- Use `@StringRes Int?` for error messages in UiState (i18n-friendly)
- Follow the per-screen 4-file pattern (Screen, ViewModel, UiState, NavigationEvent)
- Add `@Preview` to every Screen composable for Android Studio preview rendering
- Wrap preview composables in `QlosirTheme { }` for accurate theming
- Use `showBackground = true, showSystemUi = true` in `@Preview` annotation
- Make preview functions `private` with naming convention `<Feature>ScreenPreview()`
- Write unit tests for ViewModel logic
- Use `Dispatchers.setMain(testDispatcher)` in unit tests that test coroutine code
- Use English for all code, Indonesian for default user-facing strings

### ❌ Don't

- Don't use `mutableStateOf` in ViewModels (couples to Compose runtime)
- Don't hardcode strings in composables — always use string resources
- Don't hardcode colors — use theme tokens from `Color.kt`
- Don't hardcode font sizes/weights — use `MaterialTheme.typography`
- Don't navigate directly from ViewModel — emit events, let NavGraph handle routing
- Don't use Indonesian for code identifiers, file names, or comments
- Don't add new dependencies without updating `gradle/libs.versions.toml`
- Don't create `@Preview` functions without wrapping in `QlosirTheme`
- Don't add parameters to `@Preview` composable functions (they must be parameterless)
- Don't use `android.util.Patterns` directly in unit tests — it's null in JVM environment
