import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

// This is my interface implementation
public class Ring extends GameObject implements Movable {
    private boolean wrapped;
    private final Color color;
    private static final Color[] COLORS = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE};

    public int getX() {
        return x;
    }

    public int getWidth() {
        return width;
    }

    //overloaded constructor
    public Ring(int x, int y) {
        super(x, y, 30, 30);
        this.wrapped = false;
        this.color = COLORS[new Random().nextInt(COLORS.length)];
    }
    
    public Ring(int x, int y, Color color) {
        super(x, y, 30, 30);
        this.wrapped = false;
        this.color = color; // Allow custom colors
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x, y, width, height);
        g.setColor(Color.WHITE);  // Set white color for outline
        g.drawOval(x, y, width, height);
    }

    @Override
    public void move(int dx, int dy) {
        if (!wrapped) {
            x += dx;
            y += dy;
        }
    }

    public void floatRandomly() {
        if (!wrapped) {
            Random random = new Random();
            int dx = (random.nextInt(5) - 2) * 2; // Faster horizontal movement
            int dy = random.nextInt(6) + 2;       // Faster downward movement
            move(dx, dy);
        }
    }

    public boolean isBelowScreen(int screenHeight) {
        return y > screenHeight;
    }

    public boolean isWrapped() {
        return wrapped;
    }

    public void setWrapped(boolean wrapped) {
        this.wrapped = wrapped;
    }
}
