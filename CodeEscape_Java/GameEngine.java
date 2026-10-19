import java.awt.Color;
import java.util.List;

/**
 * GameEngine coordinates gameplay states, movement, collision detection,
 * door access validation, entity updates, and progression.
 */
public class GameEngine {
    private GameState state = GameState.TITLE_SCREEN;
    private int currentLevelIndex = 0;
    private Level currentLevel;
    private Player player;
    private final SaveManager saveManager;
    private final SoundManager soundManager;
    private final ParticleSystem particleSystem;

    // Terminal puzzle modal state
    private Terminal activeTerminal;
    private Question activeQuestion;
    private int selectedOptionIndex = 0;
    private boolean showingFeedback = false;
    private boolean lastAnswerCorrect = false;

    // General UI selection states
    private int menuSelection = 0;
    private int levelSelectCursor = 0;
    private int codexIndex = 0;

    // HUD and feedback
    private String statusMessage = "WELCOME TO CODEESCAPE — INITIATE PROTOCOL";
    private Color statusColor = new Color(34, 211, 238);
    private float statusTimer = 3.0f;
    private float levelTimeSec = 0.0f;

    public GameEngine() {
        this.saveManager = new SaveManager();
        this.soundManager = new SoundManager(saveManager.isSoundEnabled());
        this.particleSystem = new ParticleSystem();
        loadLevel(0);
    }

    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
    public int getCurrentLevelIndex() { return currentLevelIndex; }
    public Level getCurrentLevel() { return currentLevel; }
    public Player getPlayer() { return player; }
    public SaveManager getSaveManager() { return saveManager; }
    public SoundManager getSoundManager() { return soundManager; }
    public ParticleSystem getParticleSystem() { return particleSystem; }
    public Terminal getActiveTerminal() { return activeTerminal; }
    public Question getActiveQuestion() { return activeQuestion; }
    public int getSelectedOptionIndex() { return selectedOptionIndex; }
    public boolean isShowingFeedback() { return showingFeedback; }
    public boolean isLastAnswerCorrect() { return lastAnswerCorrect; }
    public int getMenuSelection() { return menuSelection; }
    public int getLevelSelectCursor() { return levelSelectCursor; }
    public int getCodexIndex() { return codexIndex; }
    public String getStatusMessage() { return statusMessage; }
    public Color getStatusColor() { return statusColor; }
    public float getLevelTimeSec() { return levelTimeSec; }

    public void setStatus(String message, Color color) {
        this.statusMessage = message;
        this.statusColor = color;
        this.statusTimer = 4.0f;
    }

    public void loadLevel(int index) {
        if (index < 0) index = 0;
        if (index >= LevelManager.getLevelCount()) index = LevelManager.getLevelCount() - 1;

        this.currentLevelIndex = index;
        this.currentLevel = LevelManager.createLevel(index);
        if (this.player == null) {
            this.player = new Player(currentLevel.getStartX(), currentLevel.getStartY());
        } else {
            this.player.resetLevelState(currentLevel.getStartX(), currentLevel.getStartY());
        }
        this.levelTimeSec = 0.0f;
        this.particleSystem.clear();
        setStatus("SECTOR " + (currentLevelIndex + 1) + ": " + currentLevel.getName().toUpperCase(), new Color(34, 211, 238));
    }

    public void restartLevel() {
        currentLevel.reset();
        player.resetLevelState(currentLevel.getStartX(), currentLevel.getStartY());
        levelTimeSec = 0.0f;
        particleSystem.clear();
        setStatus("SYSTEM RESTORED — LEVEL RESET", new Color(250, 204, 21));
    }

    public void startCampaign() {
        loadLevel(0);
        state = GameState.PLAYING;
    }

    public void update(float dt) {
        long dtMs = (long) (dt * 1000.0f);
        particleSystem.update(dt);

        if (statusTimer > 0.0f) {
            statusTimer -= dt;
        }

        if (state == GameState.PLAYING) {
            levelTimeSec += dt;
            player.update(dt);
            currentLevel.update(dt, dtMs, player.getGridX(), player.getGridY());

            // Exit vortex particles
            particleSystem.emitPortalRings(
                (currentLevel.getExitX() + 0.5f) * 44.0f,
                (currentLevel.getExitY() + 0.5f) * 44.0f,
                new Color(168, 85, 247)
            );

            // Hazard collision check
            checkHazardCollision();
        }
    }

