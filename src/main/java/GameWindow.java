import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameWindow extends JFrame {
    private JLayeredPane layeredPane;
    private JLabel backgroundLabel;
    private JLabel titleLabel;
    private JTextField playerNameField;
    private JButton startButton;
    private String playerName;
    private ImageIcon backgroundImage; // Add backgroundImage field

    public GameWindow() {
        this("FishSticks Game");
    }

    public GameWindow(String title) {
        setTitle(title);
        setSize(810, 635);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        layeredPane = new JLayeredPane();
        setContentPane(layeredPane);

        initializeComponents();
        setVisible(true);
    }

    private void initializeComponents() {
        // Set up background
        backgroundLabel = new JLabel();
        setGameBackground("underwaterintro.gif"); // Corrected image name
        backgroundLabel.setBounds(0, 0, 800, 600);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        // Play intro sound
        SoundManager.loadSound("intro", "sound/water-bubbles.wav");
        SoundManager.playSound("intro");

        // Set up title
        titleLabel = new JLabel("FishSticks", SwingConstants.CENTER);
        titleLabel.setForeground(Color.ORANGE);
        titleLabel.setFont(new Font("Bauhaus 93", Font.BOLD, 64));
        titleLabel.setBounds(0, 170, getWidth(), 80);
        titleLabel.setOpaque(false);
        layeredPane.add(titleLabel, JLayeredPane.PALETTE_LAYER);

        // Set up player name input field
        playerNameField = new JTextField("Please enter your name");
        playerNameField.setFont(new Font("Arial", Font.BOLD, 18));
        playerNameField.setBounds((getWidth() - 300) / 2, 250, 300, 30);
        playerNameField.setOpaque(true);
        playerNameField.setBackground(new Color(255, 255, 255, 200));
        playerNameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (playerNameField.getText().equals("Please enter your name")) {
                    playerNameField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (playerNameField.getText().isEmpty()) {
                    playerNameField.setText("Please enter your name");
                }
            }
        });
        layeredPane.add(playerNameField, JLayeredPane.PALETTE_LAYER);

        // Set up start button
        startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setBounds((getWidth() - 150) / 2, 360, 150, 40);
        startButton.setOpaque(true);
        startButton.setBackground(new Color(0, 255, 0, 200));
        startButton.addActionListener(e -> startGame());
        layeredPane.add(startButton, JLayeredPane.PALETTE_LAYER);
    }

    private void setGameBackground(String imagePath) {
        ImageIcon bgIcon = new ImageIcon(getClass().getClassLoader().getResource("images/" + imagePath));
        if (bgIcon != null) {
            Image image = bgIcon.getImage().getScaledInstance(800, 600, Image.SCALE_DEFAULT);
            backgroundImage = new ImageIcon(image); // Store scaled image
            backgroundLabel.setIcon(new ImageIcon(image));
        } else {
            System.err.println("Could not load image: " + imagePath);
        }
    }


    // Method to start the game
    private void startGame() {
        playerName = playerNameField.getText().trim();
        if (playerName.isEmpty() || playerName.equals("Please enter your name")) {
            JOptionPane.showMessageDialog(this, "Please enter your name to start the game.");
            return;
        }

        SoundManager.stopAllSounds();
        remove(titleLabel);
        remove(playerNameField);
        remove(startButton);

        setGameBackground("underwaterbackground.png");

        GameLogic gamePanel = new GameLogic(this, playerName);
        add(gamePanel);
        gamePanel.requestFocusInWindow();

        revalidate();
        repaint();
    }

    // Getter for player name
    public String getPlayerName() {
        return playerName;
    }

    // Main method to start the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SoundManager.initialize();
            new GameWindow();
        });
    }
}
