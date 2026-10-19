import java.util.Arrays;

/**
 * High-tech security blast door with explicit state transitions and access requirements.
 */
public class Door {
    private final int x;
    private final int y;
    private final DoorType doorType;
    private final KeyColor requiredKey; // null if not KEY_DOOR
    private final int[] linkedSwitchIds; // for switch or timed door
    private final int linkedTerminalId;  // for puzzle door
    private final float timedDurationSec; // duration for timed doors

    private DoorState state;
    private float animationProgress; // 0.0f (closed) to 1.0f (open)
    private float timerRemainingSec;

    public Door(int x, int y, KeyColor requiredKey) {
        this(x, y, DoorType.KEY_DOOR, requiredKey, new int[0], -1, 0.0f);
    }

    public static Door makeSwitchDoor(int x, int y, int... switchIds) {
        return new Door(x, y, DoorType.SWITCH_DOOR, null, switchIds, -1, 0.0f);
    }

    public static Door makePuzzleDoor(int x, int y, int terminalId) {
        return new Door(x, y, DoorType.PUZZLE_DOOR, null, new int[0], terminalId, 0.0f);
    }

    public static Door makeTimedDoor(int x, int y, float durationSec, int... switchIds) {
        return new Door(x, y, DoorType.TIMED_DOOR, null, switchIds, -1, durationSec);
    }

    public static Door makePressureDoor(int x, int y, int... switchIds) {
        return new Door(x, y, DoorType.PRESSURE_DOOR, null, switchIds, -1, 0.0f);
    }

    public Door(int x, int y, DoorType doorType, KeyColor requiredKey,
                int[] linkedSwitchIds, int linkedTerminalId, float timedDurationSec) {
        this.x = x;
        this.y = y;
        this.doorType = doorType;
        this.requiredKey = requiredKey;
        this.linkedSwitchIds = linkedSwitchIds != null ? linkedSwitchIds : new int[0];
        this.linkedTerminalId = linkedTerminalId;
        this.timedDurationSec = timedDurationSec;
        this.state = DoorState.LOCKED;
        this.animationProgress = 0.0f;
        this.timerRemainingSec = 0.0f;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public DoorType getDoorType() { return doorType; }
    public KeyColor getRequiredKey() { return requiredKey; }
    public int[] getLinkedSwitchIds() { return linkedSwitchIds; }
    public int getLinkedTerminalId() { return linkedTerminalId; }
    public DoorState getState() { return state; }
    public float getAnimationProgress() { return animationProgress; }
    public float getTimerRemainingSec() { return timerRemainingSec; }
    public float getTimedDurationSec() { return timedDurationSec; }

    public boolean isOpen() {
        return state == DoorState.OPEN;
    }

    public boolean isPassable() {
        return state == DoorState.OPEN || (state == DoorState.UNLOCKING && animationProgress > 0.7f);
    }

    public void triggerTimedUnlock() {
        if (doorType == DoorType.TIMED_DOOR) {
            this.timerRemainingSec = this.timedDurationSec;
            this.state = DoorState.UNLOCKING;
        }
    }

    public void forceOpen() {
        this.state = DoorState.OPEN;
        this.animationProgress = 1.0f;
    }

    public void forceLock() {
        this.state = DoorState.LOCKED;
        this.animationProgress = 0.0f;
        this.timerRemainingSec = 0.0f;
    }

    public void unlock() {
        if (state == DoorState.LOCKED) {
            state = DoorState.UNLOCKING;
        }
    }

    public void update(float dt) {
        if (state == DoorState.UNLOCKING) {
            animationProgress += dt * 3.0f; // opens in ~0.33s
            if (animationProgress >= 1.0f) {
                animationProgress = 1.0f;
                state = DoorState.OPEN;
            }
        } else if (state == DoorState.OPEN && doorType == DoorType.TIMED_DOOR) {
            timerRemainingSec -= dt;
            if (timerRemainingSec <= 0.0f) {
                timerRemainingSec = 0.0f;
                state = DoorState.LOCKED;
                animationProgress = 0.0f;
            }
        }
    }

    public void reset() {
        this.state = DoorState.LOCKED;
        this.animationProgress = 0.0f;
        this.timerRemainingSec = 0.0f;
    }
}
