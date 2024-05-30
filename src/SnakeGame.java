import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SnakeGame extends JFrame {

    private final int WIDTH = 600, HEIGHT = 450; // Size of the game window
    private final int DOT_SIZE = 10; // Size of the snake and food
    private int[] x = new int[350]; // x coordinates of the snake's joints
    private int[] y = new int[420]; // y coordinates of the snake's joints
    private int bodyParts = 3; // Initial size of the snake
    private int foodX; // X coordinate of food
    private int foodY; // Y coordinate of food
    private int blueFoodX; // X coordinate of blue food
    private int blueFoodY; // Y coordinate of blue food
    private int fruitsEaten = 0; // Counter for the number of fruits eaten
    private int highScore = 0; // High score
    private char direction = 'R'; // Initial direction of the snake
    private boolean running = false; // Game state
    private Timer timer; // Timer for game updates
    private JPanel gamePanel; // Panel to control the game drawing area
    private int speedUpTimer = 0; // Timer for speed up effect
    private int speed = 0;

    public SnakeGame() {
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                doDrawing(g);
                showScore(g);
            }
        };
        gamePanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();

        add(gamePanel);
        pack();
        setLocationRelativeTo(null);

        start();
        initKeyBindings();
    }

    private void start() {
        speed = (int)(Math.random()*50) + 30;
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 50 - i * 10;
            y[i] = 50;
        }
        placeFood();
        timer = new Timer(60, e -> gameUpdate());
        timer.start();
        running = true;
        fruitsEaten = 0; // Reset the fruits eaten counter
    }

    private void initKeyBindings() {
        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "moveLeft");
        gamePanel.getActionMap().put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (direction != 'R') direction = 'L';
            }
        });

        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "moveRight");
        gamePanel.getActionMap().put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (direction != 'L') direction = 'R';
            }
        });

        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "moveUp");
        gamePanel.getActionMap().put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (direction != 'D') direction = 'U';
            }
        });

        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "moveDown");
        gamePanel.getActionMap().put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (direction != 'U') direction = 'D';
            }
        });

        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "restartGame");
        gamePanel.getActionMap().put("restartGame", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!running) restartGame();
            }
        });
    }

    private void placeFood() {
        int r = (int) (Math.random() * ((WIDTH - DOT_SIZE) / DOT_SIZE));
        foodX = r * DOT_SIZE;
        int y = (int) (Math.random() * ((HEIGHT - DOT_SIZE) / DOT_SIZE));
        foodY = y * DOT_SIZE;
    }
    private void gameUpdate() {
        if (running) {
            move();
            checkFood();
            checkBlueFood();
            checkCollisions();
        }
        gamePanel.repaint();
    }

    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        int delay = speed; // Default delay
        if (speedUpTimer > 0) {
            delay = 30; // Faster delay during speed up
            speedUpTimer--;
        }

        switch (direction) {
            case 'U': y[0] -= DOT_SIZE; break;
            case 'D': y[0] += DOT_SIZE; break;
            case 'L': x[0] -= DOT_SIZE; break;
            case 'R': x[0] += DOT_SIZE; break;
        }

        timer.setDelay(delay);
    }

    private void checkFood() {
        if ((x[0] == foodX) && (y[0] == foodY)) {
            bodyParts++;
            fruitsEaten++;
            placeFood();
            updateHighScore();
        }
    }

    private void checkBlueFood() {
        if ((x[0] == blueFoodX) && (y[0] == blueFoodY)) {
            bodyParts += 3; // Increase body length by 3
            fruitsEaten += 3; // Increase score by 3
            updateHighScore();
            speedUpTimer = 180; // Speed up for 3 seconds (180 frames at 60 FPS)
        }
    }

    private void updateHighScore() {
        if (fruitsEaten > highScore) {
            highScore = fruitsEaten;
        }
    }

    private void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((i > 4) && (x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }

        if (y[0] < 0 || y[0] >= HEIGHT || x[0] < 0 || x[0] >= WIDTH) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    private void restartGame() {
        bodyParts = 3;
        fruitsEaten = 0;
        direction = 'R';
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 50 - i * 10;
            y[i] = 50;
        }
        placeFood();
        timer.start();
        running = true;
        speedUpTimer = 0; // Reset speed up timer
        speed = (int)(Math.random()*50) + 30;
    }

    private void doDrawing(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        if (running) {
            g.setColor(Color.red);
            g.fillRect(foodX, foodY, DOT_SIZE, DOT_SIZE);

            g.setColor(Color.blue);
            g.fillRect(blueFoodX, blueFoodY, DOT_SIZE, DOT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], DOT_SIZE, DOT_SIZE);
                } else {
                    g.setColor(new Color(0, 45, 180));
                    g.fillRect(x[i], y[i], DOT_SIZE, DOT_SIZE);
                }
            }
        } else {
            gameOver(g);
        }
    }

    private void showScore(Graphics g) {
        g.setColor(Color.black);
        g.drawString("Current Score: " + fruitsEaten, 13, 23);
        g.drawString("High Score: " + highScore, 13, 43);
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.black);
        g.drawString("Game Over", WIDTH / 2 - 50, HEIGHT / 2);
        g.drawString("Final Score: " + fruitsEaten, WIDTH / 2 - 50, HEIGHT / 2 + 20);
        g.drawString("High Score: " + highScore, WIDTH / 2 - 50, HEIGHT / 2 + 40);
        g.drawString("Press r to restart", WIDTH / 2 - 50, HEIGHT / 2 + 60);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new SnakeGame();
            frame.setVisible(true);
        });
    }
}
