/**
 * Collectible research data chip rewarding exploration and efficiency.
 */
public class DataChip {
    private final int x;
    private final int y;
    private final int chipId;
    private final int pointValue;
    private boolean collected;

    public DataChip(int x, int y, int chipId) {
        this(x, y, chipId, 50);
    }

    public DataChip(int x, int y, int chipId, int pointValue) {
        this.x = x;
        this.y = y;
        this.chipId = chipId;
        this.pointValue = pointValue;
        this.collected = false;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getChipId() { return chipId; }
    public int getPointValue() { return pointValue; }
    public boolean isCollected() { return collected; }
    public void setCollected(boolean collected) { this.collected = collected; }

    public void reset() {
        this.collected = false;
    }
}
