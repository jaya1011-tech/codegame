/**
 * Keycard entity that can be collected to unlock matching colored security doors.
 */
public class Keycard {
    private final int x;
    private final int y;
    private final KeyColor color;
    private boolean collected;
    private final boolean puzzleGated;
    private final int gatedTerminalId;

    public Keycard(int x, int y, KeyColor color) {
        this(x, y, color, false, -1);
    }

    public Keycard(int x, int y, KeyColor color, boolean puzzleGated, int gatedTerminalId) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.collected = false;
        this.puzzleGated = puzzleGated;
        this.gatedTerminalId = gatedTerminalId;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public KeyColor getColor() { return color; }
    public boolean isCollected() { return collected; }
    public void setCollected(boolean collected) { this.collected = collected; }
    public boolean isPuzzleGated() { return puzzleGated; }
    public int getGatedTerminalId() { return gatedTerminalId; }

    public void reset() {
        this.collected = false;
    }
}
