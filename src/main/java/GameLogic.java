import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class GameLogic extends JPanel implements KeyListener {
    @SuppressWarnings("unused")
    private final GameWindow gameWindow;
    private final String playerName;
    // This is my array of subclass objects
    private final ArrayList<Ring> rings;
    private final ArrayList<Stick> sticks;
    private int score;
    private JLabel scoreLabel;
    private JLabel playerNameLabel;
    private JLabel highScoreLabel;
    private ImageIcon backgroundImage;
    private boolean gameOver = false;

    public GameLogic(GameWindow gameWindow, String playerName) {
        this.gameWindow = gameWindow;
        this.playerName = playerName;
        this.rings = new ArrayList<>();
        this.sticks = new ArrayList<>();
        this.score = 0;

        setLayout(null);
        setBounds(0, 0, 800, 600);
        setFocusable(true);

        initializeComponents();
        initializeGameObjects();
        startFloatingRings();
        loadBackgroundImage();
        loadHighScore(); // Load and display high score

        SoundManager.setVolume("game-music", 0.3f);
        SoundManager.playBackgroundMusic("game-music");
    }

    private void initializeComponents() {
        addKeyListener(this);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreLabel.setForeground(Color.BLUE);
        scoreLabel.setBounds(10, 38, 200, 16);
        add(scoreLabel);

        playerNameLabel = new JLabel("Player: " + playerName);
        playerNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        playerNameLabel.setForeground(Color.BLUE);
        playerNameLabel.setBounds(10, 5, 200, 16);
        add(playerNameLabel);
    }

    private void initializeGameObjects() {
        Random random = new Random();
        int gameWidth = getWidth();
        int stickSpacing = gameWidth / 8;

        for (int i = 0; i < 7; i++) {
            int stickHeight = random.nextInt(200) + 100;
            int stickY = getHeight() - stickHeight - random.nextInt(100);
            int stickX = (i + 1) * stickSpacing - 5;
            sticks.add(new Stick(stickX, stickY, 10, stickHeight));
        }

        for (int i = 0; i < 25; i++) {
            int ringX = random.nextInt(gameWidth - 30);
            int ringY = random.nextInt(200);
            rings.add(new Ring(ringX, ringY));
        }
    }

    private void startFloatingRings() {
        Timer timer = new Timer(50, e -> {
            if (!gameOver) {
                for (Ring ring : rings) {
                    ring.floatRandomly();
                }
                checkLostRings();
                repaint();
            }
        });
        timer.start();
    }

    private void checkLostRings() {
        Iterator<Ring> iterator = rings.iterator();
        boolean allRingsLost = true;
        while (iterator.hasNext()) {
            Ring ring = iterator.next();
            if (ring.isBelowScreen(getHeight())) {
                iterator.remove();
            } else if (!ring.isWrapped()) {
                allRingsLost = false;
            }
        }
        if (allRingsLost) {
            gameOver(false);
        }
    }

    private void loadBackgroundImage() {
        backgroundImage = new ImageIcon(getClass().getClassLoader().getResource("images/underwaterbackground.png"));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        }

        for (Stick stick : sticks) {
            stick.draw(g);
        }
        for (Ring ring : rings) {
            ring.draw(g);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT) {
            int gustForce = keyCode == KeyEvent.VK_LEFT ? -10 : 10;
            moveRings(gustForce);
            SoundManager.playSound("water-drop");
            checkCollisions();
            repaint();
        }
    }

    private void moveRings(int dx) {
        for (Ring ring : rings) {
            int newX = ring.getX() + dx;
            if (newX >= 0 && newX <= getWidth() - ring.getWidth()) {
                ring.move(dx, 0);
            }
        }
    }

    private void checkCollisions() {
        for (Ring ring : rings) {
            for (Stick stick : sticks) {
                if (ring.intersects(stick) && !ring.isWrapped()) {
                    ring.setWrapped(true);
                    score += 1000;
                    scoreLabel.setText("Score: " + score);
                    SoundManager.playSound("winner-bell");
                    checkWinCondition();
                    break;
                }
            }
        }
    }

    private void checkWinCondition() {
        if (score == 25000) {
            gameOver(true);
        }
    }

    private void gameOver(boolean isWin) {
        this.gameOver = true;
        SoundManager.stopBackgroundMusic();

        if (isWin) {
            SoundManager.playSound("winner-ceremony");
            setGameBackground("fish-sticks.jpg");
            showWinBanner("Congratulations, you won a stack of FishSticks!");
        } else {
            SoundManager.playSound("game-over-horn");
            setGameBackground("dead-fish.jpg");
            showGameOverBanner("Sorry Matey....you didn't catch all the rings", Color.YELLOW);
        }

        saveHighScore();
        addRestartButton();
    }

    private void addRestartButton() {
        JButton restartButton = new JButton("Restart Game");
        restartButton.setFont(new Font("Arial", Font.BOLD, 16));
        restartButton.setBounds((getWidth() - 150) / 2, 550, 150, 40);
        restartButton.setOpaque(true);
        restartButton.setBackground(new Color(0, 255, 0, 200));
        restartButton.addActionListener(e -> restartGame());
        add(restartButton);
        revalidate();
        repaint();
    }

    private void restartGame() {
        SoundManager.stopAllSounds(); // Stop all sounds, including winner-ceremony.wav

        removeAll();
        rings.clear();
        sticks.clear();
        score = 0;
        gameOver = false;

        initializeComponents();
        initializeGameObjects();
        startFloatingRings();
        loadBackgroundImage();
        loadHighScore();

        SoundManager.playBackgroundMusic("game-music");

        requestFocusInWindow();
        revalidate();
        repaint();
    }

    private void showWinBanner(String message) {
        JLabel bannerLabel = new JLabel(message, SwingConstants.CENTER);
        bannerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        bannerLabel.setForeground(Color.BLACK);
        bannerLabel.setBounds(0, 250, getWidth(), 50);
        bannerLabel.setOpaque(true);
        bannerLabel.setBackground(new Color(255, 255, 255, 200));
        add(bannerLabel);
        revalidate();
        repaint();
    }

    private void showGameOverBanner(String message, Color color) {
        JLabel bannerLabel = new JLabel(message, SwingConstants.CENTER);
        bannerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        bannerLabel.setForeground(color);
        bannerLabel.setBounds(0, 250, getWidth(), 50);
        bannerLabel.setOpaque(true);
        bannerLabel.setBackground(new Color(0, 0, 0, 200));
        add(bannerLabel);
        revalidate();
        repaint();
    }

    private void setGameBackground(String imagePath) {
        backgroundImage = new ImageIcon(getClass().getClassLoader().getResource("images/" + imagePath));
        repaint();
    }

    private void saveHighScore() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("highscore.txt"))) {
            writer.println(playerName + "," + score);
        } catch (IOException e) {
            System.err.println("Error saving high score: " + e.getMessage());
        }
    }

    private void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader("highscore.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                String[] parts = line.split(",");
                String highScorePlayer = parts[0];
                int highScore = Integer.parseInt(parts[1]);
                highScoreLabel = new JLabel("High Score: " + highScorePlayer + " - " + highScore);
                highScoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
                highScoreLabel.setForeground(Color.YELLOW);
                highScoreLabel.setBounds(10, 60, 200, 16);
                add(highScoreLabel);
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading high score: " + e.getMessage());
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
