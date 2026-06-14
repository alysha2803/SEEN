# IMAGE_MANIFEST — SEEN

All images live at `app/src/main/assets/images/<imageKey>.png`.
Loaded by Coil via `"file:///android_asset/images/<imageKey>.png"`.

**To replace a placeholder with real art:**
1. Open `app/src/main/assets/images/`
2. Replace the file with your image, keeping the **exact same filename**
3. Match the suggested dimensions / aspect ratio so layouts don't distort
4. Rebuild the app (assets are bundled; no code changes needed)
5. Only add new `imageKey` references in code if you're adding a brand-new image

---

## Shell

| imageKey | Appears in | Suggested size | Notes |
|---|---|---|---|
| `wallpaper_lockscreen` | Lockscreen background | 1080 × 2400 | Out-of-focus street at dusk, slightly blurred. Shown at 55 % alpha. |
| `wallpaper_home` | HomeScreen background | 1080 × 2400 | Same or complementary street/ambient image. Shown at 40 % alpha. |

## Instagram

| imageKey | Appears in | Suggested size | Notes |
|---|---|---|---|
| `aina_profile` | Instagram profile avatar, Phone contact, Gallery dossier | 400 × 400 | Fictional adult woman, neutral expression. Must be consenting adult / stock / AI-generated fictional person — never a real person without consent. |
| `ig_post_audit` | Instagram feed (first / pinned post); MG2 "Lock it down" target | 1080 × 1080 | Saturday reset post. Should include subtle visual references to: live-location sticker, gym tag, car in background, QR code corner, restaurant caption. All fictional. |
| `ig_post_office` | Instagram feed | 1080 × 1080 | Nasi lemak lunch. Fictional restaurant — no identifiable real landmark. |
| `ig_post_car` | Instagram feed | 1080 × 1080 | Washed car. Fictional number plate (WXX 1234 or clearly fake). |
| `ig_post_qr` | Instagram feed | 1080 × 1080 | Table of food + DuitNow QR. QR must be fictional (not a real payable code). |
| `ig_post_misc` | Instagram feed | 1080 × 1080 | Slow Sunday morning. Generic ambient image. |

## Gallery

| imageKey | Appears in | Suggested size | Notes |
|---|---|---|---|
| `gallery_candid_01` | Gallery grid | 800 × 800 | Candid shot outside a generic office building. Fictional adult. |
| `gallery_candid_02` | Gallery grid | 800 × 800 | Candid in a café. |
| `gallery_candid_03` | Gallery grid | 800 × 800 | Candid in a car park. |
| `gallery_candid_04` | Gallery grid | 800 × 800 | Candid on a street. |
| `gallery_candid_05` | Gallery grid | 800 × 800 | Candid at a bus stop. |
| `gallery_screenshot_01` | Gallery grid | 800 × 800 | Screenshot of a social post (generic). |
| `gallery_screenshot_02` | Gallery grid | 800 × 800 | Screenshot of a liked post. |
| `gallery_flagged_01` | Gallery grid (labelled "+ a guy") | 800 × 800 | Aina with a male friend, flagged. Both fictional adults. |
| `gallery_flagged_02` | Gallery grid (labelled "+ a guy") | 800 × 800 | Second flagged photo with male friend. |

## Notes

| imageKey | Appears in | Suggested size | Notes |
|---|---|---|---|
| `note_office_annotated` | Notes screen — "the restaurant post" | 1080 × 1080 | Mock annotated screenshot: a restaurant promo post with hand-drawn circles, arrows, and the scrawl "→ office found". Shows his **conclusion**, not a method. |

## Maps

| imageKey | Appears in | Suggested size | Notes |
|---|---|---|---|
| `map_static` | MapsScreen background | 1080 × 1080 | Generic city map tile (fictional or freely-licensed). Pins are rendered as Compose overlays at xPct/yPct positions — no map SDK required. |

## Contacts / Messages avatars

| imageKey | Appears in | Suggested size | Notes |
|---|---|---|---|
| `chong_avatar` | Messages inbox — Chong conversation row | 400 × 400 | Fictional adult male. Currently falls back to text initial "C". Replace with real art to activate. |
| `mom_avatar` | Messages inbox — Mom conversation row | 400 × 400 | Fictional adult woman (older). Currently falls back to text initial "M". Replace with real art to activate. |
| `generic_contact_avatar` | Messages inbox — GrabFood, Maybank, Ahmad, Housemates rows; Phone Contacts tab | 400 × 400 | Generic placeholder for contacts without personalised art. Text initial fallback is used until this is replaced. |

## Monitor (grey app)

| imageKey | Appears in | Suggested size | Notes |
|---|---|---|---|
| `monitor_avatar` | Reserved for Monitor dashboard | 400 × 400 | Not used in current code; placeholder for the monitoring app's "target" avatar if added. |

---

## Content and consent requirements (non-negotiable)

- All images must depict **fictional people** or **stock / AI-generated** persons — never real individuals without written consent.
- **No minors** in any image, under any circumstances.
- The annotated note image shows a crime-detection result for awareness — it must **not** be a real usable geolocation tutorial or show a real landmark precisely.
- QR codes in the ig_post_qr image must be non-functional (random pixels or clearly dummy codes — not a real payment link).
- Number plates must be clearly fictional (WXX 1234, or obfuscated).