    private void checkHazardCollision() {
        for (LaserHazard hz : currentLevel.getHazards()) {
            if (hz.isDeadlyAt(player.getGridX(), player.getGridY())) {
                boolean tookDamage = player.takeDamage();
                if (tookDamage) {
                    soundManager.playLaserDamage();
                    particleSystem.emitSparks(
                        (player.getGridX() + 0.5f) * 44.0f,
                        (player.getGridY() + 0.5f) * 44.0f,
                        new Color(239, 68, 68), 24
                    );
                    setStatus("HAZARD DETECTED! SHIELD DAMAGED (-1)", new Color(239, 68, 68));

                    if (player.getShields() <= 0) {
                        state = GameState.GAME_OVER;
                        soundManager.playIncorrect();
                    } else {
                        // Respawn at level start
                        player.setGridPos(currentLevel.getStartX(), currentLevel.getStartY());
                    }
                }
                break;
            }
        }
    }

    public boolean canExitLevel() {
        if (currentLevel == null) return false;
        // 1. All coding terminals in this sector must be solved
        for (Terminal t : currentLevel.getTerminals()) {
            if (!t.isSolved()) return false;
        }
        // 2. All keycards in this sector must be collected
        for (Keycard k : currentLevel.getKeys()) {
            if (!k.isCollected()) return false;
        }
        // 3. All doors in this sector must be open/unlocked
        for (Door d : currentLevel.getDoors()) {
            if (!d.isOpen()) return false;
        }
        return true;
    }

    public void movePlayer(Direction dir) {
        if (state != GameState.PLAYING) return;

        int nx = player.getGridX() + dir.getDx();
        int ny = player.getGridY() + dir.getDy();

        // 1. Boundary check
        if (nx < 0 || nx >= currentLevel.getWidth() || ny < 0 || ny >= currentLevel.getHeight()) {
            return;
        }

        // 2. Wall check
        if (currentLevel.isWall(nx, ny)) {
            setStatus("OBSTACLE: STRUCTURAL WALL", new Color(148, 163, 184));
            return;
        }

        // 3. Door check
        Door door = currentLevel.getDoorAt(nx, ny);
        if (door != null) {
            if (!door.isPassable()) {
                handleDoorAccess(door);
                return; // Cannot move through locked door
            }
        }

        // 4. Exit Portal check - MUST be unsealed before player can enter!
        if (currentLevel.isExitAt(nx, ny)) {
            if (!canExitLevel()) {
                soundManager.playAccessDenied();
                // Explain specific missing requirement
                for (Terminal t : currentLevel.getTerminals()) {
                    if (!t.isSolved()) {
                        setStatus("EXIT SEALED — SOLVE " + t.getLabel() + " FIRST!", new Color(239, 68, 68));
                        return;
                    }
                }
                for (Keycard k : currentLevel.getKeys()) {
                    if (!k.isCollected()) {
                        setStatus("EXIT SEALED — RECOVER " + k.getColor().getDisplayName() + " KEYCARD FIRST!", new Color(239, 68, 68));
                        return;
                    }
                }
                for (Door d : currentLevel.getDoors()) {
                    if (!d.isOpen()) {
                        setStatus("EXIT SEALED — UNLOCK SECURITY BLAST DOORS FIRST!", new Color(239, 68, 68));
                        return;
                    }
                }
                setStatus("EXIT SEALED — SECTOR OBJECTIVES INCOMPLETE!", new Color(239, 68, 68));
                return;
            }
        }

        // 5. Move allowed
        player.moveTo(nx, ny, dir);
        soundManager.playStep();

        // 6. Check collectibles at new tile
        DataChip chip = currentLevel.getDataChipAt(nx, ny);
        if (chip != null && !chip.isCollected()) {
            chip.setCollected(true);
            player.addChip(chip.getPointValue());
            soundManager.playChipCollect();
            particleSystem.emitBurst((nx + 0.5f) * 44.0f, (ny + 0.5f) * 44.0f, new Color(234, 179, 8), 12, 60.0f);
            setStatus("RESEARCH DATA CHIP RECOVERED (+" + chip.getPointValue() + " PTS)", new Color(234, 179, 8));
        }

        // 7. Check keycards
        Keycard key = currentLevel.getKeyAt(nx, ny);
        if (key != null && !key.isCollected()) {
            if (key.isPuzzleGated()) {
                Terminal gatedTerm = currentLevel.getTerminalById(key.getGatedTerminalId());
                if (gatedTerm != null && !gatedTerm.isSolved()) {
                    // Automatically open terminal challenge to decrypt key!
                    openTerminal(gatedTerm);
                    setStatus("KEYCARD ENCRYPTED — ANSWER QUESTION TO DECRYPT KEY!", new Color(250, 204, 21));
                    return;
                }
            }
            key.setCollected(true);
            player.addKey(key.getColor());
            player.addScore(30);
            soundManager.playKeyPickup();
            particleSystem.emitBurst((nx + 0.5f) * 44.0f, (ny + 0.5f) * 44.0f, key.getColor().getPrimaryColor(), 18, 90.0f);
            setStatus(key.getColor().getDisplayName() + " KEYCARD COLLECTED", key.getColor().getPrimaryColor());
        }

        // 8. Check switches
        FacilitySwitch sw = currentLevel.getSwitchAt(nx, ny);
        if (sw != null) {
            boolean active = sw.interact();
            soundManager.playSwitchToggle();
            particleSystem.emitSparks((nx + 0.5f) * 44.0f, (ny + 0.5f) * 44.0f, new Color(34, 197, 94), 8);
            setStatus(sw.getLabel() + " ENGAGED: " + (active ? "ONLINE" : "OFFLINE"), new Color(34, 197, 94));

            // Check if switch triggers a timed door
            for (Door d : currentLevel.getDoors()) {
                if (d.getDoorType() == DoorType.TIMED_DOOR) {
                    for (int swId : d.getLinkedSwitchIds()) {
                        if (swId == sw.getId()) {
                            d.triggerTimedUnlock();
                            soundManager.playDoorUnlock();
                            setStatus("TIMED OVERRIDE ENGAGED! ACCESS OPEN FOR " + (int) d.getTimedDurationSec() + " SECONDS", new Color(234, 179, 8));
                            break;
                        }
                    }
                }
            }
        }

        // 9. Check coding terminal
        Terminal term = currentLevel.getTerminalAt(nx, ny);
        if (term != null && !term.isSolved()) {
            openTerminal(term);
        }

        // 10. Check Exit (only triggers when canExitLevel() passed)
        if (currentLevel.isExitAt(nx, ny)) {
            triggerLevelClear();
        }
    }

