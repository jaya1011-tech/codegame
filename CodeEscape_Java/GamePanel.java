import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;

/**
 * GamePanel handles 60 FPS rendering, visual effects, sci-fi graphics,
 * robot animations, holographic HUD, and interactive UI screens.
 */
public class GamePanel extends JPanel {
    public static final int TILE_SIZE = 46;
    private static final int TOP_BAR_H = 40;
    private static final int BOTTOM_HUD_H = 100;

    private final GameEngine engine;
    private final Timer gameLoopTimer;
    private long lastFrameTimeNano;
    private float globalTimeSec = 0.0f;

    public GamePanel(GameEngine engine) {
        this.engine = engine;
        setPreferredSize(new Dimension(820, 600));
        setBackground(new Color(7, 11, 20));
        setFocusable(true);

        setupKeyboardBindings();

        this.lastFrameTimeNano = System.nanoTime();
        this.gameLoopTimer = new Timer(16, e -> {
            long now = System.nanoTime();
            float dt = (now - lastFrameTimeNano) / 1_000_000_000.0f;
            if (dt > 0.05f) dt = 0.05f; // clamp to prevent delta spikes
            lastFrameTimeNano = now;
            globalTimeSec += dt;

            engine.update(dt);
            repaint();
        });
        gameLoopTimer.start();
    }

    private void setupKeyboardBindings() {
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        bindKey(im, am, "UP", () -> {
            if (engine.getState() == GameState.PLAYING) engine.movePlayer(Direction.UP);
            else engine.menuUp();
        });
        bindKey(im, am, "W", () -> {
            if (engine.getState() == GameState.PLAYING) engine.movePlayer(Direction.UP);
            else engine.menuUp();
        });

        bindKey(im, am, "DOWN", () -> {
            if (engine.getState() == GameState.PLAYING) engine.movePlayer(Direction.DOWN);
            else engine.menuDown();
        });
        bindKey(im, am, "S", () -> {
            if (engine.getState() == GameState.PLAYING) engine.movePlayer(Direction.DOWN);
            else engine.menuDown();
        });

        bindKey(im, am, "LEFT", () -> {
            if (engine.getState() == GameState.PLAYING) engine.movePlayer(Direction.LEFT);
            else engine.menuLeft();
        });
        bindKey(im, am, "A", () -> {
            if (engine.getState() == GameState.PLAYING) engine.movePlayer(Direction.LEFT);
            else engine.menuLeft();
        });

        bindKey(im, am, "RIGHT", () -> {
            if (engine.getState() == GameState.PLAYING) engine.movePlayer(Direction.RIGHT);
            else engine.menuRight();
        });
        bindKey(im, am, "D", () -> {
            if (engine.getState() == GameState.PLAYING) engine.movePlayer(Direction.RIGHT);
            else engine.menuRight();
        });

        bindKey(im, am, "ENTER", engine::menuSelect);
        bindKey(im, am, "SPACE", engine::menuSelect);
        bindKey(im, am, "E", engine::menuSelect);

        // Escape toggles pause or backs out of sub-menus
        bindKey(im, am, "ESCAPE", () -> {
            GameState s = engine.getState();
            if (s == GameState.PLAYING) {
                engine.setState(GameState.PAUSED);
            } else if (s == GameState.PAUSED) {
                engine.setState(GameState.PLAYING);
            } else if (s == GameState.PUZZLE_MODAL) {
                engine.setState(GameState.PLAYING);
            } else if (s == GameState.LEVEL_SELECT || s == GameState.CODEX || s == GameState.SETTINGS) {
                engine.setState(GameState.TITLE_SCREEN);
            }
        });

        // Quick restart level
        bindKey(im, am, "R", () -> {
            if (engine.getState() == GameState.PLAYING) engine.restartLevel();
        });

        // Audio mute toggle
        bindKey(im, am, "M", engine::toggleSound);

        // Number keys for direct option selection in puzzle modal
        for (int i = 1; i <= 4; i++) {
            final int optIdx = i - 1;
            bindKey(im, am, String.valueOf(i), () -> {
                if (engine.getState() == GameState.PUZZLE_MODAL && !engine.isShowingFeedback()) {
                    if (engine.getActiveQuestion() != null && optIdx < engine.getActiveQuestion().getOptions().length) {
                        // simulate navigating to and submitting choice
                        engine.submitTerminalAnswer();
                    }
                }
            });
        }
    }

    private void bindKey(InputMap im, ActionMap am, String key, Runnable action) {
        im.put(KeyStroke.getKeyStroke(key), key);
        am.put(key, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Draw background grid lines
        drawSciFiBackground(g2, w, h);

        switch (engine.getState()) {
            case TITLE_SCREEN:
                drawTitleScreen(g2, w, h);
                break;
            case PLAYING:
            case PAUSED:
            case PUZZLE_MODAL:
            case LEVEL_CLEAR:
            case GAME_OVER:
                drawGameWorld(g2, w, h);
                drawTopBar(g2, w);
                drawBottomHUD(g2, w, h);

                if (engine.getState() == GameState.PAUSED) {
                    drawPauseMenu(g2, w, h);
                } else if (engine.getState() == GameState.PUZZLE_MODAL) {
                    drawPuzzleModal(g2, w, h);
                } else if (engine.getState() == GameState.LEVEL_CLEAR) {
                    drawLevelClearOverlay(g2, w, h);
                } else if (engine.getState() == GameState.GAME_OVER) {
                    drawGameOverOverlay(g2, w, h);
                }
                break;
            case VICTORY:
                drawVictoryScreen(g2, w, h);
                break;
            case LEVEL_SELECT:
                drawLevelSelectScreen(g2, w, h);
                break;
            case CODEX:
                drawCodexScreen(g2, w, h);
                break;
            case SETTINGS:
                drawSettingsScreen(g2, w, h);
                break;
        }

        g2.dispose();
    }

    private void drawSciFiBackground(Graphics2D g, int w, int h) {
        g.setColor(new Color(11, 15, 25));
        g.fillRect(0, 0, w, h);

        // Tech grid lines
        g.setColor(new Color(20, 30, 48, 60));
        g.setStroke(new BasicStroke(1));
        for (int x = 0; x < w; x += 40) g.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += 40) g.drawLine(0, y, w, y);
    }

