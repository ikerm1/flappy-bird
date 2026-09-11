# Flappy Bird — Java Project

A Flappy Bird clone built with Java Swing.

---

## Project Structure

```
FlappyBird/
├── assets/                         ← Images & sounds
│   ├── bird.png
│   ├── sonidito.wav                ← Menu background music
│   ├── Oof_-_Roblox_-.wav          ← Jump SFX
│   └── Never_gonna_give_you_up.wav ← Game-over music
│
├── src/flappybird/
│   ├── Main.java                   ← Entry point
│   │
│   ├── core/
│   │   ├── GameState.java          ← Enum: MENU, PLAYING, GAME_OVER, SETTINGS
│   │   ├── Bird.java               ← Bird physics & rendering
│   │   ├── Pipe.java               ← Single pipe (top or bottom)
│   │   └── PipeManager.java        ← Spawning, movement, collision, scoring
│   │
│   ├── audio/
│   │   └── SoundManager.java       ← Loads and plays WAV files
│   │
│   └── ui/
│       ├── Button.java             ← Clickable button component
│       ├── Slider.java             ← Draggable volume slider
│       ├── GamePanel.java          ← Main game loop & rendering
│       └── GameWindow.java         ← JFrame window setup
│
├── build.sh                        ← Build & run script (macOS / Linux)
└── README.md
```

---

## How to Run

### Option A — Shell script (macOS / Linux)
```bash
chmod +x build.sh
./build.sh
```

### Option B — Manual (Windows / any OS)
```bash
# From the FlappyBird/ folder:
mkdir out
javac -d out -sourcepath src $(find src -name "*.java")
java -cp out flappybird.Main
```

### Option C — VS Code / IntelliJ / Eclipse
1. Open the `FlappyBird/` folder as the project root.
2. Mark `src/` as the Sources Root.
3. Run `flappybird.Main`.
4. Make sure the working directory is set to `FlappyBird/` so the `assets/` folder is found.

---

## Controls

| Key / Action        | Effect                    |
|---------------------|---------------------------|
| `SPACE` / Click     | Flap / Jump               |
| `ENTER`             | Start game from menu      |
| `R`                 | Restart after game over   |
| `ESC`               | Exit                      |

---

## Notes
- The `assets/` folder **must** be next to the folder you run `java` from.
- If `bird.png` isn't found, the bird renders as a yellow oval fallback.
- Volume sliders in Settings affect the slider value visually — wire them to `FloatControl` in `SoundManager` for real volume control.
