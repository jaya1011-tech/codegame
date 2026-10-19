/**
 * Facility security switch or relay terminal.
 */
public class FacilitySwitch {
    private final int x;
    private final int y;
    private final int id;
    private final String label;
    private final boolean toggleMode; // true = toggles on/off each step, false = latches ON
    private final int sequenceOrder; // 0 if not sequential, 1..N for sequential puzzles
    private boolean active;

    public FacilitySwitch(int x, int y, int id, String label) {
        this(x, y, id, label, false, 0);
    }

    public FacilitySwitch(int x, int y, int id, String label, boolean toggleMode, int sequenceOrder) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.label = label;
        this.toggleMode = toggleMode;
        this.sequenceOrder = sequenceOrder;
        this.active = false;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getId() { return id; }
    public String getLabel() { return label; }
    public boolean isToggleMode() { return toggleMode; }
    public int getSequenceOrder() { return sequenceOrder; }
    public boolean isActive() { return active; }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean interact() {
        if (toggleMode) {
            active = !active;
            return active;
        } else {
            boolean wasActive = active;
            active = true;
            return !wasActive; // returned true if newly activated
        }
    }

    public void reset() {
        this.active = false;
    }
}
