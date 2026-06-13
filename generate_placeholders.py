"""
generate_placeholders.py — SEEN
Creates solid-colour placeholder PNG files for every imageKey in the game,
plus launcher-icon PNGs for API 24-25 fallback (API 26+ uses adaptive XML).

Run from the project root:
    python generate_placeholders.py

Replace each file in assets/images/ with real art later — no code changes needed.
"""

import struct, zlib, os

def make_png(width: int, height: int, r: int, g: int, b: int) -> bytes:
    """Return a minimal valid PNG: solid RGB colour, no alpha."""
    def chunk(tag: bytes, data: bytes) -> bytes:
        c = tag + data
        return struct.pack(">I", len(data)) + c + struct.pack(">I", zlib.crc32(c) & 0xFFFFFFFF)

    sig  = b"\x89PNG\r\n\x1a\n"
    ihdr = chunk(b"IHDR", struct.pack(">IIBBBBB", width, height, 8, 2, 0, 0, 0))
    raw  = (b"\x00" + bytes([r, g, b] * width)) * height
    idat = chunk(b"IDAT", zlib.compress(raw, level=9))
    iend = chunk(b"IEND", b"")
    return sig + ihdr + idat + iend


def write(path: str, w: int, h: int, r: int, g: int, b: int) -> None:
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "wb") as f:
        f.write(make_png(w, h, r, g, b))
    print(f"  wrote {path}  ({w}×{h}  rgb={r},{g},{b})")


ROOT   = os.path.dirname(os.path.abspath(__file__))
ASSETS = os.path.join(ROOT, "app", "src", "main", "assets", "images")
RES    = os.path.join(ROOT, "app", "src", "main", "res")

print("-- Game assets --")

GAME_IMAGES = [
    # (imageKey, width, height, r, g, b)   small dims — Coil scales them
    # Shell
    ("wallpaper_lockscreen",   9, 20, 18, 10, 28),   # deep purple-night
    ("wallpaper_home",         9, 20, 12, 15, 26),   # dark blue-grey

    # Instagram
    ("aina_profile",           8,  8, 180, 130, 120),  # warm rose
    ("ig_post_audit",          8,  8, 220, 160, 130),  # peach
    ("ig_post_office",         8,  8, 210, 160,  80),  # amber
    ("ig_post_car",            8,  8, 120, 130, 140),  # cool grey
    ("ig_post_qr",             8,  8,  70, 160, 150),  # teal
    ("ig_post_misc",           8,  8, 160, 140, 200),  # lavender

    # Gallery (dark candids)
    ("gallery_candid_01",      8,  8,  60,  55,  65),
    ("gallery_candid_02",      8,  8,  55,  60,  65),
    ("gallery_candid_03",      8,  8,  65,  55,  60),
    ("gallery_candid_04",      8,  8,  60,  60,  55),
    ("gallery_candid_05",      8,  8,  55,  65,  60),
    ("gallery_screenshot_01",  8,  8,  80,  90, 110),  # blue-grey (screenshots)
    ("gallery_screenshot_02",  8,  8,  75,  95, 115),
    ("gallery_flagged_01",     8,  8,  90,  60,  60),  # reddish-dark (flagged)
    ("gallery_flagged_02",     8,  8,  95,  55,  60),

    # Notes
    ("note_office_annotated",  8,  8, 230, 215, 195),  # warm cream / paper

    # Maps
    ("map_static",             8,  8,  85, 110,  90),  # muted map green

    # Monitor
    ("monitor_avatar",         8,  8,  20,  50,  50),  # dark teal
]

for key, w, h, r, g, b in GAME_IMAGES:
    write(os.path.join(ASSETS, f"{key}.png"), w, h, r, g, b)

print()
print("-- Launcher icons (API 24-25 fallback PNGs) --")

LAUNCHER_SIZES = [
    ("mipmap-mdpi",    48),
    ("mipmap-hdpi",    72),
    ("mipmap-xhdpi",   96),
    ("mipmap-xxhdpi",  144),
    ("mipmap-xxxhdpi", 192),
]

for density, size in LAUNCHER_SIZES:
    dir_path = os.path.join(RES, density)
    write(os.path.join(dir_path, "ic_launcher.png"),       size, size, 13, 13, 13)
    write(os.path.join(dir_path, "ic_launcher_round.png"), size, size, 13, 13, 13)

print()
print("Done. Replace files in assets/images/ with real art; no code changes needed.")
