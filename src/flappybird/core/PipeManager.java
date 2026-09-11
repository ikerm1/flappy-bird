package flappybird.core;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the list of pipe pairs (top + bottom) in the game.
 * Handles spawning, movement, scoring, and collision detection.
 */
public class PipeManager {

    private final List<Pipe> pipes = new ArrayList<>();
    private Pipe lastScoredPipe = null;

    // Configuration
    public static final int PIPE_WIDTH  = 80;
    public static final int PIPE_HEIGHT = 500;
    public static final int PIPE_SPEED  = 6;

    private final int screenWidth;
    private final int groundHeight;

    public PipeManager(int screenWidth, int groundHeight) {
        this.screenWidth = screenWidth;
        this.groundHeight = groundHeight;
    }

    /** Spawn the initial pipe pair at game start. */
    public void spawnInitial(int gapY, int gapSize) {
        pipes.clear();
        lastScoredPipe = null;
        pipes.add(new Pipe(screenWidth, gapY - PIPE_HEIGHT, PIPE_WIDTH, PIPE_HEIGHT));
        pipes.add(new Pipe(screenWidth, gapY + gapSize,     PIPE_WIDTH, PIPE_HEIGHT));
    }

    /** Called every game tick. Moves pipes, spawns new ones, removes off-screen ones. */
    public void update() {
        // Move all pipes
        for (Pipe pipe : pipes) {
            pipe.move(PIPE_SPEED);
        }

        // Spawn a new pair if needed
        boolean needsNew = pipes.isEmpty()
                || pipes.get(pipes.size() - 1).x < screenWidth - 400;
        if (needsNew) {
            spawnRandomPair();
        }

        // Remove off-screen pairs (always comes in pairs: index 0 & 1)
        if (!pipes.isEmpty() && pipes.get(0).isOffScreen()) {
            pipes.remove(0); // top pipe
            if (!pipes.isEmpty()) pipes.remove(0); // bottom pipe
        }
    }

    /** Spawn a new pipe pair with a randomised gap. */
    private void spawnRandomPair() {
        // 70% easy gap, 30% hard gap
        int gapSize = (Math.random() < 0.7)
                ? (int) (Math.random() * 80)  + 200   // easy: 200–280
                : (int) (Math.random() * 30)  + 100;  // hard: 100–130

        int maxGapY = groundHeight - gapSize - 200;
        int minGapY = 100;
        int gapY = (int) (Math.random() * (maxGapY - minGapY)) + minGapY;

        pipes.add(new Pipe(screenWidth, gapY - PIPE_HEIGHT, PIPE_WIDTH, PIPE_HEIGHT));
        pipes.add(new Pipe(screenWidth, gapY + gapSize,     PIPE_WIDTH, PIPE_HEIGHT));
    }

    /**
     * Returns true if the bird has just passed a pipe pair (score point).
     * Call once per tick.
     */
    public boolean checkScore(int birdX) {
        if (pipes.size() >= 2) {
            Pipe topPipe = pipes.get(0);
            if (topPipe != lastScoredPipe && birdX > topPipe.x + topPipe.width) {
                lastScoredPipe = topPipe;
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the bird (given its bounding box) collides with any pipe.
     */
    public boolean checkCollision(int birdX, int birdY, int birdSize) {
        for (Pipe pipe : pipes) {
            if (birdX + birdSize > pipe.x && birdX < pipe.x + pipe.width) {
                if (birdY < pipe.y + pipe.height && birdY + birdSize > pipe.y) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Draw all pipes. Even-indexed pipes are top pipes, odd-indexed are bottom. */
    public void draw(Graphics g) {
        for (int i = 0; i < pipes.size(); i++) {
            pipes.get(i).draw(g, i % 2 == 0);
        }
    }

    public void reset() {
        pipes.clear();
        lastScoredPipe = null;
    }
}
