# Brewkery

Brewkery is a native Android app for ordering coffee and bakery items. It is built with Kotlin and Jetpack Compose and gets its menu from a static REST API.

The main flow is **Menu → Item customizer → Cart → Order ticket**.

> This was a take-home assignment. I used the provided HTML prototype as the reference for the UI.

## Screens

| Menu | Item customizer | Cart | Order placed |
|:---:|:---:|:---:|:---:|
| <img src="https://github.com/user-attachments/assets/1f1aaf7a-9a36-4253-97c3-48e3f2e80535" alt="Menu screen" width="180" /> | <img src="https://github.com/user-attachments/assets/6398fcf5-610a-4c18-8e8d-f07424263636" alt="Item customizer screen" width="180" /> | <img src="https://github.com/user-attachments/assets/a6482afc-f2de-47f8-8c69-ce21e7c2145d" alt="Cart screen" width="180" /> | <img src="https://github.com/user-attachments/assets/bee634fb-5e75-426a-a56b-6bfa854c4cd7" alt="Order placed screen" width="180" /> |

## Features

- **Menu:** Loads store details, categories and items from `data.json`. Delivery time and fee also come from the API. You can browse category chips, search by item name, tagline or ingredients, and see each item's badge, rating and price.
- **Item customizer:** Choose a size, milk or spread, and sugar or serving option. The price updates as you make selections. You can also change the quantity and add the item to your cart.
- **Cart:** Change item quantities, remove a line by reducing its quantity from 1, or clear the cart. The bill shows the subtotal, flat delivery fee, tax and total.
- **Order:** Placing an order creates a ticket such as `BK-#####`, with PREPARING status, an estimated wait and the item count. After that, the Menu shows an active-order banner.
- **Loading and empty states:** Includes loading, error states with retry for offline, server and unexpected errors, plus empty states for no search results and an empty cart.
- Selections, quantities, search text and cart contents survive screen rotation. The cart is kept in memory, so it can reset when the app is closed, as allowed by the brief.

## Architecture

The app follows this flow:

```
Compose screen  →  ViewModel (StateFlow<UiState>)  →  Repository  →  Retrofit
```

- **`MenuRepository`:** A singleton that fetches `data.json` once and caches the result. It converts failures into a small `MenuError` (`NETWORK`, `SERVER`, `UNKNOWN`), so Retrofit and IO details do not reach the UI.
- **`CartRepository`:** A singleton with an in-memory `StateFlow`. It owns the cart and placed order and is shared by Menu, Detail, Cart and Order. The ViewModels read from it, rather than keeping duplicate copies of the same data.
- **`PriceCalculator`:** Keeps the pricing functions in one place. Prices are stored as integer cents to avoid floating-point rounding issues.
- **UI state:** Each screen has one immutable sealed `UiState`. Each screen's Composables are split between a stateful screen and a stateless `...Content`, with state handled by the ViewModel.
- **Navigation:** Navigation Compose uses type-safe routes. Only ids are passed as arguments, and the back stack is set up intentionally (see Decisions & trade-offs).
- **Dependency injection:** Hilt provides the singletons and ViewModels.

I have not added use cases or DTO-to-domain mappers. For an app of this size, they would only pass data through without adding any behavior.

## Tech stack

| Area | Library |
|---|---|
| Language / UI | Kotlin 2.4.10, Jetpack Compose (BOM 2026.02.01), Material 3 |
| Navigation | Navigation Compose 2.9.8 (type-safe routes via kotlinx.serialization) |
| DI | Hilt 2.60 + KSP 2.3.11, `androidx.hilt:hilt-lifecycle-viewmodel-compose` |
| Networking | Retrofit 3.0.0, OkHttp, kotlinx.serialization 1.11.0 |
| Images | Coil 3 (`coil-compose`, `coil-network-okhttp`) |
| Build | AGP 9.2.1, Gradle Kotlin DSL, version catalog |
| Tests | JUnit 4 |

I intentionally left out Room, Firebase, WorkManager, paging and `material-icons-extended` because this app does not need them.

## API

Base URL: `https://raw.githubusercontent.com/VivekShah138/Brewkery/main/`

| Endpoint | Used |
|---|---|
| `GET data.json` | Yes. Provides store details, categories and all items. |
| `GET api/items/{id}.json` | No. It returns the same item object already included in `items[]`, so the Detail screen uses the cached menu instead of making another request. |

The API does not require authentication and does not use pagination. A few details affected how I built the app:

- **Some option ids are reused across items**, such as `sz_medium` and `m_oat`. Because of that, a cart line is identified by the item id and its selected options together.
- **`milk_options` is also used for bakery spreads.** The UI keeps the prototype's label, "Milk Options / Spreads".
- **One `sugar_levels` value includes an extra charge:** `"Light Wildflower Honey (+0.40)"`. The app parses and adds that charge so the displayed option and final total agree.
- Some option groups contain only one option. The app selects it automatically and still lays out the screen correctly.
- Tax applies to the **subtotal only**. The prototype's numbers confirm this: $14.25 subtotal → $1.14 tax → $17.89 total.

## Matching the prototype

I measured the colors, type sizes, corner radii and dimensions from the prototype's CSS and screenshots, then added them as theme tokens under `presentation/theme` instead of estimating them by eye.

