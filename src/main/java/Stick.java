import java.awt.*;
import java.util.Random;

public class Stick extends GameObject {
    private final Color color;
    private static final Color[] COLORS = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE};

    public Stick(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.color = COLORS[new Random().nextInt(COLORS.length)];
    }
    // This is my overridden method
    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }
}
