package flappybird.ui;

import flappybird.audio.SoundManager;

import javax.swing.*;
import java.io.File;

/**
 * The main application window.
 * Creates the JFrame and hosts the GamePanel.
 */
public class GameWindow {

    public GameWindow() {
        // Resolve the assets folder relative to the project root
        String assetsPath = resolveAssetsPath();

        SoundManager soundManager = new SoundManager(assetsPath);
        soundManager.play(SoundManager.BGM_MENU, true);

        GamePanel panel = new GamePanel(assetsPath, soundManager);

        JFrame frame = new JFrame("Flappy Bird");
        frame.add(panel);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        panel.requestFocusInWindow();
    }

    /**
     * Try to find the assets/ folder whether running from IDE or from a JAR.
     * Looks for: ./assets, ../assets, ../../assets
     */
    private String resolveAssetsPath() {
        String[] candidates = { "assets", "../assets", "../../assets" };
        for (String candidate : candidates) {
            File dir = new File(candidate);
            if (dir.isDirectory() && new File(dir, "bird.png").exists()) {
                return dir.getAbsolutePath();
            }
        }
        // Fallback — return working directory / assets
        System.err.println("[GameWindow] Could not locate assets folder. Using ./assets as default.");
        return "assets";
    }
}
