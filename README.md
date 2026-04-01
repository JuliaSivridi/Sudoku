# Sudoku

A clean, minimal Sudoku app for Android built with Kotlin and Jetpack Compose.

---

## Screenshots

| Light theme | Dark theme |
|-------------|------------|
| ![Light theme](screenshots/light.jpg) | ![Dark theme](screenshots/dark.jpg) |

---

## Features

**Gameplay**
- Three difficulty levels — Easy, Medium, Hard
- Two input modes — **Number first** (pick digit, tap cell) or **Cell first** (tap cell, pick digit); switchable in Settings
- Notes mode — pencil marks in a 3×3 mini-grid per cell; auto-clear when a digit is placed in the same row, column, or box
- Auto-notes — fill in all valid candidates for every empty cell in one tap
- Conflict highlighting — wrong digits shown in red; detects duplicates against both given and user-placed digits
- Undo — step back through every move all the way to the start
- Hints — reveal a correct digit in any empty cell (unlimited)
- Auto-complete digit pad — once a digit is placed 9 times it disappears from the number pad
- Motivational finish screen — a random encouraging message on completion

**Solver**
- Enter any puzzle (from a newspaper, another app, etc.) and tap **Solve**

**Save & Continue**
- Current game saves automatically after every move
- Resume from the main menu with the **Continue** button

**Statistics**
- Tracks completed games per difficulty (Easy / Medium / Hard)

**Themes**
- Four color themes: Orange, Green, Blue, Purple
- Each theme colors the accent, digit highlights, and user-placed numbers
- Dark / light mode follows system setting automatically

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Architecture | ViewModel + StateFlow |
| Navigation | Navigation Compose |
| Persistence | SharedPreferences |
| Icons | Material Icons Extended |
| Min SDK | API 26 (Android 8.0) |

---

## Building

1. Clone the repository
   ```bash
   git clone git@github-personal:JuliaSivridi/Sudoku.git
   cd Sudoku
   ```
2. Open in **Android Studio**
3. Connect an Android device or start an emulator
4. Click **Run ▶**

No API keys or external services required — everything runs on-device.

---

## How to Play

1. Pick a difficulty on the start screen
2. **Number first mode** (default): select a digit in the number pad, then tap an empty cell to place it
   **Cell first mode**: tap a cell to select it, then tap a digit to place it — great for filling notes without switching focus
3. Use **Notes** to pencil in candidates — they auto-clear as the board fills; tap **Auto** to fill all valid candidates at once
4. Use **Undo** to step back, **Hint** to reveal a correct digit
5. Finish the puzzle and collect your reward 🎉

Use the **Solver** screen to solve any puzzle from a newspaper or another app — enter the given digits and tap **Solve**.
