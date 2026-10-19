import java.util.ArrayList;
import java.util.List;

/**
 * Level representation holding map geometry, entities, hazards, terminals, and objectives.
 */
public class Level {
    private final int levelIndex;
    private final String name;
    private final String subtitle;
    private final String hint;
    private final int width;
    private final int height;
    private final boolean[][] walls;

    private int startX, startY;
    private int exitX, exitY;

    private final List<Door> doors = new ArrayList<>();
    private final List<Keycard> keys = new ArrayList<>();
    private final List<FacilitySwitch> switches = new ArrayList<>();
    private final List<PressurePlate> pressurePlates = new ArrayList<>();
    private final List<LaserHazard> hazards = new ArrayList<>();
    private final List<Terminal> terminals = new ArrayList<>();
    private final List<DataChip> dataChips = new ArrayList<>();

    public Level(int levelIndex, String name, String subtitle, String hint, int width, int height) {
        this.levelIndex = levelIndex;
        this.name = name;
        this.subtitle = subtitle;
        this.hint = hint;
        this.width = width;
        this.height = height;
        this.walls = new boolean[height][width];
    }

    public int getLevelIndex() { return levelIndex; }
    public int getLevelNumber() { return levelIndex + 1; }
    public String getName() { return name; }
    public String getSubtitle() { return subtitle; }
    public String getHint() { return hint; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public int getStartX() { return startX; }
    public int getStartY() { return startY; }
    public void setStart(int x, int y) { this.startX = x; this.startY = y; }

    public int getExitX() { return exitX; }
    public int getExitY() { return exitY; }
    public void setExit(int x, int y) { this.exitX = x; this.exitY = y; }

    public void setWall(int x, int y, boolean isWall) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            walls[y][x] = isWall;
        }
    }

    public boolean isWall(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return true;
        return walls[y][x];
    }

    public void addDoor(Door door) {
        doors.add(door);
        setWall(door.getX(), door.getY(), false);
    }
    public void addKey(Keycard key) {
        keys.add(key);
        setWall(key.getX(), key.getY(), false);
    }
    public void addSwitch(FacilitySwitch sw) {
        switches.add(sw);
        setWall(sw.getX(), sw.getY(), false);
    }
    public void addPressurePlate(PressurePlate pp) {
        pressurePlates.add(pp);
        setWall(pp.getX(), pp.getY(), false);
    }
    public void addHazard(LaserHazard hz) { hazards.add(hz); }
    public void addTerminal(Terminal tm) {
        terminals.add(tm);
        setWall(tm.getX(), tm.getY(), false);
    }
    public void addDataChip(DataChip chip) {
        dataChips.add(chip);
        setWall(chip.getX(), chip.getY(), false);
    }

    public List<Door> getDoors() { return doors; }
    public List<Keycard> getKeys() { return keys; }
    public List<FacilitySwitch> getSwitches() { return switches; }
    public List<PressurePlate> getPressurePlates() { return pressurePlates; }
    public List<LaserHazard> getHazards() { return hazards; }
    public List<Terminal> getTerminals() { return terminals; }
    public List<DataChip> getDataChips() { return dataChips; }

    public Door getDoorAt(int x, int y) {
        for (Door d : doors) {
            if (d.getX() == x && d.getY() == y) return d;
        }
        return null;
    }

    public Keycard getKeyAt(int x, int y) {
        for (Keycard k : keys) {
            if (k.getX() == x && k.getY() == y && !k.isCollected()) return k;
        }
        return null;
    }

    public FacilitySwitch getSwitchAt(int x, int y) {
        for (FacilitySwitch s : switches) {
            if (s.getX() == x && s.getY() == y) return s;
        }
        return null;
    }

    public Terminal getTerminalAt(int x, int y) {
        for (Terminal t : terminals) {
            if (t.getX() == x && t.getY() == y) return t;
        }
        return null;
    }

    public PressurePlate getPressurePlateAt(int x, int y) {
        for (PressurePlate p : pressurePlates) {
            if (p.getX() == x && p.getY() == y) return p;
        }
        return null;
    }

    public DataChip getDataChipAt(int x, int y) {
        for (DataChip c : dataChips) {
            if (c.getX() == x && c.getY() == y && !c.isCollected()) return c;
        }
        return null;
    }

    public boolean isExitAt(int x, int y) {
        return x == exitX && y == exitY;
    }

    public FacilitySwitch getSwitchById(int id) {
        for (FacilitySwitch s : switches) {
            if (s.getId() == id) return s;
        }
        return null;
    }

    public PressurePlate getPressurePlateById(int id) {
        for (PressurePlate p : pressurePlates) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    public Terminal getTerminalById(int id) {
        for (Terminal t : terminals) {
            if (t.getTerminalId() == id) return t;
        }
        return null;
    }

    public void update(float dt, long dtMs, int playerX, int playerY) {
        // Update pressure plates
        for (PressurePlate pp : pressurePlates) {
            pp.updateState(pp.getX() == playerX && pp.getY() == playerY);
        }

        // Update hazards
        for (LaserHazard hz : hazards) {
            if (hz.getLinkedSwitchId() > 0) {
                FacilitySwitch sw = getSwitchById(hz.getLinkedSwitchId());
                if (sw != null && sw.isActive()) {
                    hz.setDisabled(true);
                } else {
                    hz.setDisabled(false);
                }
            }
            hz.update(dtMs);
        }

        // Update doors (including switch and pressure linkages)
        for (Door d : doors) {
            if (d.getDoorType() == DoorType.SWITCH_DOOR) {
                boolean allActive = true;
                for (int swId : d.getLinkedSwitchIds()) {
                    FacilitySwitch sw = getSwitchById(swId);
                    if (sw == null || !sw.isActive()) {
                        allActive = false;
                        break;
                    }
                }
                if (allActive && !d.isOpen()) {
                    d.unlock();
                } else if (!allActive && d.isOpen()) {
                    d.forceLock();
                }
            } else if (d.getDoorType() == DoorType.PRESSURE_DOOR) {
                boolean allActive = true;
                for (int plateId : d.getLinkedSwitchIds()) {
                    PressurePlate pp = getPressurePlateById(plateId);
                    if (pp == null || !pp.isActive()) {
                        allActive = false;
                        break;
                    }
                }
                if (allActive && !d.isOpen()) {
                    d.unlock();
                } else if (!allActive && d.isOpen()) {
                    d.forceLock();
                }
            } else if (d.getDoorType() == DoorType.PUZZLE_DOOR) {
                Terminal term = getTerminalById(d.getLinkedTerminalId());
                if (term != null && term.isSolved() && !d.isOpen()) {
                    d.unlock();
                }
            }
            d.update(dt);
        }
    }

    public void reset() {
        for (Door d : doors) d.reset();
        for (Keycard k : keys) k.reset();
        for (FacilitySwitch s : switches) s.reset();
        for (PressurePlate p : pressurePlates) p.reset();
        for (LaserHazard h : hazards) h.reset();
        for (Terminal t : terminals) t.reset();
        for (DataChip c : dataChips) c.reset();
    }
}