There are a few intentional differences:

- The cart bar and Add-to-Cart row stay pinned to the bottom. In the prototype they scroll with the content, which appears to be an effect of its fixed-height phone frame.
- Search works in the app, even though it was not required by the prototype.
- The heart on the detail screen is decorative; the brief does not include a favorites feature.
- The active-order banner uses the same card style as the store information. The prototype screenshots do not show this banner.
- The prototype displays four menu items, but the API returns six. The app displays all six items returned by the API.

## Project structure

```
app/src/main/java/com/example/brewkery/
├── BrewkeryApp.kt, MainActivity.kt
├── cart/          CartModels, PriceCalculator, ItemSelection, CartRepository
├── data/          MenuRepository, model/ (API models), remote/BrewkeryApi
├── di/            NetworkModule
├── navigation/    BrewkeryNavHost (routes + graph)
└── presentation/
    ├── common/    shared components and formatters
    ├── theme/     Color, Type, Theme
    └── menu/ detail/ cart/ order/    Screen, ViewModel, UiState, components
```

## Build & run

You will need an Android Studio version that supports AGP 9.2, JDK 17 or newer, and an internet connection because the menu and images load over HTTPS. The app's `minSdk` is 26.

```bash
./gradlew :app:testDebugUnitTest      # run unit tests
./gradlew :app:assembleDebug          # build the debug APK at app/build/outputs/apk/debug/app-debug.apk
```

On Windows, use `gradlew.bat`. A prebuilt debug APK is available on the repository's Releases page.

## Tests

I added unit tests for the parts where small mistakes can affect the result:

- `MenuModelsParsingTest`: checks the `data.json` structure, unknown keys and defaults for optional fields.
- `PriceCalculatorTest`: checks cents rounding, option extras, the `(+0.40)` surcharge and the bill from the prototype (subtotal 9.40 → tax 0.75 → total 12.65).
- `CartRepositoryTest`: checks merging cart lines, reused option ids on different items, quantity changes, clearing the cart and placing an order.
- `ItemSelectionTest`: checks the default-selection rule.
- `MenuFilterTest`: checks category and search filtering.

## Decisions & trade-offs

- **Cart flow:** Tapping Add to Cart opens the Cart and removes Detail from the back stack, so Back takes you to the Menu. Place Order replaces the Cart screen with the Order screen, so Back from the ticket also takes you to the Menu.
- **Default selection:** The app picks the first option without an extra charge, or the first option if every option has a surcharge. This matches the prototype's defaults, including the macchiato at $4.85 and the croissant at $3.90.
- **Cart state while ordering:** The Cart screen stops observing updates once an order is placed. Otherwise, it briefly showed its empty state while the app navigated to the ticket.
- **Accessibility:** Cards and rows use merged semantics, icon buttons have content descriptions, touch targets are 48dp, and decorative images are not announced by screen readers.

## Known limitations

- The cart is not saved across process death.
- The layout is designed for portrait phones only.
- The API is read-only, so placing an order happens locally. The ticket id is random.

## AI usage

I used **Claude** (Anthropic) during the assignment, as permitted in the brief. I worked through the app in stages and reviewed each stage before moving on.

1. Started with the requirements, API contract, architecture and risks, without writing code.
2. Built the data layer (models, Retrofit, repository and ViewModel) and checked it before moving on.
3. Added the design tokens, built the screens one by one, then worked on the cart and ordering flow.
4. Shared actual build logs with Claude when I needed help debugging.

**Example prompts**

- *"Analyze the assignment and prototype. Do NOT write code yet. Separate required, nice-to-have and unnecessary. If you can't inspect the API, tell me what you need instead of inventing a contract."*
- *"Don't create unnecessary interfaces, mappers, managers or wrapper classes. Don't invent API fields. Every file must be complete and compilable."*
- *"Deeply analyze the style block and the screenshots. Don't get confused."*

**What helped**

- Measuring the prototype screenshots programmatically—including colors, sizes, corner radii and font sizes calibrated against the actual fonts—rather than guessing the design.
- Checking the real API early and finding details such as repeated option ids and the surcharge hidden in a sugar-level string. These findings affected how the cart works.
- Adding unit tests for the pricing rules and checking them against the amounts shown in the prototype.

**Where Claude got things wrong, and what I changed**

- It suggested an outdated `hiltViewModel` import. The function had moved to an artifact in `androidx.hilt` 1.3, which caused an "Unresolved reference" error. I switched to `hilt-lifecycle-viewmodel-compose`.
- It suggested `minSdk = 24`, but the template's adaptive launcher icons required a higher minimum and resource linking failed. I changed `minSdk` to 26.
- It initially suggested Kotlin 2.2.10 from memory. A dependency brought in stdlib 2.4.10, which the compiler could not read and led to hundreds of "unresolved reference" errors. I aligned Kotlin, KSP and Hilt to 2.4.10 / 2.3.11 / 2.60.
- It guessed that Grande was the default size and that option labels changed by category. The screenshots showed otherwise, so I followed the prototype.
- Its first font instructions caused resource errors because the file names were wrong. I added the static font files with the correct names.

**What I tested myself:** I ran the app against the live API, checked offline behavior and retry, rotated the device, tested Back navigation throughout the flow, and compared the totals with the prototype.
