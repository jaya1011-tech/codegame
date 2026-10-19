import java.util.ArrayList;
import java.util.List;

/**
 * LevelManager defines and constructs all 12 campaign levels
 * with verified layouts, entity bindings, and progressive difficulty.
 */
public class LevelManager {
    private static final int LEVEL_COUNT = 12;

    public static int getLevelCount() {
        return LEVEL_COUNT;
    }

    public static Level createLevel(int index) {
        switch (index) {
            case 0: return buildLevel1();
            case 1: return buildLevel2();
            case 2: return buildLevel3();
            case 3: return buildLevel4();
            case 4: return buildLevel5();
            case 5: return buildLevel6();
            case 6: return buildLevel7();
            case 7: return buildLevel8();
            case 8: return buildLevel9();
            case 9: return buildLevel10();
            case 10: return buildLevel11();
            case 11: return buildLevel12();
            default: return buildLevel1();
        }
    }

    private static Level parseBaseGrid(int index, String name, String subtitle, String hint, String[] map) {
        int height = map.length;
        int width = map[0].length();
        Level lvl = new Level(index, name, subtitle, hint, width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                char c = map[y].charAt(x);
                if (c == '#') {
                    lvl.setWall(x, y, true);
                } else if (c == 'P') {
                    lvl.setStart(x, y);
                } else if (c == 'E') {
                    lvl.setExit(x, y);
                }
            }
        }
        return lvl;
    }

    // ==========================================
    // LEVEL 1 — BOOT SEQUENCE
    // Movement tutorial, collision, Gold keycard, Gold door
    // ==========================================
    private static Level buildLevel1() {
        String[] map = {
            "################",
            "#P.............#",
            "#.####.#.#####.#",
            "#.#....#.....#.#",
            "#.#.########.#.#",
            "#.#........#...#",
            "#.########.#.###",
            "#............#E#",
            "################"
        };
        Level lvl = parseBaseGrid(0, "Boot Sequence", "Sector 01 — Diagnostics Lab",
            "Follow the corridor down to recover the Gold Keycard, then unlock the security door.", map);

        // Terminal at (7, 1) introducing Java fundamentals
        lvl.addTerminal(new Terminal(7, 1, 101, 1, "DIAGNOSTIC CONSOLE"));
        // Keycard at (1, 7) - encrypted until Diagnostic Console is solved
        lvl.addKey(new Keycard(1, 7, KeyColor.GOLD, true, 101));
        // Locked door at (13, 7) guarding Exit at (14, 7)
        lvl.addDoor(new Door(13, 7, KeyColor.GOLD));

        // Collectibles
        lvl.addDataChip(new DataChip(13, 1, 1));
        lvl.addDataChip(new DataChip(7, 5, 2));
        lvl.addDataChip(new DataChip(3, 3, 3));

        return lvl;
    }

    // ==========================================
    // LEVEL 2 — ACCESS CONTROL
    // Multi-color keycards (Red & Blue) and matching doors
    // ==========================================
    private static Level buildLevel2() {
        String[] map = {
            "################",
            "#P...#.........#",
            "####.#.#######.#",
            "#....#.#.....#.#",
            "#.####.#.###.#.#",
            "#......#...#.#.#",
            "#.########.#.#.#",
            "#..........#..E#",
            "################"
        };
        Level lvl = parseBaseGrid(1, "Access Control", "Sector 02 — Biometric Vaults",
            "Red doors require Red keycards; Blue doors require Blue keycards. Answer both security challenges to proceed.", map);

        // Terminal 201 for Red Keycard (Variables & Types)
        lvl.addTerminal(new Terminal(1, 5, 201, 8, "RED VAULT CONSOLE"));
        // Red key at (1, 5) encrypted until Terminal 201 is solved
        lvl.addKey(new Keycard(1, 5, KeyColor.RED, true, 201));

        // Terminal 202 for Blue Keycard (Operators & Integer Division)
        lvl.addTerminal(new Terminal(14, 1, 202, 9, "BLUE VAULT CONSOLE"));
        // Blue key at (14, 1) encrypted until Terminal 202 is solved
        lvl.addKey(new Keycard(14, 1, KeyColor.BLUE, true, 202));

        // Red door at (5, 1) blocking access to Blue wing
        lvl.addDoor(new Door(5, 1, KeyColor.RED));
        // Blue door at (14, 5) blocking column 14 corridor to Exit
        lvl.addDoor(new Door(14, 5, KeyColor.BLUE));

        // Chips
        lvl.addDataChip(new DataChip(1, 3, 1));
        lvl.addDataChip(new DataChip(10, 5, 2));
        lvl.addDataChip(new DataChip(14, 3, 3));

        return lvl;
    }

    // ==========================================
    // LEVEL 3 — CONDITIONAL LOGIC
    // Terminal puzzle gates access to security corridor
    // ==========================================
    private static Level buildLevel3() {
        String[] map = {
            "################",
            "#P...#.....#...#",
            "#.##.#.###.#.#.#",
            "#..#...#...#.#.#",
            "##.#####.###.#.#",
            "#........#...#.#",
            "#.########.###.#",
            "#............#E#",
            "################"
        };
        Level lvl = parseBaseGrid(2, "Conditional Logic", "Sector 03 — Logic Gateway",
            "Solve the Boolean condition at the terminal to lower the security gate.", map);

        // Terminal 301 at (4, 1) gates Puzzle Door at (5, 3)
        lvl.addTerminal(new Terminal(4, 1, 301, 15, "IF-STATEMENT TERMINAL"));
        lvl.addDoor(Door.makePuzzleDoor(5, 3, 301));

        // Red key behind the puzzle door at (9, 3) - gated by terminal 301
        lvl.addKey(new Keycard(9, 3, KeyColor.RED, true, 301));
        // Red door at (13, 7)
        lvl.addDoor(new Door(13, 7, KeyColor.RED));

        // Optional terminal for bonus score at (1, 5)
        lvl.addTerminal(new Terminal(1, 5, 302, 19, "AUXILIARY TERMINAL"));

        // Chips
        lvl.addDataChip(new DataChip(11, 1, 1));
        lvl.addDataChip(new DataChip(1, 7, 2));
        lvl.addDataChip(new DataChip(14, 3, 3));

        return lvl;
    }

    // ==========================================
    // LEVEL 4 — SWITCH NETWORK
    // Dual switches must both be activated to open the blast door
    // ==========================================
    private static Level buildLevel4() {
        String[] map = {
            "################",
            "#P...#.....#...#",
            "####.#.###.#.###",
            "#....#...#.#...#",
            "#.######.#.###.#",
            "#.#......#.....#",
            "#.#.##########.#",
            "#..............E",
            "################"
        };
        Level lvl = parseBaseGrid(3, "Switch Network", "Sector 04 — Power Distribution",
            "Activate both Power Relay A and Power Relay B to disengage the main blast door.", map);

        // Switch 1 at (1, 3)
        lvl.addSwitch(new FacilitySwitch(1, 3, 401, "RELAY A"));
        // Switch 2 at (14, 3)
        lvl.addSwitch(new FacilitySwitch(14, 3, 402, "RELAY B"));

        // Switch Door at (10, 5) linked to BOTH switch 401 and 402
        lvl.addDoor(Door.makeSwitchDoor(10, 5, 401, 402));

        // Terminal at (7, 1)
        lvl.addTerminal(new Terminal(7, 1, 403, 14, "POWER LOGIC TERMINAL"));

        // Chips
        lvl.addDataChip(new DataChip(10, 3, 1));
        lvl.addDataChip(new DataChip(3, 5, 2));
        lvl.addDataChip(new DataChip(14, 1, 3));

        return lvl;
    }

    // ==========================================
    // LEVEL 5 — LOOP PROTOCOL
    // Looping corridor, iteration terminal, keycard
    // ==========================================
    private static Level buildLevel5() {
        String[] map = {
            "################",
            "#P.......#.....#",
            "#.######.#.###.#",
            "#.#....#.#...#.#",
            "#.#.##.#.###.#.#",
            "#...#..#.....#.#",
            "#####.########.#",
            "#..............E",
            "################"
        };
        Level lvl = parseBaseGrid(4, "Loop Protocol", "Sector 05 — Iteration Chamber",
            "Understand repetition to navigate the loop array and solve the terminal.", map);

        // Terminal at (3, 3)
        lvl.addTerminal(new Terminal(3, 3, 501, 25, "FOR-LOOP CONSOLE"));
        lvl.addDoor(Door.makePuzzleDoor(8, 1, 501));

        // Green Key at (14, 3) - gated by terminal 501
        lvl.addKey(new Keycard(14, 3, KeyColor.GREEN, true, 501));
        // Green Door at (14, 6)
        lvl.addDoor(new Door(14, 6, KeyColor.GREEN));

        // Chips along the loop
        lvl.addDataChip(new DataChip(1, 5, 1));
        lvl.addDataChip(new DataChip(6, 5, 2));
        lvl.addDataChip(new DataChip(14, 1, 3));

        return lvl;
    }

    // ==========================================
    // LEVEL 6 — LASER CORRIDOR
    // Dynamic pulsing laser beams and breaker switch
    // ==========================================
    private static Level buildLevel6() {
        String[] map = {
            "################",
            "#P.....#.......#",
            "######.#.#####.#",
            "#......#.#.....#",
            "#.######.#.###.#",
            "#........#...#.#",
            "#.##########.#.#",
            "#............#E#",
            "################"
        };
        Level lvl = parseBaseGrid(5, "Laser Corridor", "Sector 06 — Laser Containment",
            "Observe laser timing cycles. Breaker switches can permanently disable hazard emitters.", map);

        // Laser 1: horizontal beam from (1, 3) to (4, 3), cycle 3000ms, active 1400ms
        lvl.addHazard(new LaserHazard(1, 3, 4, 3, 3000, 1400, 0));

        // Breaker Switch 601 at (5, 1) permanently disables Laser 2
        lvl.addSwitch(new FacilitySwitch(5, 1, 601, "BREAKER 01"));
        // Laser 2: vertical beam at x=10 from y=3 to y=5, linked to switch 601
        lvl.addHazard(new LaserHazard(10, 3, 10, 5, 2800, 1600, 500, 0, 601));

        // Red Key at (14, 3) - gated by terminal 602
        lvl.addKey(new Keycard(14, 3, KeyColor.RED, true, 602));
        // Red Door at (13, 7)
        lvl.addDoor(new Door(13, 7, KeyColor.RED));

        // Terminal at (1, 5)
        lvl.addTerminal(new Terminal(1, 5, 602, 17, "SECURITY ARCHIVE"));

        // Chips
        lvl.addDataChip(new DataChip(3, 1, 1));
        lvl.addDataChip(new DataChip(8, 3, 2));
        lvl.addDataChip(new DataChip(14, 5, 3));

        return lvl;
    }

    // ==========================================
    // LEVEL 7 — TIMED ACCESS
    // Security override button opens a timed door for 10 seconds
    // ==========================================
    private static Level buildLevel7() {
        String[] map = {
            "################",
            "#P...#.........#",
            "#.##.#.#######.#",
            "#..#...#.....#.#",
            "##.#####.###.#.#",
            "#........#...#.#",
            "#.########.###.#",
            "#............#E#",
            "################"
        };
        Level lvl = parseBaseGrid(6, "Timed Access", "Sector 07 — Chrono Vault",
            "The override switch unlocks the blast door for 10 seconds. Plan your route!", map);

        // Timed switch at (1, 7) triggers timed door
        lvl.addSwitch(new FacilitySwitch(1, 7, 701, "TIMER OVERRIDE"));
        // Timed door at (9, 5) stays open for 10.0 seconds when triggered
        lvl.addDoor(Door.makeTimedDoor(9, 5, 10.0f, 701));

        // Blue Keycard behind timed door at (14, 1) - gated by terminal 702
        lvl.addKey(new Keycard(14, 1, KeyColor.BLUE, true, 702));
        // Blue Door at (13, 7)
        lvl.addDoor(new Door(13, 7, KeyColor.BLUE));

        // Terminal at (4, 1)
        lvl.addTerminal(new Terminal(4, 1, 702, 34, "CHRONO TERMINAL"));

        // Chips
        lvl.addDataChip(new DataChip(3, 3, 1));
        lvl.addDataChip(new DataChip(7, 3, 2));
        lvl.addDataChip(new DataChip(14, 3, 3));

        return lvl;
    }

    // ==========================================
    // LEVEL 8 — PRESSURE GRID
    // Floor pressure plates that toggle security barriers
    // ==========================================
    private static Level buildLevel8() {
        String[] map = {
            "################",
            "#P.....#.......#",
            "#.####.#.#####.#",
            "#.#....#.#...#.#",
            "#.#.####.#.#.#.#",
            "#...#....#.#...#",
            "#####.####.###.#",
            "#............#E#",
            "################"
        };
        Level lvl = parseBaseGrid(7, "Pressure Grid", "Sector 08 — Pressure Sensor Array",
            "Step onto pressure plates to lower magnetic barriers.", map);

        // Pressure plate 801 at (3, 3) (latching)
        lvl.addPressurePlate(new PressurePlate(3, 3, 801, true));
        // Pressure door at (6, 1) linked to plate 801
        lvl.addDoor(Door.makePressureDoor(6, 1, 801));

        // Pressure plate 802 at (10, 3)
        lvl.addPressurePlate(new PressurePlate(10, 3, 802, true));
        // Pressure door at (10, 6) linked to plate 802
        lvl.addDoor(Door.makePressureDoor(10, 6, 802));

        // Gold Key at (14, 1) - gated by terminal 803
        lvl.addKey(new Keycard(14, 1, KeyColor.GOLD, true, 803));
        // Gold Door at (13, 7) guarding exit at (14, 7)
        lvl.addDoor(new Door(13, 7, KeyColor.GOLD));

        // Terminal at (1, 5)
        lvl.addTerminal(new Terminal(1, 5, 803, 39, "SENSOR DIAGNOSTICS"));

        // Chips
        lvl.addDataChip(new DataChip(4, 5, 1));
        lvl.addDataChip(new DataChip(8, 3, 2));
        lvl.addDataChip(new DataChip(12, 3, 3));

        return lvl;
    }

    // ==========================================
    // LEVEL 9 — DEBUGGING LAB
    // Terminal code snippets with bugs to correct
    // ==========================================
    private static Level buildLevel9() {
        String[] map = {
            "################",
            "#P...#.....#...#",
            "####.#.###.#.#.#",
            "#....#.#...#.#.#",
            "#.####.#.###.#.#",
            "#......#.#...#.#",
            "########.#.###.#",
            "#............#E#",
            "################"
        };
        Level lvl = parseBaseGrid(8, "Debugging Lab", "Sector 09 — Code Inspection Core",
            "Identify programming bugs in the terminal challenges to restore system integrity.", map);

        // Terminal 1: Off-by-one bug at (1, 3)
        lvl.addTerminal(new Terminal(1, 3, 901, 52, "DEBUG CONSOLE ALPHA"));
        lvl.addDoor(Door.makePuzzleDoor(5, 1, 901));

        // Terminal 2: NullPointer bug at (7, 1)
        lvl.addTerminal(new Terminal(7, 1, 902, 51, "DEBUG CONSOLE BETA"));
        lvl.addDoor(Door.makePuzzleDoor(11, 1, 902));

        // Cyan Keycard at (14, 1) - gated by terminal 902
        lvl.addKey(new Keycard(14, 1, KeyColor.CYAN, true, 902));
        // Cyan Door at (13, 7)
        lvl.addDoor(new Door(13, 7, KeyColor.CYAN));

        // Chips
        lvl.addDataChip(new DataChip(3, 5, 1));
        lvl.addDataChip(new DataChip(8, 5, 2));
        lvl.addDataChip(new DataChip(14, 5, 3));

        return lvl;
    }

    // ==========================================
    // LEVEL 10 — MULTI-KEY SECURITY
    // Triple keycards (Red, Blue, Gold), lasers, and multi-wing layout
    // ==========================================
    private static Level buildLevel10() {
        String[] map = {
            "################",
            "#P...#.....#...#",
            "#.##.#.###.#.#.#",
            "#..#.#.#...#.#.#",
            "##.#.#.#.###.#.#",
            "#..#.#...#...#.#",
            "#.##.#####.###.#",
            "#............#E#",
            "################"
        };
        Level lvl = parseBaseGrid(9, "Multi-Key Security", "Sector 10 — High-Security Compound",
            "Requires Red, Blue, and Gold keycards from separate security sectors.", map);

        // Terminal 1001 for Red Key (OOP Constructors)
        lvl.addTerminal(new Terminal(1, 5, 1001, 45, "RED ARCHIVE"));
        lvl.addKey(new Keycard(1, 5, KeyColor.RED, true, 1001));

        // Terminal 1002 for Blue Key (OOP 'this' Keyword)
        lvl.addTerminal(new Terminal(7, 3, 1002, 46, "BLUE ARCHIVE"));
        lvl.addKey(new Keycard(7, 3, KeyColor.BLUE, true, 1002));

        // Terminal 1003 for Gold Key (OOP Encapsulation)
        lvl.addTerminal(new Terminal(14, 1, 1003, 47, "GOLD ARCHIVE"));
        lvl.addKey(new Keycard(14, 1, KeyColor.GOLD, true, 1003));

        // Red door at (4, 1)
        lvl.addDoor(new Door(4, 1, KeyColor.RED));
        // Blue door at (6, 5)
        lvl.addDoor(new Door(6, 5, KeyColor.BLUE));
        // Gold door at (13, 7)
        lvl.addDoor(new Door(13, 7, KeyColor.GOLD));

        // Laser in central corridor
        lvl.addHazard(new LaserHazard(8, 1, 10, 1, 2600, 1200, 0));

        // Chips
        lvl.addDataChip(new DataChip(3, 3, 1));
        lvl.addDataChip(new DataChip(9, 5, 2));
        lvl.addDataChip(new DataChip(14, 5, 3));

        return lvl;
    }

    // ==========================================
    // LEVEL 11 — SYSTEM FAILURE
    // Hazardous emergency lockdown, complex laser grids, algorithmic puzzle
    // ==========================================
    private static Level buildLevel11() {
        String[] map = {
            "################",
            "#P.....#.......#",
            "#.####.#.#####.#",
            "#......#...#...#",
            "#.########.#.###",
            "#........#.#...#",
            "#.######.#.###.#",
            "#............#E#",
            "################"
        };
        Level lvl = parseBaseGrid(10, "System Failure", "Sector 11 — Emergency Meltdown",
            "Containment failure! Evade synchronized laser grids and override terminal locks.", map);

        // Staggered lasers
        lvl.addHazard(new LaserHazard(1, 3, 4, 3, 3000, 1400, 0));
        lvl.addHazard(new LaserHazard(8, 3, 10, 3, 3000, 1400, 1500));

        // Breaker switch at (5, 1)
        lvl.addSwitch(new FacilitySwitch(5, 1, 1101, "GRID SHUTDOWN"));

        // Terminal at (1, 5)
        lvl.addTerminal(new Terminal(1, 5, 1102, 57, "FAILSAFE MAINFRAME"));
        lvl.addDoor(Door.makePuzzleDoor(6, 1, 1102));

        // Green Key at (14, 1) - gated by terminal 1102
        lvl.addKey(new Keycard(14, 1, KeyColor.GREEN, true, 1102));
        // Green Door at (13, 7)
        lvl.addDoor(new Door(13, 7, KeyColor.GREEN));

        // Chips
        lvl.addDataChip(new DataChip(3, 1, 1));
        lvl.addDataChip(new DataChip(12, 5, 2));
        lvl.addDataChip(new DataChip(14, 5, 3));

        return lvl;
    }

    // ==========================================
    // LEVEL 12 — FINAL ESCAPE
    // The grand finale: Multi-phase gauntlet combining all mechanics
    // ==========================================
    private static Level buildLevel12() {
        String[] map = {
            "################",
            "#P...#.....#...#",
            "#.##.#.###.#.#.#",
            "#..#.#...#.#.#.#",
            "##.#####.#.#.#.#",
            "#........#.#.#.#",
            "#.########.###.#",
            "#..............E",
            "################"
        };
        Level lvl = parseBaseGrid(11, "Final Escape", "Sector 12 — Evacuation Shuttle Hangar",
            "The final security gauntlet. Synthesize all logic protocols to reach the shuttle!", map);

        // Terminal at (4, 1): Final master algorithm question
        lvl.addTerminal(new Terminal(4, 1, 1201, 60, "MASTER OVERRIDE"));
        lvl.addDoor(Door.makePuzzleDoor(5, 3, 1201));

        // Switch 1 at (1, 5)
        lvl.addSwitch(new FacilitySwitch(1, 5, 1202, "SHUTTLE RELAY 1"));
        // Switch 2 at (7, 3)
        lvl.addSwitch(new FacilitySwitch(7, 3, 1203, "SHUTTLE RELAY 2"));

        // Switch door at (8, 5) requiring both relays
        lvl.addDoor(Door.makeSwitchDoor(8, 5, 1202, 1203));

        // Timed door at (12, 6) triggered by timed switch at (10, 1)
        lvl.addSwitch(new FacilitySwitch(10, 1, 1204, "AIRLOCK TIMER"));
        lvl.addDoor(Door.makeTimedDoor(12, 6, 12.0f, 1204));

        // Master Gold Keycard at (14, 1) - gated by terminal 1201
        lvl.addKey(new Keycard(14, 1, KeyColor.GOLD, true, 1201));
        // Master Airlock Door at (14, 5)
        lvl.addDoor(new Door(14, 5, KeyColor.GOLD));

        // Laser hazard in corridor
        lvl.addHazard(new LaserHazard(1, 7, 6, 7, 3200, 1500, 0));

        // Collectibles
        lvl.addDataChip(new DataChip(3, 3, 1, 100));
        lvl.addDataChip(new DataChip(10, 3, 2, 100));
        lvl.addDataChip(new DataChip(14, 3, 3, 100));

        return lvl;
    }
}
