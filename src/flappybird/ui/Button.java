package flappybird.ui;

import java.awt.*;

/**
 * A simple clickable button drawn with Graphics.
 */
public class Button {

    private final int x, y, width, height;
    private final String text;
    private final Color color;

    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 30);

    public Button(int x, int y, int width, int height, String text, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.color = color;
    }

    public void draw(Graphics g) {
        // Background
        g.setColor(color);
        g.fillRoundRect(x, y, width, height, 20, 20);

        // Border
        g.setColor(Color.WHITE);
        g.drawRoundRect(x, y, width, height, 20, 20);

        // Centered text
        g.setFont(BUTTON_FONT);
        FontMetrics fm = g.getFontMetrics();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        int textY = y + ((height - fm.getHeight()) / 2) + fm.getAscent();
        g.setColor(Color.WHITE);
        g.drawString(text, textX, textY);
    }

    /** Returns true if the mouse click coordinates are inside this button. */
    public boolean isClicked(int mouseX, int mouseY) {
        return mouseX > x && mouseX < x + width
            && mouseY > y && mouseY < y + height;
    }
}
