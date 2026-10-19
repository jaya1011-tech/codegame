/**
 * Laser hazard emitter that pulses with a visible charge-up warning and firing cycle.
 * Can be linked to security breaker switches to shut down.
 */
public class LaserHazard {
    private final int startX, startY;
    private final int endX, endY;
    private final long cyclePeriodMs;
    private final long activeDurationMs;
    private final long warningDurationMs;
    private final long offsetMs;
    private final int linkedSwitchId; // if > 0, disabled when switch is active

    private long elapsedTimeMs;
    private boolean disabled;

    public LaserHazard(int startX, int startY, int endX, int endY,
                       long cyclePeriodMs, long activeDurationMs, long offsetMs) {
        this(startX, startY, endX, endY, cyclePeriodMs, activeDurationMs, 600, offsetMs, -1);
    }

    public LaserHazard(int startX, int startY, int endX, int endY,
                       long cyclePeriodMs, long activeDurationMs, long warningDurationMs,
                       long offsetMs, int linkedSwitchId) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.cyclePeriodMs = cyclePeriodMs;
        this.activeDurationMs = activeDurationMs;
        this.warningDurationMs = warningDurationMs;
        this.offsetMs = offsetMs;
        this.linkedSwitchId = linkedSwitchId;
        this.elapsedTimeMs = offsetMs;
        this.disabled = false;
    }

    public int getStartX() { return startX; }
    public int getStartY() { return startY; }
    public int getEndX() { return endX; }
    public int getEndY() { return endY; }
    public int getLinkedSwitchId() { return linkedSwitchId; }
    public boolean isDisabled() { return disabled; }
    public void setDisabled(boolean disabled) { this.disabled = disabled; }

    public void update(long dtMs) {
        if (disabled) return;
        elapsedTimeMs = (elapsedTimeMs + dtMs) % cyclePeriodMs;
    }

    public boolean isActive() {
        if (disabled) return false;
        long t = elapsedTimeMs % cyclePeriodMs;
        return t < activeDurationMs;
    }

    public boolean isWarning() {
        if (disabled || isActive()) return false;
        long t = elapsedTimeMs % cyclePeriodMs;
        // warning runs immediately before active cycle starts
        return t >= (cyclePeriodMs - warningDurationMs);
    }

    public boolean contains(int x, int y) {
        if (startX == endX) {
            // vertical beam
            int minY = Math.min(startY, endY);
            int maxY = Math.max(startY, endY);
            return x == startX && y >= minY && y <= maxY;
        } else if (startY == endY) {
            // horizontal beam
            int minX = Math.min(startX, endX);
            int maxX = Math.max(startX, endX);
            return y == startY && x >= minX && x <= maxX;
        }
        return x == startX && y == startY;
    }

    public boolean isDeadlyAt(int x, int y) {
        return !disabled && isActive() && contains(x, y);
    }

    public void reset() {
        this.elapsedTimeMs = offsetMs;
        this.disabled = false;
    }
}
