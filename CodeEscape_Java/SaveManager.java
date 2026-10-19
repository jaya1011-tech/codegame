import java.io.*;
import java.util.Properties;

/**
 * Manages local game persistence: unlocked levels, stars, high scores, and audio settings.
 */
public class SaveManager {
    private static final String FILE_NAME = "codeescape_progress.properties";
    private final File saveFile;
    private final Properties props = new Properties();

    private int maxUnlockedLevel = 0; // 0-indexed (Level 1 is 0)
    private final int[] levelStars = new int[12];
    private int highScore = 0;
    private boolean soundEnabled = true;

    public SaveManager() {
        // Prefer local directory, fallback to user.home
        File local = new File(FILE_NAME);
        if (local.canWrite() || !local.exists()) {
            this.saveFile = local;
        } else {
            this.saveFile = new File(System.getProperty("user.home"), FILE_NAME);
        }
        load();
    }

    public void load() {
        if (!saveFile.exists()) return;
        try (InputStream in = new FileInputStream(saveFile)) {
            props.load(in);
            maxUnlockedLevel = Integer.parseInt(props.getProperty("unlockedLevel", "0"));
            highScore = Integer.parseInt(props.getProperty("highScore", "0"));
            soundEnabled = Boolean.parseBoolean(props.getProperty("soundEnabled", "true"));
            for (int i = 0; i < 12; i++) {
                levelStars[i] = Integer.parseInt(props.getProperty("stars_" + i, "0"));
            }
        } catch (Exception e) {
            System.err.println("Notice: Using fresh game state (" + e.getMessage() + ")");
        }
    }

    public void save() {
        try (OutputStream out = new FileOutputStream(saveFile)) {
            props.setProperty("unlockedLevel", String.valueOf(maxUnlockedLevel));
            props.setProperty("highScore", String.valueOf(highScore));
            props.setProperty("soundEnabled", String.valueOf(soundEnabled));
            for (int i = 0; i < 12; i++) {
                props.setProperty("stars_" + i, String.valueOf(levelStars[i]));
            }
            props.store(out, "CodeEscape Campaign Progress");
        } catch (Exception e) {
            System.err.println("Notice: Could not write save file (" + e.getMessage() + ")");
        }
    }

    public int getMaxUnlockedLevel() { return maxUnlockedLevel; }

    public boolean isLevelUnlocked(int levelIndex) {
        return levelIndex <= maxUnlockedLevel;
    }

    public void unlockLevel(int levelIndex) {
        if (levelIndex > maxUnlockedLevel && levelIndex < 12) {
            maxUnlockedLevel = levelIndex;
            save();
        }
    }

    public int getStars(int levelIndex) {
        if (levelIndex >= 0 && levelIndex < 12) return levelStars[levelIndex];
        return 0;
    }

    public void recordStars(int levelIndex, int stars) {
        if (levelIndex >= 0 && levelIndex < 12) {
            if (stars > levelStars[levelIndex]) {
                levelStars[levelIndex] = stars;
                save();
            }
        }
    }

    public int getHighScore() { return highScore; }

    public void recordScore(int score) {
        if (score > highScore) {
            highScore = score;
            save();
        }
    }

    public boolean isSoundEnabled() { return soundEnabled; }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
        save();
    }

    public void resetProgress() {
        maxUnlockedLevel = 0;
        for (int i = 0; i < 12; i++) levelStars[i] = 0;
        highScore = 0;
        save();
    }
}
