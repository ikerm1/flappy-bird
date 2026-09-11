package flappybird.ui;

import java.awt.*;

/**
 * A draggable horizontal slider for adjusting volume settings.
 */
public class Slider {

    private final int x, y, width;
    private final int height = 20;
    private final String label;
    public int value; // 0–100

    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 25);
    private static final Font VALUE_FONT = new Font("Arial", Font.PLAIN, 20);

    public Slider(int x, int y, int width, String label, int startValue) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.label = label;
        this.value = startValue;
    }

    public void draw(Graphics g) {
        // Label
        g.setColor(Color.WHITE);
        g.setFont(LABEL_FONT);
        g.drawString(label, x, y - 10);

        // Track
        g.setColor(Color.GRAY);
        g.fillRect(x, y, width, height);

        // Filled portion
        int fillWidth = (int) (width * (value / 100.0));
        g.setColor(Color.WHITE);
        g.fillRect(x, y, fillWidth, height);

        // Handle
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x + fillWidth - 5, y - 5, 10, height + 10);

        // Percentage label
        g.setColor(Color.WHITE);
        g.setFont(VALUE_FONT);
        g.drawString(value + "%", x + width + 20, y + 15);
    }

    /** Returns true if the mouse is within the draggable area of this slider. */
    public boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width
            && mouseY >= y - 10 && mouseY <= y + height + 10;
    }

    /** Update the slider value based on mouse X position (called while dragging). */
    public void updateValue(int mouseX) {
        int newValue = (int) (((mouseX - x) / (float) width) * 100);
        value = Math.max(0, Math.min(100, newValue));
    }
}