    private void drawGameWorld(Graphics2D g, int w, int h) {
        Level lvl = engine.getCurrentLevel();
        if (lvl == null) return;

        int gridW = lvl.getWidth() * TILE_SIZE;
        int gridH = lvl.getHeight() * TILE_SIZE;
        int offX = (w - gridW) / 2;
        int offY = TOP_BAR_H + (h - TOP_BAR_H - BOTTOM_HUD_H - gridH) / 2;

        // Draw maze tiles
        for (int y = 0; y < lvl.getHeight(); y++) {
            for (int x = 0; x < lvl.getWidth(); x++) {
                int px = offX + x * TILE_SIZE;
                int py = offY + y * TILE_SIZE;

                if (lvl.isWall(x, y)) {
                    drawWallTile(g, px, py);
                } else {
                    drawFloorTile(g, px, py);
                }
            }
        }

        // Draw Pressure Plates
        for (PressurePlate pp : lvl.getPressurePlates()) {
            drawPressurePlate(g, offX + pp.getX() * TILE_SIZE, offY + pp.getY() * TILE_SIZE, pp.isActive());
        }

        // Draw Switches
        for (FacilitySwitch sw : lvl.getSwitches()) {
            drawSwitch(g, offX + sw.getX() * TILE_SIZE, offY + sw.getY() * TILE_SIZE, sw.isActive(), sw.getLabel());
        }

        // Draw Terminals
        for (Terminal tm : lvl.getTerminals()) {
            drawTerminal(g, offX + tm.getX() * TILE_SIZE, offY + tm.getY() * TILE_SIZE, tm.isSolved());
        }

        // Draw Keycards
        for (Keycard key : lvl.getKeys()) {
            if (!key.isCollected()) {
                drawKeycard(g, offX + key.getX() * TILE_SIZE, offY + key.getY() * TILE_SIZE, key);
            }
        }

        // Draw Data Chips
        for (DataChip chip : lvl.getDataChips()) {
            if (!chip.isCollected()) {
                drawDataChip(g, offX + chip.getX() * TILE_SIZE, offY + chip.getY() * TILE_SIZE);
            }
        }

        // Draw Exit Portal
        drawExitPortal(g, offX + lvl.getExitX() * TILE_SIZE, offY + lvl.getExitY() * TILE_SIZE);

        // Draw Doors
        for (Door door : lvl.getDoors()) {
            drawDoor(g, offX + door.getX() * TILE_SIZE, offY + door.getY() * TILE_SIZE, door);
        }

        // Draw Laser Hazards
        for (LaserHazard hz : lvl.getHazards()) {
            drawLaserHazard(g, offX, offY, hz);
        }

        // Render particles in world space
        Graphics2D gp = (Graphics2D) g.create();
        gp.translate(offX, offY);
        engine.getParticleSystem().render(gp);
        gp.dispose();

        // Draw Player Robot
        Player p = engine.getPlayer();
        if (p != null) {
            float rx = offX + p.getVisualX() * TILE_SIZE;
            float ry = offY + p.getVisualY() * TILE_SIZE;
            drawPlayerRobot(g, rx, ry, p);
        }
    }

    private void drawWallTile(Graphics2D g, int x, int y) {
        g.setColor(new Color(23, 37, 84));
        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);

