package flappybird.core;

import java.awt.*;

/**
 * Represents a single pipe (top or bottom).
 * Two pipes form a pair — one top, one bottom, with a gap between them.
 */
public class Pipe {

    public int x;
    public int y;
    public final int width;
    public final int height;

    private static final Color PIPE_COLOR     = new Color(34, 139, 34);
    private static final Color PIPE_BORDER    = new Color(0, 100, 0);
    private static final int   CAP_HEIGHT     = 30;
    private static final int   CAP_EXTRA_W    = 10;

    public Pipe(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /** Move the pipe to the left by the given speed. */
    public void move(int speed) {
        x -= speed;
    }

    /** Returns true if the pipe has scrolled off the left side of the screen. */
    public boolean isOffScreen() {
        return x + width < -10;
    }

    /**
     * Draw the pipe with a simple cap effect.
     * The cap sticks out slightly on each side at the open end.
     */
    public void draw(Graphics g, boolean isTopPipe) {
        // Main body
        g.setColor(PIPE_COLOR);
        g.fillRect(x, y, width, height);

        // Border
        g.setColor(PIPE_BORDER);
        g.drawRect(x, y, width, height);

        // Cap (at the gap-facing end)
        g.setColor(PIPE_COLOR);
        int capX = x - CAP_EXTRA_W / 2;
        int capW = width + CAP_EXTRA_W;
        int capY = isTopPipe ? y + height - CAP_HEIGHT : y;
        g.fillRect(capX, capY, capW, CAP_HEIGHT);
        g.setColor(PIPE_BORDER);
        g.drawRect(capX, capY, capW, CAP_HEIGHT);
    }
}
