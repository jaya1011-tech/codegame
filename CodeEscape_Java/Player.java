import java.util.EnumSet;
import java.util.Set;

/**
 * Player robot entity with directional orientation, inventory, shields, and smooth movement.
 */
public class Player {
    private int gridX;
    private int gridY;
    private float visualX;
    private float visualY;
    private Direction facing;
    private float walkCycle;
    private final Set<KeyColor> keycards;

    private int shields; // 3 battery/shield lives
    private final int maxShields = 3;
    private int score;
    private int chipsCollected;
    private float invulnerableTimer; // temporary invulnerability after hazard hit

    public Player(int startX, int startY) {
        this.gridX = startX;
        this.gridY = startY;
        this.visualX = startX;
        this.visualY = startY;
        this.facing = Direction.DOWN;
        this.walkCycle = 0.0f;
        this.keycards = EnumSet.noneOf(KeyColor.class);
        this.shields = maxShields;
        this.score = 0;
        this.chipsCollected = 0;
        this.invulnerableTimer = 0.0f;
    }

    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
    public float getVisualX() { return visualX; }
    public float getVisualY() { return visualY; }
    public Direction getFacing() { return facing; }
    public float getWalkCycle() { return walkCycle; }
    public Set<KeyColor> getKeycards() { return keycards; }
    public int getShields() { return shields; }
    public int getMaxShields() { return maxShields; }
    public int getScore() { return score; }
    public int getChipsCollected() { return chipsCollected; }
    public boolean isInvulnerable() { return invulnerableTimer > 0.0f; }

    public void setGridPos(int gx, int gy) {
        this.gridX = gx;
        this.gridY = gy;
        this.visualX = gx;
        this.visualY = gy;
    }

    public void moveTo(int gx, int gy, Direction dir) {
        this.gridX = gx;
        this.gridY = gy;
        this.facing = dir;
        this.walkCycle += 1.0f;
    }

    public void update(float dt) {
        // Smooth visual position interpolation towards grid position
        float speed = 14.0f;
        visualX += (gridX - visualX) * Math.min(1.0f, speed * dt);
        visualY += (gridY - visualY) * Math.min(1.0f, speed * dt);

        if (invulnerableTimer > 0.0f) {
            invulnerableTimer -= dt;
            if (invulnerableTimer < 0.0f) invulnerableTimer = 0.0f;
        }
    }

    public boolean hasKey(KeyColor color) {
        return keycards.contains(color);
    }

    public void addKey(KeyColor color) {
        keycards.add(color);
    }

    public void removeKey(KeyColor color) {
        keycards.remove(color);
    }

    public void clearKeys() {
        keycards.clear();
    }

    public void addScore(int points) {
        this.score += points;
    }

    public void deductScore(int points) {
        this.score = Math.max(0, this.score - points);
    }

    public void addChip(int points) {
        this.chipsCollected++;
        this.score += points;
    }

    public boolean takeDamage() {
        if (invulnerableTimer > 0.0f) return false;
        shields--;
        invulnerableTimer = 1.5f; // 1.5s invulnerability
        return true;
    }

    public void restoreShield() {
        if (shields < maxShields) shields++;
    }

    public void resetLevelState(int startX, int startY) {
        this.gridX = startX;
        this.gridY = startY;
        this.visualX = startX;
        this.visualY = startY;
        this.facing = Direction.DOWN;
        this.keycards.clear();
        this.shields = maxShields;
        this.invulnerableTimer = 0.0f;
    }
}