        g.setColor(new Color(30, 58, 138));
        g.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);

        g.setColor(new Color(59, 130, 246, 90));
        g.drawRect(x + 4, y + 4, TILE_SIZE - 8, TILE_SIZE - 8);

        // Tech screw corners
        g.setColor(new Color(96, 165, 250, 180));
        g.fillRect(x + 5, y + 5, 2, 2);
        g.fillRect(x + TILE_SIZE - 7, y + 5, 2, 2);
        g.fillRect(x + 5, y + TILE_SIZE - 7, 2, 2);
        g.fillRect(x + TILE_SIZE - 7, y + TILE_SIZE - 7, 2, 2);
    }

    private void drawFloorTile(Graphics2D g, int x, int y) {
        g.setColor(new Color(15, 23, 42));
        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);

        g.setColor(new Color(30, 41, 59, 120));
        g.drawRect(x, y, TILE_SIZE, TILE_SIZE);

        g.setColor(new Color(51, 65, 85, 40));
        g.drawLine(x + 8, y + 8, x + TILE_SIZE - 8, y + TILE_SIZE - 8);
    }

    private void drawPressurePlate(Graphics2D g, int x, int y, boolean active) {
        g.setColor(active ? new Color(16, 185, 129, 60) : new Color(100, 116, 139, 40));
        g.fillRoundRect(x + 6, y + 6, TILE_SIZE - 12, TILE_SIZE - 12, 8, 8);

        g.setColor(active ? new Color(52, 211, 153) : new Color(148, 163, 184));
        g.setStroke(new BasicStroke(active ? 2.5f : 1.5f));
        g.drawRoundRect(x + 8, y + 8, TILE_SIZE - 16, TILE_SIZE - 16, 6, 6);

        if (active) {
            g.setColor(new Color(110, 231, 183));
            g.fillOval(x + TILE_SIZE / 2 - 4, y + TILE_SIZE / 2 - 4, 8, 8);
        }
    }

    private void drawSwitch(Graphics2D g, int x, int y, boolean active, String label) {
        g.setColor(new Color(30, 41, 59));
        g.fillRoundRect(x + 8, y + 8, TILE_SIZE - 16, TILE_SIZE - 16, 8, 8);

        Color lightColor = active ? new Color(34, 197, 94) : new Color(239, 68, 68);
        g.setColor(lightColor);
        g.fillOval(x + TILE_SIZE / 2 - 6, y + TILE_SIZE / 2 - 6, 12, 12);

        // Pulsing glow
        g.setColor(new Color(lightColor.getRed(), lightColor.getGreen(), lightColor.getBlue(), 80));
        g.drawOval(x + TILE_SIZE / 2 - 9, y + TILE_SIZE / 2 - 9, 18, 18);
    }

    private void drawTerminal(Graphics2D g, int x, int y, boolean solved) {
        g.setColor(new Color(15, 23, 42));
        g.fillRoundRect(x + 6, y + 6, TILE_SIZE - 12, TILE_SIZE - 12, 8, 8);

        Color screenColor = solved ? new Color(34, 197, 94) : new Color(14, 165, 233);
        g.setColor(screenColor);
        g.drawRoundRect(x + 8, y + 8, TILE_SIZE - 16, TILE_SIZE - 16, 6, 6);

        // Terminal prompt text
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        g.drawString(solved ? "✓" : ">", x + 16, y + 29);

        // Subtle scanline
        if (!solved) {
            float blink = (float) Math.sin(globalTimeSec * 6.0f);
            if (blink > 0) {
                g.fillRect(x + 28, y + 26, 6, 3);
            }
        }
    }

    private void drawKeycard(Graphics2D g, int x, int y, Keycard key) {
        float bob = (float) Math.sin(globalTimeSec * 4.0f) * 3.0f;
        int py = (int) (y + bob);

        KeyColor color = key.getColor();
        boolean isEncrypted = false;
        if (key.isPuzzleGated() && engine.getCurrentLevel() != null) {
            Terminal t = engine.getCurrentLevel().getTerminalById(key.getGatedTerminalId());
            if (t != null && !t.isSolved()) {
                isEncrypted = true;
            }
        }

        g.setColor(isEncrypted ? new Color(55, 65, 81) : color.getPrimaryColor());
        g.fillRoundRect(x + 10, py + 8, TILE_SIZE - 20, TILE_SIZE - 16, 6, 6);

        g.setColor(isEncrypted ? new Color(239, 68, 68) : color.getLightAccent());
        g.setStroke(new BasicStroke(isEncrypted ? 1.8f : 1.0f));
        g.drawRoundRect(x + 10, py + 8, TILE_SIZE - 20, TILE_SIZE - 16, 6, 6);

        if (isEncrypted) {
            g.setColor(new Color(254, 202, 202));
            g.setFont(new Font("SansSerif", Font.BOLD, 8));
            g.drawString("LOCK", x + 12, py + 20);
        } else {
            // Gold chip on card
            g.setColor(new Color(253, 224, 71));
            g.fillRect(x + 15, py + 16, 8, 8);
        }
    }

    private void drawDataChip(Graphics2D g, int x, int y) {
        float bob = (float) Math.cos(globalTimeSec * 3.5f) * 3.0f;
        int cy = (int) (y + TILE_SIZE / 2 + bob);
        int cx = x + TILE_SIZE / 2;

        int[] xs = {cx, cx + 9, cx, cx - 9};
        int[] ys = {cy - 11, cy, cy + 11, cy};

        g.setColor(new Color(234, 179, 8));
        g.fillPolygon(xs, ys, 4);

        g.setColor(new Color(254, 240, 138));
        g.drawPolygon(xs, ys, 4);
    }

    private void drawExitPortal(Graphics2D g, int x, int y) {
        int cx = x + TILE_SIZE / 2;
        int cy = y + TILE_SIZE / 2;

        boolean isUnlocked = engine.canExitLevel();

        if (isUnlocked) {
            // Swirling active neon violet / cyan vortex
            g.setColor(new Color(168, 85, 247, 90));
            g.fillOval(cx - 18, cy - 18, 36, 36);

            g.setColor(new Color(192, 132, 252));
            g.setStroke(new BasicStroke(2.5f));
            g.drawOval(cx - 14, cy - 14, 28, 28);

            g.setColor(Color.WHITE);
            g.fillOval(cx - 5, cy - 5, 10, 10);

            g.setColor(new Color(34, 211, 238));
            g.setFont(new Font("SansSerif", Font.BOLD, 9));
            g.drawString("OPEN", cx - 12, cy + 18);
        } else {
            // Sealed forcefield portal
            g.setColor(new Color(239, 68, 68, 60));
            g.fillOval(cx - 18, cy - 18, 36, 36);

            g.setColor(new Color(239, 68, 68));
            g.setStroke(new BasicStroke(1.8f));
            g.drawOval(cx - 14, cy - 14, 28, 28);

            // Red padlock text
            g.setColor(new Color(254, 202, 202));
            g.setFont(new Font("SansSerif", Font.BOLD, 8));
            g.drawString("SEALED", cx - 16, cy + 3);
        }
    }

    private void drawDoor(Graphics2D g, int x, int y, Door door) {
        if (door.isOpen()) {
            // Draw open doorway pocket
            g.setColor(new Color(15, 23, 42));
            g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
            g.setColor(new Color(34, 197, 94, 80));
            g.drawRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
            return;
        }

        Color doorCol = new Color(220, 38, 38); // default Red
        String badge = "LOCK";

        if (door.getDoorType() == DoorType.KEY_DOOR && door.getRequiredKey() != null) {
            doorCol = door.getRequiredKey().getPrimaryColor();
            badge = door.getRequiredKey().getDisplayName().substring(0, 1);
        } else if (door.getDoorType() == DoorType.SWITCH_DOOR) {
            doorCol = new Color(245, 158, 11);
            badge = "SW";
        } else if (door.getDoorType() == DoorType.PUZZLE_DOOR) {
            doorCol = new Color(14, 165, 233);
            badge = "LOGIC";
        } else if (door.getDoorType() == DoorType.TIMED_DOOR) {
            doorCol = new Color(236, 72, 153);
            badge = String.valueOf((int) Math.ceil(door.getTimerRemainingSec()));
        } else if (door.getDoorType() == DoorType.PRESSURE_DOOR) {
            doorCol = new Color(16, 185, 129);
            badge = "PAD";
        }

        // Blast door frame
        g.setColor(new Color(31, 41, 55));
        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);

        // Blast door sliding panels (animated)
        float progress = door.getAnimationProgress();
        int slide = (int) (progress * (TILE_SIZE / 2));

        g.setColor(doorCol);
        // Left half
        g.fillRect(x + 2, y + 2, (TILE_SIZE / 2) - 2 - slide, TILE_SIZE - 4);
        // Right half
        g.fillRect(x + TILE_SIZE / 2 + slide, y + 2, (TILE_SIZE / 2) - 2 - slide, TILE_SIZE - 4);

        if (progress < 0.3f) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 10));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(badge, x + (TILE_SIZE - fm.stringWidth(badge)) / 2, y + TILE_SIZE / 2 + 4);
        }
    }

    private void drawLaserHazard(Graphics2D g, int offX, int offY, LaserHazard hz) {
        if (hz.isDisabled()) return;

        int sx = offX + hz.getStartX() * TILE_SIZE + TILE_SIZE / 2;
        int sy = offY + hz.getStartY() * TILE_SIZE + TILE_SIZE / 2;
        int ex = offX + hz.getEndX() * TILE_SIZE + TILE_SIZE / 2;
        int ey = offY + hz.getEndY() * TILE_SIZE + TILE_SIZE / 2;

        if (hz.isActive()) {
            // Intense active laser beam
            g.setColor(new Color(239, 68, 68, 120));
            g.setStroke(new BasicStroke(10.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(sx, sy, ex, ey);

            g.setColor(new Color(254, 202, 202));
            g.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(sx, sy, ex, ey);

            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(1.5f));
            g.drawLine(sx, sy, ex, ey);
        } else if (hz.isWarning()) {
            // Flickering charging laser
            float pulse = (float) Math.sin(globalTimeSec * 25.0f);
            if (pulse > 0) {
                g.setColor(new Color(249, 115, 22, 160));
                g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6, 6}, 0));
                g.drawLine(sx, sy, ex, ey);
            }
        }
    }

    private void drawPlayerRobot(Graphics2D g, float rx, float ry, Player p) {
        int x = (int) rx;
        int y = (int) ry;

        // Robot Chassis
        g.setColor(new Color(34, 211, 238));
        g.fillRoundRect(x + 7, y + 7, TILE_SIZE - 14, TILE_SIZE - 14, 12, 12);

        g.setColor(new Color(8, 47, 73));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x + 7, y + 7, TILE_SIZE - 14, TILE_SIZE - 14, 12, 12);

        // Directional Visor
        int eyeOffX = p.getFacing().getDx() * 4;
        int eyeOffY = p.getFacing().getDy() * 4;

        g.setColor(new Color(15, 23, 42));
        g.fillRoundRect(x + 12 + eyeOffX, y + 14 + eyeOffY, TILE_SIZE - 24, 12, 6, 6);

        // Glowing Optic Visor
        g.setColor(new Color(0, 242, 254));
        g.fillRect(x + 15 + eyeOffX, y + 17 + eyeOffY, TILE_SIZE - 30, 6);

        // Antenna with pulsing beacon
        g.setColor(new Color(148, 163, 184));
        g.drawLine(x + TILE_SIZE / 2, y + 7, x + TILE_SIZE / 2, y + 2);
        g.setColor(new Color(250, 204, 21));
        g.fillOval(x + TILE_SIZE / 2 - 2, y + 1, 5, 5);

        // Invulnerability shield flash
        if (p.isInvulnerable()) {
            g.setColor(new Color(239, 68, 68, 100));
            g.drawOval(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
        }
    }

    private void drawTopBar(Graphics2D g, int w) {
        g.setColor(new Color(15, 23, 42, 230));
        g.fillRect(0, 0, w, TOP_BAR_H);

        g.setColor(new Color(51, 65, 85));
        g.drawLine(0, TOP_BAR_H, w, TOP_BAR_H);

        Level lvl = engine.getCurrentLevel();
        if (lvl == null) return;

        // Level Title & Sector
        g.setColor(new Color(34, 211, 238));
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("SECTOR " + String.format("%02d", lvl.getLevelNumber()) + " // " + lvl.getName().toUpperCase(), 16, 25);

        // Timer
        g.setColor(new Color(226, 232, 240));
        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        int totalSec = (int) engine.getLevelTimeSec();
        String timeStr = String.format("TIME: %02d:%02d", totalSec / 60, totalSec % 60);
        g.drawString(timeStr, w - 160, 25);

        // Audio icon
        g.setColor(engine.getSoundManager().isEnabled() ? new Color(34, 197, 94) : new Color(148, 163, 184));
        g.drawString(engine.getSoundManager().isEnabled() ? "AUDIO [ON]" : "AUDIO [MUTE]", w - 280, 25);
    }

    private void drawBottomHUD(Graphics2D g, int w, int h) {
        int y = h - BOTTOM_HUD_H;

        g.setColor(new Color(15, 23, 42, 240));
        g.fillRect(0, y, w, BOTTOM_HUD_H);

        g.setColor(new Color(51, 65, 85));
        g.drawLine(0, y, w, y);

        Player p = engine.getPlayer();
        if (p == null) return;

        // 1. Shield Integrity / Battery Life
        g.setColor(new Color(148, 163, 184));
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("SHIELD CHARGE", 20, y + 22);

        for (int i = 0; i < p.getMaxShields(); i++) {
            boolean filled = i < p.getShields();
            g.setColor(filled ? new Color(34, 211, 238) : new Color(51, 65, 85));
            g.fillRoundRect(20 + i * 26, y + 28, 20, 14, 4, 4);
            g.setColor(new Color(148, 163, 184));
            g.drawRoundRect(20 + i * 26, y + 28, 20, 14, 4, 4);
        }

        // 2. Keycard Inventory
        g.setColor(new Color(148, 163, 184));
        g.drawString("SECURITY KEYCARDS", 125, y + 22);

        KeyColor[] colors = KeyColor.values();
        for (int i = 0; i < colors.length; i++) {
            KeyColor c = colors[i];
            boolean has = p.hasKey(c);
            int kx = 125 + i * 36;
            int ky = y + 28;

            g.setColor(has ? c.getPrimaryColor() : new Color(30, 41, 59));
            g.fillRoundRect(kx, ky, 28, 16, 4, 4);
            g.setColor(has ? c.getLightAccent() : new Color(71, 85, 105));
            g.drawRoundRect(kx, ky, 28, 16, 4, 4);

            g.setColor(has ? Color.WHITE : new Color(100, 116, 139));
            g.setFont(new Font("SansSerif", Font.BOLD, 9));
            g.drawString(c.getDisplayName().substring(0, 1), kx + 10, ky + 12);
        }

        // 3. Score & Data Chips
        g.setColor(new Color(148, 163, 184));
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("SCORE: " + p.getScore(), 330, y + 24);
        g.drawString("CHIPS: " + p.getChipsCollected(), 330, y + 42);

        // 4. Status Message Banner
        g.setColor(new Color(11, 15, 25));
        g.fillRoundRect(20, y + 54, w - 40, 36, 6, 6);

        g.setColor(engine.getStatusColor());
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(20, y + 54, w - 40, 36, 6, 6);

        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        g.drawString("> " + engine.getStatusMessage(), 32, y + 77);

        // Help shortcuts prompt
        g.setColor(new Color(100, 116, 139));
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.drawString("WASD / Arrows: Move | Esc: Pause | R: Reset | M: Mute", w - 320, y + 34);
    }

    private void drawTitleScreen(Graphics2D g, int w, int h) {
        // Glowing sci-fi logo
        g.setFont(new Font("SansSerif", Font.BOLD, 48));
        String title = "CODEESCAPE";
        FontMetrics fm = g.getFontMetrics();
        int tx = (w - fm.stringWidth(title)) / 2;

        g.setColor(new Color(6, 182, 212, 120));
        g.drawString(title, tx - 2, 138);
        g.setColor(new Color(34, 211, 238));
        g.drawString(title, tx, 140);

        g.setFont(new Font("SansSerif", Font.PLAIN, 15));
        g.setColor(new Color(148, 163, 184));
        String subtitle = "ADVANCED PROFESSIONAL JAVA PUZZLE ADVENTURE";
        fm = g.getFontMetrics();
        g.drawString(subtitle, (w - fm.stringWidth(subtitle)) / 2, 175);

        // Menu Options
        String[] menuItems = {
            "START CAMPAIGN",
            "SECTOR SELECT (1-12)",
            "QUESTION CODEX (64 TOPICS)",
            "AUDIO & SETTINGS",
            "EXIT TO DESKTOP"
        };

        int startY = 240;
        int btnW = 320;
        int btnH = 44;
        int btnX = (w - btnW) / 2;

        for (int i = 0; i < menuItems.length; i++) {
            boolean selected = (i == engine.getMenuSelection());
            int by = startY + i * 56;

            g.setColor(selected ? new Color(14, 116, 144) : new Color(30, 41, 59));
            g.fillRoundRect(btnX, by, btnW, btnH, 8, 8);

            g.setColor(selected ? new Color(34, 211, 238) : new Color(71, 85, 105));
            g.setStroke(new BasicStroke(selected ? 2.5f : 1.0f));
            g.drawRoundRect(btnX, by, btnW, btnH, 8, 8);

            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.setColor(selected ? Color.WHITE : new Color(203, 213, 225));
            fm = g.getFontMetrics();
            g.drawString(menuItems[i], btnX + (btnW - fm.stringWidth(menuItems[i])) / 2, by + 27);

            if (selected) {
                g.setColor(new Color(34, 211, 238));
                g.drawString("▶", btnX + 16, by + 27);
            }
        }

        // Footer version info
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.setColor(new Color(100, 116, 139));
        String foot = "Press W/S or UP/DOWN to navigate, ENTER or SPACE to select";
        fm = g.getFontMetrics();
        g.drawString(foot, (w - fm.stringWidth(foot)) / 2, h - 30);
    }

    private void drawPuzzleModal(Graphics2D g, int w, int h) {
        Question q = engine.getActiveQuestion();
        if (q == null) return;

        // Dark dim backdrop
        g.setColor(new Color(3, 7, 18, 220));
        g.fillRect(0, 0, w, h);

        int modalW = 660;
        int modalH = 460;
        int mx = (w - modalW) / 2;
        int my = (h - modalH) / 2;

        // Modal container
        g.setColor(new Color(15, 23, 42));
        g.fillRoundRect(mx, my, modalW, modalH, 12, 12);

        g.setColor(new Color(14, 165, 233));
        g.setStroke(new BasicStroke(2.5f));
        g.drawRoundRect(mx, my, modalW, modalH, 12, 12);

        // Modal Header
        g.setColor(new Color(30, 41, 59));
        g.fillRoundRect(mx + 2, my + 2, modalW - 4, 40, 10, 10);

        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.setColor(new Color(34, 211, 238));
        g.drawString("TERMINAL CHALLENGE // " + q.getCategory().toUpperCase() + " [DIFF: " + q.getDifficulty() + "/5]", mx + 16, my + 26);

        // Prompt
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.setColor(Color.WHITE);
        g.drawString(q.getPrompt(), mx + 20, my + 68);

        int curY = my + 92;

        // Code snippet box (if present)
        if (q.getCodeSnippet() != null && !q.getCodeSnippet().isEmpty()) {
            g.setColor(new Color(11, 15, 25));
            g.fillRoundRect(mx + 20, curY, modalW - 40, 75, 6, 6);
            g.setColor(new Color(51, 65, 85));
            g.drawRoundRect(mx + 20, curY, modalW - 40, 75, 6, 6);

            g.setFont(new Font("Monospaced", Font.PLAIN, 12));
            g.setColor(new Color(125, 211, 252));
            String[] lines = q.getCodeSnippet().split("\n");
            for (int li = 0; li < lines.length && li < 4; li++) {
                g.drawString(lines[li], mx + 30, curY + 20 + li * 16);
            }
            curY += 88;
        }

        // Multiple choices or feedback
        if (engine.isShowingFeedback()) {
            boolean correct = engine.isLastAnswerCorrect();
            Color resColor = correct ? new Color(34, 197, 94) : new Color(239, 68, 68);

            g.setColor(new Color(11, 15, 25));
            g.fillRoundRect(mx + 20, curY, modalW - 40, 130, 8, 8);
            g.setColor(resColor);
            g.setStroke(new BasicStroke(2.0f));
            g.drawRoundRect(mx + 20, curY, modalW - 40, 130, 8, 8);

            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            g.drawString(correct ? "✓ ACCESS GRANTED — COMPILATION SUCCESSFUL!" : "✗ ACCESS DENIED — LOGIC FAULT DETECTED", mx + 36, curY + 34);

            g.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g.setColor(new Color(226, 232, 240));
            // Multi-line explanation
            String[] explLines = wrapText(q.getExplanation(), 70);
            for (int li = 0; li < explLines.length && li < 3; li++) {
                g.drawString(explLines[li], mx + 36, curY + 64 + li * 20);
            }

            // Prompt to continue
            g.setFont(new Font("SansSerif", Font.BOLD, 13));
            g.setColor(new Color(250, 204, 21));
            g.drawString("Press ENTER or SPACE to continue", mx + 36, curY + 120);
        } else {
            // Options list
            String[] options = q.getOptions();
            for (int i = 0; i < options.length; i++) {
                boolean selected = (i == engine.getSelectedOptionIndex());
                int oy = curY + i * 44;

                g.setColor(selected ? new Color(30, 58, 138) : new Color(30, 41, 59));
                g.fillRoundRect(mx + 20, oy, modalW - 40, 36, 6, 6);

                g.setColor(selected ? new Color(96, 165, 250) : new Color(71, 85, 105));
                g.setStroke(new BasicStroke(selected ? 2.0f : 1.0f));
                g.drawRoundRect(mx + 20, oy, modalW - 40, 36, 6, 6);

                g.setFont(new Font("SansSerif", Font.BOLD, 13));
                g.setColor(selected ? Color.WHITE : new Color(203, 213, 225));
                g.drawString("[" + (i + 1) + "]  " + options[i], mx + 36, oy + 23);
            }

            // Hint footer
            g.setFont(new Font("SansSerif", Font.ITALIC, 12));
            g.setColor(new Color(148, 163, 184));
            g.drawString("Hint: " + q.getHint(), mx + 20, modalH + my - 20);
        }
    }

    private void drawPauseMenu(Graphics2D g, int w, int h) {
        g.setColor(new Color(3, 7, 18, 200));
        g.fillRect(0, 0, w, h);

        int pw = 300, ph = 320;
        int px = (w - pw) / 2;
        int py = (h - ph) / 2;

        g.setColor(new Color(15, 23, 42));
        g.fillRoundRect(px, py, pw, ph, 12, 12);
        g.setColor(new Color(59, 130, 246));
        g.drawRoundRect(px, py, pw, ph, 12, 12);

        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.setColor(new Color(34, 211, 238));
        FontMetrics fm = g.getFontMetrics();
        g.drawString("MISSION PAUSED", px + (pw - fm.stringWidth("MISSION PAUSED")) / 2, py + 45);

        String[] pauseOptions = {
            "RESUME PROTOCOL",
            "RESTART SECTOR",
            "SECTOR SELECT",
            "AUDIO SETTINGS",
            "TITLE SCREEN"
        };

        for (int i = 0; i < pauseOptions.length; i++) {
            boolean sel = (i == engine.getMenuSelection());
            int by = py + 75 + i * 44;

            g.setColor(sel ? new Color(14, 116, 144) : new Color(30, 41, 59));
            g.fillRoundRect(px + 20, by, pw - 40, 34, 6, 6);
            g.setColor(sel ? new Color(34, 211, 238) : new Color(71, 85, 105));
            g.drawRoundRect(px + 20, by, pw - 40, 34, 6, 6);

            g.setFont(new Font("SansSerif", Font.BOLD, 13));
            g.setColor(sel ? Color.WHITE : new Color(203, 213, 225));
            fm = g.getFontMetrics();
            g.drawString(pauseOptions[i], px + (pw - fm.stringWidth(pauseOptions[i])) / 2, by + 22);
        }
    }

    private void drawLevelClearOverlay(Graphics2D g, int w, int h) {
        g.setColor(new Color(3, 7, 18, 210));
        g.fillRect(0, 0, w, h);

        int cw = 440, ch = 280;
        int cx = (w - cw) / 2, cy = (h - ch) / 2;

        g.setColor(new Color(15, 23, 42));
        g.fillRoundRect(cx, cy, cw, ch, 12, 12);
        g.setColor(new Color(34, 197, 94));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(cx, cy, cw, ch, 12, 12);

        g.setFont(new Font("SansSerif", Font.BOLD, 26));
        g.setColor(new Color(34, 197, 94));
        FontMetrics fm = g.getFontMetrics();
        g.drawString("SECTOR CLEARED!", cx + (cw - fm.stringWidth("SECTOR CLEARED!")) / 2, cy + 50);

        int stars = engine.getSaveManager().getStars(engine.getCurrentLevelIndex());
        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        g.setColor(new Color(250, 204, 21));
        StringBuilder starStr = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            starStr.append(i < stars ? "★ " : "☆ ");
        }
        String starsText = starStr.toString().trim();
        fm = g.getFontMetrics();
        g.drawString(starsText, cx + (cw - fm.stringWidth(starsText)) / 2, cy + 95);

        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.setColor(new Color(226, 232, 240));
        g.drawString("Sector Time: " + (int) engine.getLevelTimeSec() + " seconds", cx + 50, cy + 140);
        g.drawString("Current Score: " + engine.getPlayer().getScore() + " pts", cx + 50, cy + 165);
        g.drawString("Shields Remaining: " + engine.getPlayer().getShields() + "/3", cx + 50, cy + 190);

        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.setColor(new Color(34, 211, 238));
        String prompt = "Press ENTER or SPACE for Next Sector";
        fm = g.getFontMetrics();
        g.drawString(prompt, cx + (cw - fm.stringWidth(prompt)) / 2, cy + 240);
    }

    private void drawGameOverOverlay(Graphics2D g, int w, int h) {
        g.setColor(new Color(3, 7, 18, 230));
        g.fillRect(0, 0, w, h);

        int cw = 440, ch = 240;
        int cx = (w - cw) / 2, cy = (h - ch) / 2;

        g.setColor(new Color(15, 23, 42));
        g.fillRoundRect(cx, cy, cw, ch, 12, 12);
        g.setColor(new Color(239, 68, 68));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(cx, cy, cw, ch, 12, 12);

        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        g.setColor(new Color(239, 68, 68));
        FontMetrics fm = g.getFontMetrics();
        g.drawString("SYSTEM FAILURE", cx + (cw - fm.stringWidth("SYSTEM FAILURE")) / 2, cy + 60);

        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.setColor(new Color(203, 213, 225));
        String desc = "Robot chassis drained. Shields depleted by facility hazards.";
        fm = g.getFontMetrics();
        g.drawString(desc, cx + (cw - fm.stringWidth(desc)) / 2, cy + 110);

        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.setColor(new Color(250, 204, 21));
        String prompt = "Press ENTER or SPACE to Reboot Sector";
        fm = g.getFontMetrics();
        g.drawString(prompt, cx + (cw - fm.stringWidth(prompt)) / 2, cy + 175);
    }

    private void drawVictoryScreen(Graphics2D g, int w, int h) {
        g.setColor(new Color(7, 11, 20));
        g.fillRect(0, 0, w, h);

        g.setFont(new Font("SansSerif", Font.BOLD, 38));
        g.setColor(new Color(34, 197, 94));
        FontMetrics fm = g.getFontMetrics();
        String title = "FACILITY EVACUATED — VICTORY!";
        g.drawString(title, (w - fm.stringWidth(title)) / 2, 120);

        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g.setColor(new Color(226, 232, 240));
        String sub = "All 12 security sectors breached. Robot successfully escaped.";
        fm = g.getFontMetrics();
        g.drawString(sub, (w - fm.stringWidth(sub)) / 2, 160);

        // Stats card
        int sx = (w - 400) / 2, sy = 200;
        g.setColor(new Color(15, 23, 42));
        g.fillRoundRect(sx, sy, 400, 200, 10, 10);
        g.setColor(new Color(34, 211, 238));
        g.drawRoundRect(sx, sy, 400, 200, 10, 10);

        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("Final Score: " + engine.getPlayer().getScore() + " pts", sx + 40, sy + 50);
        g.drawString("High Score Record: " + engine.getSaveManager().getHighScore() + " pts", sx + 40, sy + 85);
        g.drawString("Data Chips Recovered: " + engine.getPlayer().getChipsCollected(), sx + 40, sy + 120);
        g.drawString("Rank: MASTER SYSTEM ARCHITECT (S+)", sx + 40, sy + 155);

        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.setColor(new Color(250, 204, 21));
        String prompt = "Press ENTER or SPACE to Return to Title Menu";
        fm = g.getFontMetrics();
        g.drawString(prompt, (w - fm.stringWidth(prompt)) / 2, h - 80);
    }

    private void drawLevelSelectScreen(Graphics2D g, int w, int h) {
        g.setColor(new Color(7, 11, 20));
        g.fillRect(0, 0, w, h);

        g.setFont(new Font("SansSerif", Font.BOLD, 26));
        g.setColor(new Color(34, 211, 238));
        FontMetrics fm = g.getFontMetrics();
        g.drawString("SECTOR SELECTION (1-12)", (w - fm.stringWidth("SECTOR SELECTION (1-12)")) / 2, 50);

        int cols = 4;
        int cardW = 160, cardH = 95;
        int startX = (w - (cols * cardW + (cols - 1) * 20)) / 2;
        int startY = 85;

        for (int i = 0; i < 12; i++) {
            int c = i % cols;
            int r = i / cols;
            int x = startX + c * (cardW + 20);
            int y = startY + r * (cardH + 20);

            boolean unlocked = engine.getSaveManager().isLevelUnlocked(i);
            boolean selected = (i == engine.getLevelSelectCursor());
            int stars = engine.getSaveManager().getStars(i);

            g.setColor(selected ? new Color(14, 116, 144) : new Color(15, 23, 42));
            g.fillRoundRect(x, y, cardW, cardH, 8, 8);

            g.setColor(selected ? new Color(34, 211, 238) : (unlocked ? new Color(59, 130, 246) : new Color(71, 85, 105)));
            g.setStroke(new BasicStroke(selected ? 2.5f : 1.0f));
            g.drawRoundRect(x, y, cardW, cardH, 8, 8);

            // Sector number
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.setColor(unlocked ? Color.WHITE : new Color(100, 116, 139));
            g.drawString("SECTOR " + (i + 1), x + 12, y + 25);

            // Level name
            Level tempLvl = LevelManager.createLevel(i);
            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g.drawString(unlocked ? tempLvl.getName() : "[CLASSIFIED]", x + 12, y + 45);

            // Stars
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.setColor(new Color(250, 204, 21));
            StringBuilder sb = new StringBuilder();
            for (int s = 0; s < 3; s++) sb.append(s < stars ? "★" : "☆");
            g.drawString(unlocked ? sb.toString() : "🔒 LOCKED", x + 12, y + 75);
        }

        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.setColor(new Color(148, 163, 184));
        String hint = "Arrow Keys / WASD: Choose Sector | ENTER: Launch | ESC: Back to Title";
        fm = g.getFontMetrics();
        g.drawString(hint, (w - fm.stringWidth(hint)) / 2, h - 35);
    }

    private void drawCodexScreen(Graphics2D g, int w, int h) {
        g.setColor(new Color(7, 11, 20));
        g.fillRect(0, 0, w, h);

        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        g.setColor(new Color(34, 211, 238));
        FontMetrics fm = g.getFontMetrics();
        g.drawString("FACILITY PROGRAMMING CODEX (64 TOPICS)", (w - fm.stringWidth("FACILITY PROGRAMMING CODEX (64 TOPICS)")) / 2, 45);

        Question q = QuestionBank.getById(engine.getCodexIndex() + 1);

        int cx = 60, cy = 70;
        int cw = w - 120, ch = h - 140;

        g.setColor(new Color(15, 23, 42));
        g.fillRoundRect(cx, cy, cw, ch, 10, 10);
        g.setColor(new Color(59, 130, 246));
        g.drawRoundRect(cx, cy, cw, ch, 10, 10);

        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.setColor(new Color(34, 211, 238));
        g.drawString("ENTRY [" + q.getId() + "/64] — " + q.getCategory().toUpperCase() + " (DIFFICULTY " + q.getDifficulty() + "/5)", cx + 20, cy + 32);

        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.setColor(Color.WHITE);
        g.drawString(q.getPrompt(), cx + 20, cy + 62);

        int currY = cy + 90;
        if (q.getCodeSnippet() != null && !q.getCodeSnippet().isEmpty()) {
            g.setColor(new Color(11, 15, 25));
            g.fillRoundRect(cx + 20, currY, cw - 40, 70, 6, 6);
            g.setColor(new Color(125, 211, 252));
            g.setFont(new Font("Monospaced", Font.PLAIN, 12));
            String[] lines = q.getCodeSnippet().split("\n");
            for (int li = 0; li < lines.length && li < 4; li++) {
                g.drawString(lines[li], cx + 30, currY + 20 + li * 15);
            }
            currY += 80;
        }

        // Correct Answer
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.setColor(new Color(34, 197, 94));
        g.drawString("Correct Answer: " + q.getOptions()[q.getCorrectIndex()], cx + 20, currY + 20);

        // Explanation
        g.setColor(new Color(203, 213, 225));
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        String[] expl = wrapText("Technical Explanation: " + q.getExplanation(), 75);
        for (int li = 0; li < expl.length && li < 4; li++) {
            g.drawString(expl[li], cx + 20, currY + 50 + li * 20);
        }

        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.setColor(new Color(148, 163, 184));
        String nav = "UP/DOWN: Next Entry | LEFT/RIGHT: Jump 5 Entries | ESC: Title Screen";
        fm = g.getFontMetrics();
        g.drawString(nav, (w - fm.stringWidth(nav)) / 2, h - 30);
    }

    private void drawSettingsScreen(Graphics2D g, int w, int h) {
        g.setColor(new Color(7, 11, 20));
        g.fillRect(0, 0, w, h);

        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        g.setColor(new Color(34, 211, 238));
        FontMetrics fm = g.getFontMetrics();
        g.drawString("SETTINGS & CONTROLS", (w - fm.stringWidth("SETTINGS & CONTROLS")) / 2, 70);

        int sx = (w - 480) / 2, sy = 120;
        g.setColor(new Color(15, 23, 42));
        g.fillRoundRect(sx, sy, 480, 320, 10, 10);
        g.setColor(new Color(59, 130, 246));
        g.drawRoundRect(sx, sy, 480, 320, 10, 10);

        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.setColor(Color.WHITE);
        g.drawString("AUDIO SYSTEM (SYNTHESIZER)", sx + 30, sy + 45);

        boolean sound = engine.getSoundManager().isEnabled();
        g.setColor(sound ? new Color(34, 197, 94) : new Color(239, 68, 68));
        g.drawString(sound ? "[ENABLED] - PRESS ENTER/M TO MUTE" : "[MUTED] - PRESS ENTER/M TO ENABLE", sx + 30, sy + 75);

        g.setColor(new Color(148, 163, 184));
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("CONTROLS REFERENCE", sx + 30, sy + 125);

        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.setColor(new Color(226, 232, 240));
        g.drawString("• Arrow Keys or W, A, S, D — Move Robot", sx + 30, sy + 155);
        g.drawString("• E, ENTER, or SPACE — Confirm / Interact with Terminals", sx + 30, sy + 185);
        g.drawString("• ESC — Pause Game / Access Mission Menu", sx + 30, sy + 215);
        g.drawString("• R — Quick Restart Current Sector", sx + 30, sy + 245);
        g.drawString("• M — Toggle Synthesized Sci-Fi SFX", sx + 30, sy + 275);

        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.setColor(new Color(250, 204, 21));
        String back = "Press ESC or ENTER to Return";
        fm = g.getFontMetrics();
        g.drawString(back, (w - fm.stringWidth(back)) / 2, h - 50);
    }

    private String[] wrapText(String text, int maxChars) {
        if (text == null) return new String[0];
        String[] words = text.split(" ");
        java.util.List<String> lines = new java.util.ArrayList<>();
        StringBuilder sb = new StringBuilder();

        for (String w : words) {
            if (sb.length() + w.length() + 1 > maxChars) {
                lines.add(sb.toString());
                sb = new StringBuilder();
            }
            if (sb.length() > 0) sb.append(" ");
            sb.append(w);
        }
        if (sb.length() > 0) lines.add(sb.toString());
        return lines.toArray(new String[0]);
    }
}
