package flappybird.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import flappybird.audio.SoundManager;
import flappybird.core.Bird;
import flappybird.core.GameState;
import flappybird.core.PipeManager;

/**
 * The main game panel. Handles all rendering and the game loop.
 * Delegates logic to Bird, PipeManager, and SoundManager.
 */
public class GamePanel extends JPanel {

    // ── Screen ──────────────────────────────────────────────────────────────
    private final int screenWidth;
    private final int screenHeight;
    private final int groundHeight;

    // ── Game objects ────────────────────────────────────────────────────────
    private Bird bird;
    private PipeManager pipeManager;

    // ── Audio ────────────────────────────────────────────────────────────────
    private final SoundManager soundManager;

    // ── State ────────────────────────────────────────────────────────────────
    private GameState currentState = GameState.MENU;
    private int score = 0;

    // ── UI Components ─────────────────────────────────────────────────────────
    private Button playButton;
    private Button settingsButton;
    private Button exitButton;
    private Button backButton;

    private Slider jumpVolumeSlider;
    private Slider scoreVolumeSlider;
    private Slider gameOverVolumeSlider;

    // ── Assets ───────────────────────────────────────────────────────────────
    private BufferedImage birdImage;
    private final String assetsPath;

    // ── Colors ───────────────────────────────────────────────────────────────
    private static final Color SKY_COLOR = new Color(135, 206, 235);
    private static final Color GROUND_COLOR = new Color(83, 53, 10);
    private static final Color GRASS_COLOR = new Color(100, 200, 50);

    // ── Constructor ──────────────────────────────────────────────────────────
    public GamePanel(String assetsPath, SoundManager soundManager) {
        this.assetsPath = assetsPath;
        this.soundManager = soundManager;

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = screen.width;
        screenHeight = screen.height;
        groundHeight = screenHeight - 100;

        loadAssets();
        initGameObjects();
        initUI();
        initInputHandlers();
        startGameLoop();
    }

    // ── Initialisation ───────────────────────────────────────────────────────

    private void loadAssets() {
        // 1st try: classpath
        try (InputStream is = getClass().getResourceAsStream("/flappybird/assets/bird.png")) {
            if (is != null) {
                birdImage = removeBackground(ImageIO.read(is));
                return;
            }
        } catch (Exception ignored) {
        }

        // 2nd try: filesystem
        try {
            File f = new File(assetsPath + File.separator + "bird.png");
            if (f.exists()) {
                birdImage = removeBackground(ImageIO.read(f));
                return;
            }
        } catch (Exception ignored) {
        }

        System.err.println("[GamePanel] bird.png not found — using yellow oval fallback.");
        birdImage = null;
    }

