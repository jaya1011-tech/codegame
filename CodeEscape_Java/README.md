# CodeEscape — Advanced Professional Java Puzzle Adventure

An upgraded, feature-rich 2D sci-fi puzzle adventure built with Java Swing and Java 2D Graphics. Navigate an abandoned high-tech research facility as a diagnostic robot, solve Java programming challenges, evade synchronized laser hazards, manipulate switch relays and pressure grids, collect colored keycards, and unlock the evacuation shuttle across 12 handcrafted campaign sectors.

---

## Key Features & Upgrades

- **Strict Access Control System**: Complete fix of the original door-unlocking bug. Doors now feature explicit state machines (`LOCKED`, `UNLOCKING`, `OPEN`) with requirement validation. Doors require matching keycard colors (Red, Blue, Green, Gold, Cyan), switch networks, pressure plates, or terminal overrides. Wrong keys never unlock doors!
- **12-Sector Campaign**: Fully verified, playable, and solvable levels with increasing complexity (Boot Sequence, Access Control, Conditional Logic, Switch Network, Loop Protocol, Laser Corridor, Timed Access, Pressure Grid, Debugging Lab, Multi-Key Security, System Failure, Final Escape).
- **Comprehensive Question Bank (64 Verified Topics)**: Spans Java Fundamentals, Variables & Types, Operators & Logic, Conditionals, Loops, Arrays & 2D Grids, Methods, Object-Oriented Programming, Debugging, Algorithms & Pathfinding, and Collections.
- **Dynamic Hazards & Timed Mechanisms**: Synchronized pulsing laser beams with charge-up warning flashes, circuit breaker switches, pressure plates, and timed blast doors with real-time countdowns.
- **Futuristic Visual Design**: Sci-fi dark facility aesthetic (`#070B14`) with glowing neon cyan, electric blue, amber, emerald, and crimson accents. Features smooth robot animations, directional visors, particle sparks, energy portal vortexes, and custom holographic UI overlays.
- **Zero-Dependency Procedural Synthesizer**: Pure Java audio engine utilizing standard `javax.sound.sampled` for retro sci-fi sound effects (footsteps, card pickups, mechanical chimes, error buzzes, laser zaps, victory fanfares) with zero external audio files.
- **Save Progression & Scoring**: Automatic persistence for campaign sector unlocks, 3-star ratings, high scores, data chips, and audio settings stored in `codeescape_progress.properties`.
- **Integrated Question Codex & Settings**: In-game codex to browse and study all 64 Java programming questions with detailed explanations.

---

## Requirements

- **Operating System**: Windows, macOS, or Linux.
- **Java**: Java Development Kit (JDK) 17 or later (JDK 21 or 25 LTS recommended).
- **Environment**: Visual Studio Code, Terminal, or Windows Command Prompt. Zero external libraries, dependencies, or internet connections required.

---

## How to Run

### Option 1: Quick Launch on Windows (Recommended)
Double-click `run.bat` or run in Command Prompt:
```bat
run.bat
```

### Option 2: Command Line (Windows / PowerShell / Bash)
Compile all source files and launch:
```bash
javac *.java
java CodeEscape
```

### Option 3: Visual Studio Code
1. Open the `CodeEscape_Java` folder in Visual Studio Code.
2. Open `CodeEscape.java`.
3. Click the **Run** button (or press `F5` / `Ctrl+F5`) in Visual Studio Code.

---

## Automated QA & Test Suites

The project includes two built-in automated verification test suites:

1. **Campaign & Question Bank Verifier**:
   ```bash
   java GameVerifier
   ```
   *Verifies all 64 questions, checks door/key access logic, tests switch networks and timed doors, and runs BFS pathfinding to prove all 12 levels are 100% solvable.*

2. **Live Engine Integration Verifier**:
   ```bash
   java EngineVerifier
   ```
   *Simulates live gameplay cycles, movement, collision, door unlocks, terminal solving, feedback rendering, and state transitions.*

---

## Controls

| Key | Action |
| --- | --- |
| **W, A, S, D** or **Arrow Keys** | Move Robot / Navigate Menu Options |
| **E**, **ENTER**, or **SPACE** | Interact with Terminal / Confirm Selection |
| **1, 2, 3, 4** | Quick Select Option in Terminal Modal |
| **ESCAPE** | Pause Game / Return to Previous Menu |
| **R** | Quick Restart Current Sector |
| **M** | Toggle Synthesized Audio On/Off |

---

## Project Architecture

- `CodeEscape.java`: Main application entry point and window setup.
- `GameEngine.java`: Coordinates game states, player movement, collision detection, and logic.
- `GamePanel.java`: Double-buffered 60 FPS Swing rendering surface, particle system, and HUD.
- `Player.java`: Robot entity, inventory, shield integrity, directional orientation, and smooth interpolation.
- `Level.java`: Grid layout, entity tracking, pressure plate updates, and hazard processing.
- `LevelManager.java`: Definitions and geometry for all 12 campaign levels.
- `Door.java`: Door state machine (`LOCKED`, `UNLOCKING`, `OPEN`) and access requirement checker.
- `DoorType.java` & `DoorState.java`: Enumerations for door access mechanisms and states.
- `Keycard.java` & `KeyColor.java`: Color-coded keycard items (`RED`, `BLUE`, `GREEN`, `GOLD`, `CYAN`).
- `FacilitySwitch.java`: Security switches and circuit breakers with latching and toggle modes.
- `PressurePlate.java`: Floor sensor pads that dynamically toggle barriers.
- `LaserHazard.java`: Directional pulsing laser beams with active, warning, and disabled states.
- `Terminal.java`: Interactive coding terminals bound to educational questions.
- `DataChip.java`: Collectible research items rewarding exploration and score.
- `Question.java` & `QuestionBank.java`: 64 Java programming questions with code snippets and explanations.
- `SoundManager.java`: Real-time procedural audio wave synthesizer.
- `SaveManager.java`: Local progress persistence for levels, stars, high scores, and audio preferences.
- `ParticleSystem.java`: Lightweight 2D sci-fi particle effects for sparks, portals, and card bursts.
- `GameVerifier.java`: Automated test suite for door logic, 64 questions, and 12-level BFS solvability.
- `EngineVerifier.java`: Headless live engine integration test suite.
