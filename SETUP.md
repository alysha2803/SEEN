# SEEN — Getting started

## 1. Open in Android Studio

1. Launch **Android Studio** (Hedgehog / Iguana / Jellyfish or newer).
2. **File → Open** → select `C:\Users\Alysha\AndroidProjects\SEEN`.
3. Wait for Gradle sync to complete.
   - If Studio warns about a missing Gradle wrapper, click **Use Gradle wrapper** or let it download automatically.
   - The project targets **compileSdk 35**, **minSdk 24**, **JDK 17**.

## 2. Run on an emulator or device

- **Recommended emulator:** Pixel 7 / API 34 (portrait orientation, ~1080 × 2400).
- Or connect a physical Android device with USB debugging enabled.
- Press **Run ▶** (Shift+F10).

## 3. Replacing placeholder art          

All game images are in `app/src/main/assets/images/`.  
Every placeholder is a solid-colour 8 × 8 (or similar) PNG. See [`IMAGE_MANIFEST.md`](IMAGE_MANIFEST.md) for what each one depicts and size guidelines.

**To swap one in:**
1. Open `app/src/main/assets/images/`.
2. Replace `<imageKey>.png` with your real image, keeping the **exact same filename**.
3. Match the recommended aspect ratio so layouts don't distort.
4. **Build → Rebuild Project** (assets are bundled, so a rebuild is required).
5. No Kotlin changes needed — Coil loads by filename.

## 4. Run the unit tests

The progression-gating logic is covered by pure JUnit4 tests (no emulator needed):

```
./gradlew :app:test
```

Or in Android Studio: right-click `ProgressGatingTest.kt` → **Run tests**.

## 5. Current milestone status (M1–M4)

| Milestone | Status |
|---|---|
| M1 Scaffold (project, nav, theme, assets) | ✅ Done |
| M2 Progression engine (state, DataStore, gating tests) | ✅ Done |
| M3 Shell (ContentWarning, Lockscreen, HomeScreen, MonologueOverlay) | ✅ Done |
| M4 Vertical slice (Messages + MG1 AffectionSwipe, gating wired) | ✅ Done |
| M5 Remaining apps (full Phone, Gallery, Notes, Instagram, X, Browser, Maps, Monitor) | 🟡 Stubs in place |
| M6 Remaining mini-games (LockItDown, TraceItBack, PhoneCheck, WhatDoesSheDo) | 🟡 Stubs with DEV skip button |
| M7 Narrative pass (all beats wired, first-fire tracking) | 🟡 Framework done; beats authored |
| M8 Climax & ending (Scene 9 decision, Aina perspective, resolution, debrief) | ⬜ Not started |
| M9 Polish (performance, localization strings, exit affordance audit) | ⬜ Not started |

## 6. Design spine reminder (non-negotiable)

Before extending or modifying the game, re-read **Part I** of `SEEN-design-bible.md`. In particular:

- **No sensitive Android permissions** — no location, contacts, camera, mic, SMS.
- **Maps is a static image + Compose pin overlays** — no SDK, no geocoding.
- **Mini-games are protective/recognition-only** — never a "find / track the target" mechanic.
- **Content warning** on launch; **"Leave / get help"** reachable everywhere.
- **Climax is non-graphic** — wrong choices escalate fear, not depicted violence.
- **Verify resource contacts** before shipping (all are marked `// TODO: verify`).

## 7. Reset progress (for testing)

The app auto-saves progress via DataStore. To reset during development:

```kotlin
// In any screen, call via the injected ProgressViewModel:
vm.reset()
```

Or uninstall and reinstall the app.

## 8. Known issues (fix in M9 polish)

- **Brief ContentWarning flash on warm launch:** DataStore loads asynchronously; the UI may briefly show the warning screen before restoring saved progress. Low-impact for dev; fix by adding a `loading: Boolean` to `ProgressState`.
- **MG1 deck is not shuffled:** Cards appear in the same order each play. Add `.shuffled()` in `AffectionSwipe` if replay variety is wanted.
- **Lockscreen notification timing:** The notification animates in immediately after the last pre-monologue caption. A deliberate pause before animation would feel more cinematic.
