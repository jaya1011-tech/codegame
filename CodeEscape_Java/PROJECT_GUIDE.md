# CodeEscape Project Guide — Advanced Edition

## 1. Project Objective & Setting

CodeEscape is a desktop 2D puzzle adventure that fuses maze exploration with educational computer science problem solving. The game is set in an abandoned high-tech research facility where the player controls a diagnostic robot. The facility's automated defense grids, laser security corridors, biometric blast doors, and logic gateways are locked down. To escape, the player must navigate complex multi-wing sectors, gather colored keycards, activate breaker switches, avoid hazardous laser beams, and solve programming challenges.

---

## 2. Root Cause Analysis & Technical Fix: The Key-and-Door Bug

### The Original Bug
In the original starter implementation, the door check in `movePlayer()` was written as:
```java
} else if (target == 'D') {
    if (hasKey || switchOn) {
        grid[ny][nx] = '.';
        playerX = nx; playerY = ny;
        message = "Door unlocked! Keep going.";
        score += 10;
    } else {
        message = "This door is locked. Find a key or solve the terminal.";
    }
```
Furthermore, `solveTerminal()` set `switchOn = true`:
```java
if (choice == answer) {
    terminalSolved[level] = true;
    switchOn = true;
    score += 30;
}
```

### Why it failed:
1. **Loose Disjunction (`|| switchOn`)**: The door allowed entry if *either* `hasKey` was true *or* `switchOn` was true. In any level where a terminal was solved or a switch was flipped, `switchOn` remained true, allowing the player to walk straight through locked doors without collecting the required key!
2. **Global Untyped Key State**: `hasKey` was a single global boolean. There was no distinction between different doors or different keys. A single key unlocked any door in the level.
3. **Lack of Door State Machine**: The door had no explicit state (e.g. `LOCKED`, `UNLOCKING`, `OPEN`). Stepping into it immediately erased it from the grid (`grid[ny][nx] = '.'`).
4. **No Rejection Feedback**: Attempting to use a wrong key produced no specific feedback explaining which keycard color or mechanism was required.

### The Upgraded Fix
1. **Explicit Object Model (`Door.java`, `DoorState.java`, `DoorType.java`)**:
   - Each door is an explicit instance with coordinates `(x, y)`, `DoorType` (`KEY_DOOR`, `SWITCH_DOOR`, `PUZZLE_DOOR`, `TIMED_DOOR`, `PRESSURE_DOOR`), and `DoorState` (`LOCKED`, `UNLOCKING`, `OPEN`).
2. **Color-Coded Requirements (`KeyColor.java`, `Keycard.java`)**:
   - Keycard doors require matching `KeyColor` (`RED`, `BLUE`, `GREEN`, `GOLD`, `CYAN`).
   - The player maintains a set of collected keys: `Set<KeyColor> keycards`.
   - When a player attempts to enter a locked door cell, `handleDoorAccess(Door door)` is invoked:
     ```java
     case KEY_DOOR:
         KeyColor req = door.getRequiredKey();
         if (player.hasKey(req)) {
             door.unlock();
             soundManager.playDoorUnlock();
             particleSystem.emitSparks(..., req.getPrimaryColor(), 16);
             setStatus("ACCESS GRANTED — " + req.getDisplayName() + " DOOR UNLOCKED", req.getPrimaryColor());
         } else {
             soundManager.playAccessDenied();
             setStatus("ACCESS DENIED — " + req.getDisplayName() + " KEYCARD REQUIRED", new Color(239, 68, 68));
         }
         break;
     ```
3. **Zero Collision Bypass**:
   - Locked doors return `isPassable() == false`. Collision checks prevent the robot from stepping onto the door cell until it has genuinely finished its unlocking animation.
4. **Decoupled Mechanisms**:
   - Switch doors require all linked switch IDs to be active.
   - Puzzle doors require the linked terminal to be solved.
   - Timed doors close automatically after their countdown timer expires.
   - Level reset restores all doors, keys, switches, and hazards to their exact initial states.

---

## 3. The 12-Sector Campaign

1. **Sector 01 — Boot Sequence**: Movement fundamentals, wall collision, recovering the Gold Keycard, unlocking the Gold blast door, and introduction to the diagnostic terminal.
2. **Sector 02 — Access Control**: Multi-color security system. Introduces Red and Blue keycards with corresponding doors. Demonstrates rejection when attempting to open a Blue door with a Red keycard.
3. **Sector 03 — Conditional Logic**: Terminal puzzle gating. Solving a Boolean logic condition at the terminal opens the security gate to the Red keycard vault.
4. **Sector 04 — Switch Network**: Dual security switches. The player must navigate the substation to engage both Power Relay A and Power Relay B simultaneously to lower the central blast shield.
5. **Sector 05 — Loop Protocol**: Iteration chamber with loop challenges. Navigating cyclic corridors and collecting data chips while solving for-loop terminal challenges.
6. **Sector 06 — Laser Corridor**: Dynamic hazard emitters. Demonstrates visible pulsing laser beams with charge-up warning indicators and a breaker switch that permanently disables a hazard line.
7. **Sector 07 — Timed Access**: Chrono vault. Activating an override switch disengages the blast door for a strict 10-second countdown window, demanding optimal route planning.
8. **Sector 08 — Pressure Grid**: Floor pressure sensors. Latching pressure plates toggle magnetic barriers across the sector.
9. **Sector 09 — Debugging Lab**: Code inspection terminal. Presenting bugged Java snippets (off-by-one errors and null pointer exceptions) that the player must diagnose to restore access.
10. **Sector 10 — Multi-Key Security**: High-security compound combining Red, Blue, and Gold keycards, laser corridors, and multi-wing navigation.
11. **Sector 11 — System Failure**: Emergency meltdown. Alternating staggered laser barriers, alarms, and advanced algorithmic mainframe challenges.
12. **Sector 12 — Final Escape**: The evacuation hangar finale. Synthesizes all mechanics: terminal override, synchronized dual relays, timed airlock bypass, master keycard retrieval, and shuttle launch!