    private void handleDoorAccess(Door door) {
        switch (door.getDoorType()) {
            case KEY_DOOR:
                KeyColor req = door.getRequiredKey();
                if (player.hasKey(req)) {
                    door.unlock();
                    soundManager.playDoorUnlock();
                    particleSystem.emitSparks((door.getX() + 0.5f) * 44.0f, (door.getY() + 0.5f) * 44.0f, req.getPrimaryColor(), 16);
                    setStatus("ACCESS GRANTED — " + req.getDisplayName() + " DOOR UNLOCKED", req.getPrimaryColor());
                    player.addScore(20);
                } else {
                    soundManager.playAccessDenied();
                    setStatus("ACCESS DENIED — " + req.getDisplayName() + " KEYCARD REQUIRED", new Color(239, 68, 68));
                }
                break;

            case SWITCH_DOOR:
                soundManager.playAccessDenied();
                setStatus("ACCESS DENIED — POWER RELAY SWITCHES REQUIRED", new Color(239, 68, 68));
                break;

            case PUZZLE_DOOR:
                soundManager.playAccessDenied();
                Terminal t = currentLevel.getTerminalById(door.getLinkedTerminalId());
                String tName = t != null ? t.getLabel() : "CODING TERMINAL";
                setStatus("ACCESS DENIED — OVERRIDE " + tName + " FIRST", new Color(239, 68, 68));
                break;

            case TIMED_DOOR:
                soundManager.playAccessDenied();
                setStatus("ACCESS DENIED — TIMED LOCKDOWN. TRIGGER TIMER SWITCH", new Color(239, 68, 68));
                break;

            case PRESSURE_DOOR:
                soundManager.playAccessDenied();
                setStatus("ACCESS DENIED — FLOOR PRESSURE SENSORS NOT ACTIVE", new Color(239, 68, 68));
                break;
        }
    }

    public void openTerminal(Terminal term) {
        this.activeTerminal = term;
        this.activeQuestion = QuestionBank.getById(term.getQuestionId());
        this.selectedOptionIndex = 0;
        this.showingFeedback = false;
        this.state = GameState.PUZZLE_MODAL;
        soundManager.playTerminalOpen();
    }

