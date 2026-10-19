import java.awt.Color;

/**
 * Keycard color classification with visual styling colors.
 */
public enum KeyColor {
    RED("RED", new Color(239, 68, 68), new Color(254, 202, 202), new Color(185, 28, 28)),
    BLUE("BLUE", new Color(59, 130, 246), new Color(191, 219, 254), new Color(29, 78, 216)),
    GREEN("GREEN", new Color(34, 197, 94), new Color(187, 247, 208), new Color(21, 128, 61)),
    GOLD("GOLD", new Color(234, 179, 8), new Color(254, 240, 138), new Color(161, 98, 7)),
    CYAN("CYAN", new Color(6, 182, 212), new Color(165, 243, 252), new Color(14, 116, 144));

    private final String displayName;
    private final Color primaryColor;
    private final Color lightAccent;
    private final Color darkAccent;

    KeyColor(String displayName, Color primaryColor, Color lightAccent, Color darkAccent) {
        this.displayName = displayName;
        this.primaryColor = primaryColor;
        this.lightAccent = lightAccent;
        this.darkAccent = darkAccent;
    }

    public String getDisplayName() { return displayName; }
    public Color getPrimaryColor() { return primaryColor; }
    public Color getLightAccent() { return lightAccent; }
    public Color getDarkAccent() { return darkAccent; }
}
