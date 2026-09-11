package flappybird.core;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Represents the player-controlled bird.
 * Handles physics (gravity, jumping) and rendering.
 */
public class Bird {

    // Position & physics
    public int x;
    public int y;
    public int size;
    private int speed;
    private final int gravity;

    // Visuals
    private final BufferedImage image;

    public Bird(int startX, int startY, int size, BufferedImage image) {
        this.x = startX;
        this.y = startY;
        this.size = size;
        this.speed = 0;
        this.gravity = 1;
        this.image = image;
    }

    /** Apply gravity each game tick. */
    public void applyGravity() {
        speed += gravity;
        y += speed;
    }

    /** Make the bird jump. */
    public void jump() {
        speed = -15;
    }

    /** Reset the bird to a given Y position. */
    public void reset(int startY) {
        this.y = startY;
        this.speed = 0;
    }

    /** Returns current vertical speed (used for rotation). */
    public int getSpeed() {
        return speed;
    }

    /**
     * Draw the bird, rotated based on its current speed.
     */
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldTransform = g2d.getTransform();

        double angle = speed * 2.0;
        g2d.translate(x + size / 2.0, y + size / 2.0);
        g2d.rotate(Math.toRadians(angle));

        if (image != null) {
            g2d.drawImage(image, -size / 2, -size / 2, size, size, null);
        } else {
            g2d.setColor(Color.YELLOW);
            g2d.fillOval(-size / 2, -size / 2, size, size);
        }

        g2d.setTransform(oldTransform);
    }
}