    public void submitTerminalAnswer() {
        if (activeQuestion == null || activeTerminal == null) return;

        if (showingFeedback) {
            // Player acknowledged feedback
            showingFeedback = false;
            if (lastAnswerCorrect) {
                activeTerminal.setSolved(true);
                player.addScore(40);
                state = GameState.PLAYING;
                setStatus("MAINFRAME HACK SUCCESSFUL! OVERRIDE ACCEPTED", new Color(34, 197, 94));

                // If player is on an encrypted keycard gated by this terminal, decrypt & collect immediately!
                Keycard keyOnTile = currentLevel.getKeyAt(player.getGridX(), player.getGridY());
                if (keyOnTile != null && !keyOnTile.isCollected()) {
                    keyOnTile.setCollected(true);
                    player.addKey(keyOnTile.getColor());
                    player.addScore(30);
                    soundManager.playKeyPickup();
                    particleSystem.emitBurst((player.getGridX() + 0.5f) * 44.0f, (player.getGridY() + 0.5f) * 44.0f, keyOnTile.getColor().getPrimaryColor(), 18, 90.0f);
                    setStatus("DECRYPTION COMPLETE! " + keyOnTile.getColor().getDisplayName() + " KEYCARD ACQUIRED", keyOnTile.getColor().getPrimaryColor());
                } else {
                    for (Keycard k : currentLevel.getKeys()) {
                        if (!k.isCollected() && k.isPuzzleGated() && k.getGatedTerminalId() == activeTerminal.getTerminalId()) {
                            setStatus(activeTerminal.getLabel() + " OVERRIDDEN — " + k.getColor().getDisplayName() + " KEYCARD DECRYPTED!", new Color(34, 197, 94));
                        }
                    }
                }
            } else {
                state = GameState.PLAYING;
                setStatus("OVERRIDE FAILED — ACCESS LOCKED. RETRY AT TERMINAL", new Color(239, 68, 68));
            }
            return;
        }

        if (activeQuestion.isCorrect(selectedOptionIndex)) {
            lastAnswerCorrect = true;
            showingFeedback = true;
            soundManager.playCorrect();
        } else {
            lastAnswerCorrect = false;
            showingFeedback = true;
            soundManager.playIncorrect();
        }
    }

    public void triggerLevelClear() {
        soundManager.playLevelComplete();
        // Calculate stars: 1 for clear, 2 if all chips found, 3 if full shields intact
        int stars = 1;
        boolean allChips = true;
        for (DataChip c : currentLevel.getDataChips()) {
            if (!c.isCollected()) { allChips = false; break; }
        }
        if (allChips) stars++;
        if (player.getShields() == player.getMaxShields()) stars++;

        saveManager.recordStars(currentLevelIndex, stars);
        saveManager.unlockLevel(currentLevelIndex + 1);
        saveManager.recordScore(player.getScore());

        if (currentLevelIndex == LevelManager.getLevelCount() - 1) {
            state = GameState.VICTORY;
        } else {
            state = GameState.LEVEL_CLEAR;
        }
    }

    public void nextLevel() {
        if (currentLevelIndex < LevelManager.getLevelCount() - 1) {
            loadLevel(currentLevelIndex + 1);
            state = GameState.PLAYING;
        } else {
            state = GameState.VICTORY;
        }
    }

    // Menu navigation methods
    public void menuUp() {
        if (state == GameState.TITLE_SCREEN) {
            menuSelection = (menuSelection - 1 + 5) % 5;
            soundManager.playStep();
        } else if (state == GameState.PAUSED) {
            menuSelection = (menuSelection - 1 + 5) % 5;
            soundManager.playStep();
        } else if (state == GameState.PUZZLE_MODAL && !showingFeedback) {
            if (activeQuestion != null) {
                selectedOptionIndex = (selectedOptionIndex - 1 + activeQuestion.getOptions().length) % activeQuestion.getOptions().length;
                soundManager.playStep();
            }
        } else if (state == GameState.LEVEL_SELECT) {
            if (levelSelectCursor >= 4) {
                levelSelectCursor -= 4;
                soundManager.playStep();
            }
        } else if (state == GameState.CODEX) {
            if (codexIndex > 0) {
                codexIndex--;
                soundManager.playStep();
            }
        }
    }

