package flappybird;

import flappybird.ui.GameWindow;

/**
 * FlappyBird - Main entry point
 * Launches the game window.
 */
public class Main {
    public static void main(String[] args) {
        // Run on the Event Dispatch Thread (Swing best practice)
        javax.swing.SwingUtilities.invokeLater(() -> new GameWindow());
    }
}
