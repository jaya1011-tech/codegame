/**
 * Floor pressure plate mechanism.
 */
public class PressurePlate {
    private final int x;
    private final int y;
    private final int id;
    private final boolean latches;
    private boolean active;

    public PressurePlate(int x, int y, int id, boolean latches) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.latches = latches;
        this.active = false;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getId() { return id; }
    public boolean doesLatch() { return latches; }
    public boolean isActive() { return active; }

    public void updateState(boolean playerOnPlate) {
        if (latches) {
            if (playerOnPlate) {
                this.active = true;
            }
        } else {
            this.active = playerOnPlate;
        }
    }

    public void reset() {
        this.active = false;
    }
}