    /**
     * Convierte los píxeles negros (o casi negros) en transparentes.
     * Umbral: si R+G+B < 60 se considera fondo negro.
     */
    private BufferedImage removeBackground(BufferedImage src) {
        BufferedImage result = new BufferedImage(
                src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int pixel = src.getRGB(x, y);
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;
                // Si el píxel es negro o casi negro → transparente
                if (r + g + b < 40) {
                    result.setRGB(x, y, 0x00000000); // transparente
                } else {
                    result.setRGB(x, y, pixel);
                }
            }
        }
        return result;
    }

    private void initGameObjects() {
        bird = new Bird(screenWidth / 8, screenHeight / 2, 45, birdImage);
        pipeManager = new PipeManager(screenWidth, groundHeight);
        int gapY = screenHeight / 2 - 100;
        pipeManager.spawnInitial(gapY, 200);
    }

    private void initUI() {
        setBackground(SKY_COLOR);

        int btnW = 300;
        int btnH = 80;
        int btnX = screenWidth / 2 - btnW / 2;

        playButton = new Button(btnX, screenHeight / 2 - 130, btnW, btnH, "Play", Color.GREEN);
        settingsButton = new Button(btnX, screenHeight / 2 - 30, btnW, btnH, "Settings", Color.BLUE);
        exitButton = new Button(btnX, screenHeight / 2 + 70, btnW, btnH, "Exit", Color.RED);
        backButton = new Button(btnX, screenHeight / 2 + 200, btnW, 60, "Back", Color.ORANGE);

        int sliderW = 400;
        int sliderX = screenWidth / 2 - sliderW / 2;
        jumpVolumeSlider = new Slider(sliderX, screenHeight / 2 - 100, sliderW, "Jump Volume", 50);
        scoreVolumeSlider = new Slider(sliderX, screenHeight / 2, sliderW, "Score Volume", 50);
        gameOverVolumeSlider = new Slider(sliderX, screenHeight / 2 + 100, sliderW, "Game Over Volume", 50);
    }

    private void startGameLoop() {
        Timer timer = new Timer(20, e -> {
            if (currentState == GameState.PLAYING) {
                tick();
            }
            repaint();
        });
        timer.start();
    }

    // ── Game logic ────────────────────────────────────────────────────────────

    /** One game tick: physics + pipes + collision + scoring. */
    private void tick() {
        bird.applyGravity();
        pipeManager.update();

        if (pipeManager.checkScore(bird.x)) {
            score++;
            // (optional) soundManager.playSfx(SoundManager.SFX_SCORE);
        }

        boolean hitPipe = pipeManager.checkCollision(bird.x, bird.y, bird.size);
        boolean hitGround = bird.y > groundHeight;
        boolean hitCeil = bird.y < 0;

        if (hitPipe || hitGround || hitCeil) {
            currentState = GameState.GAME_OVER;
            soundManager.play(SoundManager.BGM_GAME_OVER, false);
        }
    }

    /** Reset everything and return to PLAYING state. */
    private void resetGame() {
        soundManager.stopCurrent();
        score = 0;
        bird.reset(screenHeight / 2);
        int gapY = screenHeight / 2 - 100;
        pipeManager.spawnInitial(gapY, 200);
        currentState = GameState.PLAYING;
    }

    // ── Input ─────────────────────────────────────────────────────────────────

    private void initInputHandlers() {
        setFocusable(true);

        // ── Keyboard ─────────────────────────────────────────────────────────
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                if (key == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }

                if (key == KeyEvent.VK_ENTER && currentState == GameState.MENU) {
                    resetGame();
                }
                if (key == KeyEvent.VK_SPACE && currentState == GameState.PLAYING) {
                    bird.jump();
                    soundManager.playSfx(SoundManager.SFX_JUMP);
                }
                if (key == KeyEvent.VK_R && currentState == GameState.GAME_OVER) {
                    resetGame();
                }
            }
        });

        // ── Mouse clicks ─────────────────────────────────────────────────────
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mx = e.getX(), my = e.getY();

                if (currentState == GameState.MENU) {
                    if (playButton.isClicked(mx, my)) {
                        resetGame();
                    }
                    if (exitButton.isClicked(mx, my)) {
                        System.exit(0);
                    }
                    if (settingsButton.isClicked(mx, my)) {
                        currentState = GameState.SETTINGS;
                    }
                }
                if (currentState == GameState.SETTINGS) {
                    if (backButton.isClicked(mx, my)) {
                        currentState = GameState.MENU;
                    }
                }
                if (currentState == GameState.PLAYING) {
                    bird.jump();
                    soundManager.playSfx(SoundManager.SFX_JUMP);
                }
                if (currentState == GameState.GAME_OVER) {
                    if (backButton.isClicked(mx, my)) {
                        currentState = GameState.MENU;
                    }
                }
            }
        });

        // ── Slider dragging ──────────────────────────────────────────────────
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentState != GameState.SETTINGS)
                    return;
                int mx = e.getX(), my = e.getY();
                if (jumpVolumeSlider.isHovering(mx, my)) {
                    jumpVolumeSlider.updateValue(mx);
                }
                if (scoreVolumeSlider.isHovering(mx, my)) {
                    scoreVolumeSlider.updateValue(mx);
                }
                if (gameOverVolumeSlider.isHovering(mx, my)) {
                    gameOverVolumeSlider.updateValue(mx);
                }
                repaint();
            }
        });
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        switch (currentState) {
            case MENU -> drawMenu(g);
            case PLAYING -> drawGame(g);
            case GAME_OVER -> drawGameOver(g);
            case SETTINGS -> drawSettings(g);
        }
    }

    private void drawMenu(Graphics g) {
        // Background
        g.setColor(SKY_COLOR);
        g.fillRect(0, 0, screenWidth, screenHeight);
        drawGround(g);

        // Title
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 100));
        FontMetrics fm = g.getFontMetrics();
        String title = "FLAPPY BIRD";
        g.drawString(title, (screenWidth - fm.stringWidth(title)) / 2, screenHeight / 3);

        // Subtitle

        playButton.draw(g);
        settingsButton.draw(g);
        exitButton.draw(g);
    }

    private void drawGame(Graphics g) {
        // Sky
        g.setColor(SKY_COLOR);
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Pipes
        pipeManager.draw(g);

        // Ground
        drawGround(g);

        // Bird
        bird.draw(g);

        // Score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String scoreStr = String.valueOf(score);
        g.drawString(scoreStr, (screenWidth - fm.stringWidth(scoreStr)) / 2, 80);

        // ESC hint
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(new Color(255, 255, 255, 180));
        g.drawString("ESC to exit", screenWidth - 140, 30);
    }

    private void drawGameOver(Graphics g) {
        drawGame(g); // Show the game frozen behind the overlay

        // Dark overlay
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Game Over title
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 90));
        FontMetrics fm = g.getFontMetrics();
        String go = "GAME OVER";
        g.drawString(go, (screenWidth - fm.stringWidth(go)) / 2, screenHeight / 2 - 120);

        // Score
        g.setFont(new Font("Arial", Font.BOLD, 50));
        fm = g.getFontMetrics();
        String sc = "Score: " + score;
        g.drawString(sc, (screenWidth - fm.stringWidth(sc)) / 2, screenHeight / 2 - 30);

        // Instructions
        g.setFont(new Font("Arial", Font.PLAIN, 35));
        fm = g.getFontMetrics();
        String r = "Press R to Restart";
        String esc = "Press ESC to Exit";
        g.drawString(r, (screenWidth - fm.stringWidth(r)) / 2, screenHeight / 2 + 60);
        g.drawString(esc, (screenWidth - fm.stringWidth(esc)) / 2, screenHeight / 2 + 110);
    }

    private void drawSettings(Graphics g) {
        g.setColor(SKY_COLOR);
        g.fillRect(0, 0, screenWidth, screenHeight);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 80));
        FontMetrics fm = g.getFontMetrics();
        String title = "Settings";
        g.drawString(title, (screenWidth - fm.stringWidth(title)) / 2, screenHeight / 3);

        jumpVolumeSlider.draw(g);
        scoreVolumeSlider.draw(g);
        gameOverVolumeSlider.draw(g);

        backButton.draw(g);
    }

    private void drawGround(Graphics g) {
        // Dirt layer
        g.setColor(GROUND_COLOR);
        g.fillRect(0, groundHeight + 20, screenWidth, 80);
        // Grass strip
        g.setColor(GRASS_COLOR);
        g.fillRect(0, groundHeight, screenWidth, 22);
    }
}