    public void menuDown() {
        if (state == GameState.TITLE_SCREEN) {
            menuSelection = (menuSelection + 1) % 5;
            soundManager.playStep();
        } else if (state == GameState.PAUSED) {
            menuSelection = (menuSelection + 1) % 5;
            soundManager.playStep();
        } else if (state == GameState.PUZZLE_MODAL && !showingFeedback) {
            if (activeQuestion != null) {
                selectedOptionIndex = (selectedOptionIndex + 1) % activeQuestion.getOptions().length;
                soundManager.playStep();
            }
        } else if (state == GameState.LEVEL_SELECT) {
            if (levelSelectCursor + 4 < LevelManager.getLevelCount()) {
                levelSelectCursor += 4;
                soundManager.playStep();
            }
        } else if (state == GameState.CODEX) {
            if (codexIndex < QuestionBank.getCount() - 1) {
                codexIndex++;
                soundManager.playStep();
            }
        }
    }

    public void menuLeft() {
        if (state == GameState.LEVEL_SELECT) {
            if (levelSelectCursor % 4 > 0) {
                levelSelectCursor--;
                soundManager.playStep();
            }
        } else if (state == GameState.CODEX) {
            if (codexIndex >= 5) {
                codexIndex -= 5;
                soundManager.playStep();
            }
        }
    }

    public void menuRight() {
        if (state == GameState.LEVEL_SELECT) {
            if (levelSelectCursor % 4 < 3 && levelSelectCursor + 1 < LevelManager.getLevelCount()) {
                levelSelectCursor++;
                soundManager.playStep();
            }
        } else if (state == GameState.CODEX) {
            if (codexIndex + 5 < QuestionBank.getCount()) {
                codexIndex += 5;
                soundManager.playStep();
            }
        }
    }

    public void menuSelect() {
        if (state == GameState.TITLE_SCREEN) {
            switch (menuSelection) {
                case 0: startCampaign(); break;
                case 1: state = GameState.LEVEL_SELECT; break;
                case 2: state = GameState.CODEX; break;
                case 3: state = GameState.SETTINGS; break;
                case 4: System.exit(0); break;
            }
        } else if (state == GameState.PAUSED) {
            switch (menuSelection) {
                case 0: state = GameState.PLAYING; break; // Resume
                case 1: restartLevel(); state = GameState.PLAYING; break; // Restart
                case 2: state = GameState.LEVEL_SELECT; break;
                case 3: state = GameState.SETTINGS; break;
                case 4: state = GameState.TITLE_SCREEN; break; // Title Screen
            }
        } else if (state == GameState.LEVEL_SELECT) {
            if (saveManager.isLevelUnlocked(levelSelectCursor)) {
                loadLevel(levelSelectCursor);
                state = GameState.PLAYING;
            } else {
                soundManager.playAccessDenied();
                setStatus("SECTOR LOCKED — COMPLETE PREVIOUS SECTOR FIRST", new Color(239, 68, 68));
            }
        } else if (state == GameState.PUZZLE_MODAL) {
            submitTerminalAnswer();
        } else if (state == GameState.LEVEL_CLEAR) {
            nextLevel();
        } else if (state == GameState.GAME_OVER) {
            restartLevel();
            state = GameState.PLAYING;
        } else if (state == GameState.VICTORY) {
            state = GameState.TITLE_SCREEN;
        } else if (state == GameState.PLAYING) {
            // Contextual interaction with keycard or terminal on current tile
            Keycard k = currentLevel.getKeyAt(player.getGridX(), player.getGridY());
            if (k != null && !k.isCollected() && k.isPuzzleGated()) {
                Terminal gt = currentLevel.getTerminalById(k.getGatedTerminalId());
                if (gt != null && !gt.isSolved()) {
                    openTerminal(gt);
                    return;
                }
            }
            Terminal t = currentLevel.getTerminalAt(player.getGridX(), player.getGridY());
            if (t != null && !t.isSolved()) {
                openTerminal(t);
                return;
            }
        } else if (state == GameState.SETTINGS) {
            toggleSound();
        }
    }

    public void toggleSound() {
        boolean newVal = !saveManager.isSoundEnabled();
        saveManager.setSoundEnabled(newVal);
        soundManager.setEnabled(newVal);
        setStatus("AUDIO SYSTEM: " + (newVal ? "ENABLED" : "MUTED"), newVal ? new Color(34, 197, 94) : new Color(239, 68, 68));
    }
}