---

## 4. Question Bank (64 Verified Challenges)

The question bank contains 64 checked questions distributed across 11 core Java concepts:
- **Java Fundamentals** (Entry point, compilation, javac vs java, comments, naming conventions, JVM bytecode).
- **Variables & Data Types** (Primitives, default field values, integer truncation, final constants, 64-bit long, type casting).
- **Operators & Boolean Logic** (Modulo, short-circuit &&, logical ||, compound assignment, ternary operator, prefix vs postfix).
- **Conditionals** (Else-if cascading, switch fall-through, logical negation !, coordinate boundary testing, nested ifs, string .equals).
- **Loops & Iteration** (For loops, accumulation, break vs continue, while loops, do-while characteristics, nested loops, infinite loops).
- **Arrays & 2D Grids** (Zero-indexing, .length attribute, ArrayIndexOutOfBoundsException, multi-dimensional indexing [r][c], enhanced for-each loop).
- **Methods & Parameters** (Void return, pass-by-value semantics, method overloading, mathematical evaluation, private encapsulation, early returns).
- **Object-Oriented Programming** (Constructors, 'this' keyword, encapsulation, 'extends' inheritance, static methods, super() constructor calls).
- **Debugging & Common Errors** (NullPointerException dereferencing, off-by-one loop conditions, floating point comparison pitfalls, StackOverflowError in recursion, assignment '=' vs comparison '==', try-catch-finally).
- **Algorithms & Route Planning** (Breadth-First Search for shortest paths, linear search O(N) complexity, variable swap algorithms, Manhattan distance, binary search prerequisites, FIFO queue characteristics).
- **Advanced Java & Collections** (String immutability, ArrayList dynamic resizing).

---

## 5. College Evaluator Demonstration Plan (5-Minute Walkthrough)

When demonstrating this project to your professor or evaluator, follow this structured plan:

1. **Introduction (30 seconds)**:
   - Introduce the project: *"CodeEscape is an advanced 2D sci-fi puzzle adventure built in Java Swing. It features 12 levels, 64 programming questions, custom vector graphics, procedural audio synthesis, and a strict access control system."*
2. **Demonstrate the Key-and-Door Bug Fix (1 minute)**:
   - Open **Sector 02 — Access Control** from the Sector Select menu.
   - Walk up to the Blue door: show the feedback *"ACCESS DENIED — BLUE KEYCARD REQUIRED"*.
   - Pick up the Red keycard: walk up to the Blue door again.
   - Demonstrate that having the Red keycard still refuses entry (*"ACCESS DENIED — BLUE KEYCARD REQUIRED"*).
   - Use the Red keycard on the Red door: observe the sliding door animation and spark particles.
   - Retrieve the Blue keycard and unlock the Blue door to finish the sector.
   - Explain the technical fix: *"The original code used `if (hasKey || switchOn)`, allowing any terminal or switch to unlock all doors. We refactored this into an explicit object-oriented state machine with `DoorState`, `DoorType`, and `KeyColor` matching."*
3. **Demonstrate Interactive Coding Terminals (1 minute)**:
   - Open **Sector 03 — Conditional Logic**.
   - Walk to the terminal: press **E** or **ENTER**.
   - Show the in-game holographic modal, code snippet box, and multiple choices.
   - Explain how selecting an option triggers instant technical feedback and detailed educational explanations.
4. **Demonstrate Advanced Mechanics (1 minute)**:
   - Show **Sector 06 (Laser Corridor)**: Point out the pulsating laser beam, the warning charge-up flash, and the circuit breaker switch.
   - Show **Sector 07 (Timed Access)**: Point out the 10-second countdown door.
5. **Demonstrate Question Codex & Quality Assurance (1 minute)**:
   - From the title screen, open the **Question Codex**: show how evaluators can browse all 64 curriculum questions and answers.
   - In terminal, run `java GameVerifier`: demonstrate the automated test suite verifying question validity, bug fix conditions, and BFS pathfinding proof across all 12 levels!
6. **Conclusion**:
   - Conclude by highlighting zero external dependencies, clean OOP modularity, and cross-platform compatibility.

---

## 6. Testing Checklist

- [x] Compilation: All files compile cleanly with `javac *.java`.
- [x] Key-and-Door Bug: Locked doors cannot be opened without matching keys; wrong keys fail.
- [x] Level Resets: Pressing `R` or restarting properly resets doors, keys, and switches.
- [x] Solvability: Headless BFS pathfinding verifies all 12 levels are solvable.
- [x] 64 Questions: All questions verified with valid answers, code snippets, and explanations.
- [x] Zero External Dependencies: Runs on pure standard JDK without external JARs or APIs.
- [x] Audio Synthesizer: Generates procedural sound effects safely with zero external audio assets.
- [x] Local Persistence: High scores, level unlocks, and star ratings persist across launches.
