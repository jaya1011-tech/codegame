import javax.swing.*;
import java.awt.*;

/**
 * CodeEscape — Advanced Professional Java Puzzle Adventure
 * Entry point for the application.
 */
public class CodeEscape {
    public static void main(String[] args) {
        // Use system look and feel for native menus / dialogs where applicable
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback to default Swing Look & Feel
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("CodeEscape — Advanced Professional Java Puzzle Adventure");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            GameEngine engine = new GameEngine();
            GamePanel panel = new GamePanel(engine);

            frame.setContentPane(panel);
            frame.pack();
            frame.setMinimumSize(new Dimension(840, 640));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Ensure keyboard focus goes to the game panel
            panel.requestFocusInWindow();
        });
    }
}
