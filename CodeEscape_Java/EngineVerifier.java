/**
 * EngineVerifier tests the live GameEngine state transitions,
 * movement, terminal interaction, encrypted keycards, sealed exit validation,
 * scoring, and level clear cycles.
 */
public class EngineVerifier {
    public static void main(String[] args) {
        System.out.println("Running Live GameEngine Integration Tests...");

        GameEngine engine = new GameEngine();

        // 1. Initial State
        assertCondition(engine.getState() == GameState.TITLE_SCREEN, "Initial state should be TITLE_SCREEN");

        // 2. Start Campaign
        engine.startCampaign();
        assertCondition(engine.getState() == GameState.PLAYING, "State should be PLAYING after startCampaign()");
        assertCondition(engine.getCurrentLevelIndex() == 0, "Current level should be 0");

        Player player = engine.getPlayer();
        Level lvl = engine.getCurrentLevel();
        assertCondition(player.getGridX() == lvl.getStartX(), "Player should start at level startX");
        assertCondition(player.getGridY() == lvl.getStartY(), "Player should start at level startY");

        // 3. Movement and Wall Collision
        int initialX = player.getGridX();
        int initialY = player.getGridY();
        engine.movePlayer(Direction.UP);
        assertCondition(player.getGridX() == initialX && player.getGridY() == initialY, "Wall collision should prevent moving into wall");

        // 4. Verify Exit is SEALED initially
        assertCondition(!engine.canExitLevel(), "Exit portal must be SEALED at start of sector");

        // 5. Move Down towards Encrypted Keycard at (1, 7)
        for (int i = 0; i < 6; i++) {
            engine.movePlayer(Direction.DOWN);
        }
        assertCondition(player.getGridX() == 1 && player.getGridY() == 7, "Player should be at (1, 7)");
        // Since keycard is encrypted, stepping on it triggers PUZZLE_MODAL!
        assertCondition(engine.getState() == GameState.PUZZLE_MODAL, "Encrypted keycard should trigger PUZZLE_MODAL");
        assertCondition(!player.hasKey(KeyColor.GOLD), "Player should NOT have key yet while encrypted");

        // 6. Solve Terminal Question to Decrypt Key
        int correctIdx = engine.getActiveQuestion().getCorrectIndex();
        while (engine.getSelectedOptionIndex() != correctIdx) {
            engine.menuDown();
        }
        engine.submitTerminalAnswer(); // Shows feedback
        assertCondition(engine.isShowingFeedback(), "Engine should show feedback screen");
        assertCondition(engine.isLastAnswerCorrect(), "Answer should be correct");

        engine.submitTerminalAnswer(); // Confirms feedback -> decrypts key!
        assertCondition(engine.getState() == GameState.PLAYING, "State should return to PLAYING");
        assertCondition(player.hasKey(KeyColor.GOLD), "Player should now have decrypted & acquired GOLD keycard");
        assertCondition(player.getScore() > 0, "Score should have increased from question and keycard");

        // 7. Move towards locked door at (13, 7)
        for (int i = 0; i < 11; i++) {
            engine.movePlayer(Direction.RIGHT);
        }
        Door door = lvl.getDoorAt(13, 7);
        assertCondition(door != null, "Door should exist at (13, 7)");
        assertCondition(!door.isOpen(), "Door should be locked initially");

        // Move right into door tile
        engine.movePlayer(Direction.RIGHT);
        assertCondition(door.getState() != DoorState.LOCKED, "Door should unlock since player has GOLD keycard");

        // Update engine to finish door opening animation
        engine.update(1.0f);
        assertCondition(door.isOpen(), "Door should now be fully OPEN");

        // Now all terminals are solved, all keys collected, and door is open!
        assertCondition(engine.canExitLevel(), "canExitLevel() should now be TRUE");

        // 8. Move through open door to exit at (14, 7)
        engine.movePlayer(Direction.RIGHT); // enters (13, 7)
        engine.movePlayer(Direction.RIGHT); // enters (14, 7) [Exit]

        assertCondition(engine.getState() == GameState.LEVEL_CLEAR, "State should transition to LEVEL_CLEAR after reaching exit");

        // 9. Advance to next level
        engine.nextLevel();
        assertCondition(engine.getCurrentLevelIndex() == 1, "Level should advance to 1 (Sector 02)");
        assertCondition(engine.getState() == GameState.PLAYING, "State should return to PLAYING");
        assertCondition(!engine.canExitLevel(), "Exit in Sector 02 must be SEALED initially");

        // 10. Test Pause State
        engine.setState(GameState.PAUSED);
        assertCondition(engine.getState() == GameState.PAUSED, "Engine should pause");
        engine.setState(GameState.PLAYING);
        assertCondition(engine.getState() == GameState.PLAYING, "Engine should unpause");

        // 11. Test Mute
        boolean initialAudio = engine.getSoundManager().isEnabled();
        engine.toggleSound();
        assertCondition(engine.getSoundManager().isEnabled() != initialAudio, "Sound state should toggle");

        System.out.println("[PASS] All live GameEngine integration tests passed successfully!");
    }

    private static void assertCondition(boolean condition, String message) {
        if (!condition) {
            System.err.println("[FAIL] Assertion failed: " + message);
            System.exit(1);
        }
    }
}
