# Brewkery

A native Android coffee & bakery ordering app built with Kotlin and Jetpack Compose, powered by a single static REST API.
Flow: **Menu → Item customizer → Cart → Order ticket.**

> Take-home assignment. The provided HTML prototype is the source of truth for the UI.

## Screens

| Menu | Item customizer | Cart | Order placed |
|---|---|---|---|
|[Menu](<img width="575" height="1280" alt="Menu" src="https://github.com/user-attachments/assets/1f1aaf7a-9a36-4253-97c3-48e3f2e80535" />
 | [Detail](<img width="575" height="1280" alt="ProductDetail" src="https://github.com/user-attachments/assets/6398fcf5-610a-4c18-8e8d-f07424263636" />
) | [Cart](<img width="575" height="1280" alt="Cart" src="https://github.com/user-attachments/assets/a6482afc-f2de-47f8-8c69-ce21e7c2145d" />
) | [Order](<img width="575" height="1280" alt="Order" src="https://github.com/user-attachments/assets/bee634fb-5e75-426a-a56b-6bfa854c4cd7" />
) |

## Features

- **Menu** loaded from `data.json`: store info (delivery time and fee come from the API), category chips, search (name, tagline, ingredients) and item cards with badge, rating and price.
- **Item customizer**: size, milk/spread and sugar/serving selection with a live price, quantity stepper, and "Add to Cart • $X".
- **Cart**: quantity steppers (decrementing from 1 removes the line), "Clear Cart", and a bill with subtotal, flat delivery fee, tax and total.
- **Order**: generated ticket `BK-#####`, PREPARING status, estimated wait and item count. The Menu shows an active-order banner afterwards.
- **States**: loading, error with retry (offline / server / unexpected), empty (no search results, empty cart).
- Selections, quantities, search text and the cart survive rotation. The cart is in-memory (the brief allows resetting on app close).

## Architecture

```
Compose screen  →  ViewModel (StateFlow<UiState>)  →  Repository  →  Retrofit
```

- **`MenuRepository`** (singleton) fetches `data.json` once and caches it. Failures become a small `MenuError` (`NETWORK`, `SERVER`, `UNKNOWN`), so no Retrofit/IO types reach the UI.
- **`CartRepository`** (singleton, in-memory `StateFlow`) is the single source of truth for the cart and the placed order, shared by Menu, Detail, Cart and Order. ViewModels derive what they show from it, so nothing is duplicated.
- **`PriceCalculator`** holds pure pricing functions. Money is stored as integer cents to avoid floating-point drift.
- **State**: each screen has one immutable sealed `UiState`. Composables are split into a stateful screen and a stateless `...Content`, with state hoisted to the ViewModel.
- **Navigation Compose** with type-safe routes. Only ids travel in arguments, and the back stack is shaped deliberately (see Decisions).
- **Hilt** wires the singletons and ViewModels.

There are intentionally no use cases or DTO→domain mappers. At this size they would be pass-through layers with no behavior.

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

Not used on purpose: Room, Firebase, WorkManager, paging, and `material-icons-extended`.

## API

Base URL: `https://raw.githubusercontent.com/VivekShah138/Brewkery/main/`

| Endpoint | Used |
|---|---|
| `GET data.json` | Yes. Store meta, categories and all items. |
| `GET api/items/{id}.json` | No. It returns the same object as an `items[]` entry, so Detail reads from the cached menu instead of making another request. |

No authentication and no pagination. Things worth knowing about the data:

- **Option ids repeat across items** (`sz_medium`, `m_oat`). A cart line is therefore keyed by item id plus the chosen options.
- **`milk_options` holds spreads for bakery items**, and the UI label is the prototype's static "Milk Options / Spreads".
- **One `sugar_levels` string embeds a surcharge** (`"Light Wildflower Honey (+0.40)"`). It is parsed and charged, so the label never disagrees with the total.
- Some option groups have a single option. They render as selected and never break the layout.
- Tax is applied to the **subtotal only** (the prototype's numbers confirm this: $14.25 → $1.14 tax → $17.89).

## Prototype fidelity

Colors, type scale, radii and sizes were **measured from the prototype's CSS and screenshots** and turned into theme tokens (`presentation/theme`), not eyeballed.

Deliberate differences, each easy to revert:

- The cart bar and the Add-to-Cart row are **pinned** to the bottom. In the prototype they scroll with the content (an artifact of its fixed-height phone frame).
- **Search** is functional, which the prototype does not require.
- The **heart** on the detail screen is decorative. The brief has no favorites feature.
- The **active-order banner** reuses the store-info card style, because the prototype's screenshots don't show it.
- The prototype's menu shows 4 items while the API returns 6. The app shows everything the API returns.

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

Requirements: an Android Studio version that supports AGP 9.2, JDK 17+, internet access (the menu and images are loaded over HTTPS). `minSdk` is 26.

```bash
./gradlew :app:testDebugUnitTest      # unit tests
./gradlew :app:assembleDebug          # app/build/outputs/apk/debug/app-debug.apk
```

On Windows use `gradlew.bat`. A prebuilt debug APK is attached to the repository's Releases page.

## Tests

Unit tests cover the logic that is easy to get subtly wrong:

- `MenuModelsParsingTest`: the `data.json` shape, unknown keys, and defaults for optional fields.
- `PriceCalculatorTest`: cents rounding, option extras, the `(+0.40)` surcharge, and the prototype's bill (subtotal 9.40 → tax 0.75 → total 12.65).
- `CartRepositoryTest`: merging, same option ids on different items, quantity changes, clear, and placing an order.
- `ItemSelectionTest`: the default-selection rule.
- `MenuFilterTest`: category and search filtering.

## Decisions & trade-offs

- **Cart flow**: Add to Cart goes to the Cart and pops Detail, so Back returns to the Menu. Place Order replaces the Cart with the Order screen, so Back from the ticket also returns to the Menu.
- **Default selection**: the first option with no surcharge, falling back to the first option. This reproduces the prototype's defaults (e.g. the macchiato at $4.85, the croissant at $3.90).
- **Cart state during ordering**: the Cart screen stops updating once the order is placed. Without this it flashed its empty state while navigating away.
- **Accessibility**: merged semantics on cards and rows, content descriptions on icon buttons, 48dp touch targets, and decorative images are not announced.

## Known limitations

- The cart is not persisted across process death.
- Portrait phone layout only.
- The API is read-only, so "placing" an order is local. The ticket id is random.

## AI usage

I used **Claude** (Anthropic) throughout this assignment, as encouraged.

**How I worked.** In stages, with a review at the end of each one:
1. Analysis first, with no code: requirements, API contract, architecture and risks.
2. The data layer (models, Retrofit, repository, ViewModel), verified before continuing.
3. Design tokens, then the screens one by one, then the cart and ordering behavior.
4. I pasted real build logs back in for debugging.

**Example prompts**
- *"Analyze the assignment and prototype. Do NOT write code yet. Separate required, nice-to-have and unnecessary. If you can't inspect the API, tell me what you need instead of inventing a contract."*
- *"Don't create unnecessary interfaces, mappers, managers or wrapper classes. Don't invent API fields. Every file must be complete and compilable."*
- *"Deeply analyze the style block and the screenshots. Don't get confused."*

**What worked well**
- Measuring the prototype screenshots programmatically (colors, sizes, radii, font sizes calibrated against the real fonts) instead of guessing at the visual design.
- Checking the real API and finding data quirks early (repeated option ids, the surcharge hidden in a sugar-level string), which shaped the cart's design.
- Pinning the pricing rules in unit tests against the prototype's own numbers.

**Where the AI was wrong, and how I fixed it**
- It used an outdated `hiltViewModel` import. The function moved to a new artifact in `androidx.hilt` 1.3, which gave "Unresolved reference". I switched to `hilt-lifecycle-viewmodel-compose`.
- It suggested `minSdk = 24`. That conflicted with the template's adaptive launcher icons and failed resource linking. I set `minSdk = 26`.
- It pinned Kotlin 2.2.10 from memory. A dependency pulled in stdlib 2.4.10, so the compiler couldn't read it (hundreds of "unresolved reference" errors from one cause). I aligned Kotlin, KSP and Hilt (2.4.10 / 2.3.11 / 2.60).
- It guessed the default size was Grande and that option labels change per category. The screenshots disproved both, so the code follows them.
- Its first font instructions produced resource errors (wrong file names), so I added correctly named static font files.

**What I verified myself**: I ran the app against the live API, tested offline and retry, rotation, and Back behavior at every step of the flow, and compared the totals with the prototype's numbers.
