package org.example.ui;

import org.example.logic.Game;
import org.example.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GamePanel extends JPanel {
    private final Game game;
    private final BufferedImage bgImage1, bgImage2, bgImage3, bgImage4;
    private final BufferedImage projectileImage, enemyProjectileImage;
    private final BufferedImage[] shipImages = new BufferedImage[4];
    private final BufferedImage level4LaserImage;

    // Renderers
    private final NaveRenderer naveRenderer = new NaveRenderer();
    private final AsteroidRenderer asteroidRenderer;
    private final EnemyRenderer enemyRenderer;
    private final BufferedImage gemImage;

    public GamePanel(Game game) {
        this.game = game;
        setDoubleBuffered(true);
        setFocusable(true);

        // Listen for any key press to start the game
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (!game.isGameStarted()) {
                    game.startGame();
                    repaint();
                }
            }
        });

// Update Mouse Listener to also start the game on click
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (!game.isGameStarted()) {
                    game.startGame();
                    repaint();
                    return;
                }

                if (game.isGameWon() || game.isGameOver()) {
                    if (getRestartButtonBounds().contains(e.getPoint())) {
                        game.resetGame();
                        repaint();
                    }
                    return;
                }

                if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                    game.setShooting(true);
                    game.tryShoot();
                } else if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                    game.setRightMouseDown(true);
                    game.tryShoot();
                }
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                    game.setShooting(false);
                } else if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                    game.setRightMouseDown(false);
                }
            }
        });

        // --- MOUSE CLICK SHOOTING LISTENER ---
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (game.isGameWon() || game.isGameOver()) {
                    if (getRestartButtonBounds().contains(e.getPoint())) {
                        game.resetGame();
                        repaint();
                    }
                    return;
                }

                if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                    game.setShooting(true);
                    game.tryShoot();
                } else if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) { // Right click
                    game.setRightMouseDown(true);
                    game.tryShoot();
                }
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                    game.setShooting(false);
                } else if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                    game.setRightMouseDown(false);
                }
            }
        });

        // Key Listener for 'E' key
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("E"), "pressE");
        getActionMap().put("pressE", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                game.setEKeyPressed(true);
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released E"), "releaseE");
        getActionMap().put("releaseE", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                game.setEKeyPressed(false);
            }
        });

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                Nave nave = game.getNave();
                int screenWidth = getWidth();
                int screenHeight = getHeight();
                int mapWidth = game.getMapWidth();
                int mapHeight = game.getMapHeight();

                // Account for camera translation matching paintComponent
                int cameraX = Math.max(0, Math.min(nave.getX() - screenWidth / 2 + nave.getWidth() / 2, mapWidth - screenWidth));
                int cameraY = Math.max(0, Math.min(nave.getY() - screenHeight / 2 + nave.getHeight() / 2, mapHeight - screenHeight));

                // World coordinates of the mouse cursor
                double mouseWorldX = e.getX() + cameraX;
                double mouseWorldY = e.getY() + cameraY;

                // Ship center in world coordinates
                double shipCenterX = nave.getX() + nave.getWidth() / 2.0;
                double shipCenterY = nave.getY() + nave.getHeight() / 2.0;

                // Calculate angle towards mouse using Math.atan2
                double dx = mouseWorldX - shipCenterX;
                double dy = mouseWorldY - shipCenterY;
                double angleDegree = Math.toDegrees(Math.atan2(dy, dx)) + 90; // +90 adjustment depending on sprite asset orientation

                nave.setAngle(angleDegree);
            }
        });

        // Load backgrounds
        bgImage1 = BufferedImageUtil.readImage("SpaceBG.png", GamePanel.class);
        bgImage2 = BufferedImageUtil.readImage("SpaceBG1.png", GamePanel.class);
        bgImage3 = BufferedImageUtil.readImage("SpaceBG2.png", GamePanel.class);
        bgImage4 = BufferedImageUtil.readImage("SpaceBG3.png", GamePanel.class);

        // Load project/laser images
        projectileImage = BufferedImageUtil.readImage("ProjectilePea.png", GamePanel.class);
        level4LaserImage = BufferedImageUtil.readImage("laser.png", GamePanel.class);
        enemyProjectileImage = BufferedImageUtil.readImage("enemy_bullets.png", GamePanel.class);

        // Load ship assets
        shipImages[0] = BufferedImageUtil.readImage("ship_lvl1.png", GamePanel.class);
        shipImages[1] = BufferedImageUtil.readImage("ship_lvl2.png", GamePanel.class);
        shipImages[2] = BufferedImageUtil.readImage("ship_lvl3.png", GamePanel.class);
        shipImages[3] = BufferedImageUtil.readImage("ship_lvl4.png", GamePanel.class);

        // Initialize Renderers with assets
        asteroidRenderer = new AsteroidRenderer(
                BufferedImageUtil.readImage("asteroid_big.png", GamePanel.class),
                BufferedImageUtil.readImage("asteroid_mid.png", GamePanel.class),
                BufferedImageUtil.readImage("asteroid_small.png", GamePanel.class)
        );

        enemyRenderer = new EnemyRenderer(
                BufferedImageUtil.readImage("enemy_soldier.png", GamePanel.class),
                BufferedImageUtil.readImage("enemy_officer.png", GamePanel.class),
                BufferedImageUtil.readImage("enemy_boss.png", GamePanel.class)
        );

        gemImage = BufferedImageUtil.readImage("level_up.png", GamePanel.class);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Nave nave = game.getNave();
        int mapWidth = game.getMapWidth();
        int mapHeight = game.getMapHeight();

        // --- DRAW START SCREEN IF GAME HAS NOT STARTED ---
        if (!game.isGameStarted()) {
            // Render background grid across the panel view
            drawBackground(g2d, getWidth(), getHeight());

            // Dark overlay for contrast
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            // Title: andrew's
            g2d.setFont(new Font("Arial", Font.BOLD, 36));
            String line1 = "andrew's";
            FontMetrics fm1 = g2d.getFontMetrics();
            g2d.setColor(Color.CYAN);
            g2d.drawString(line1, centerX - fm1.stringWidth(line1) / 2, centerY - 60);

            // Title: STAR BLASTER
            g2d.setFont(new Font("Arial", Font.BOLD, 52));
            String line2 = "STAR BLASTER";
            FontMetrics fm2 = g2d.getFontMetrics();
            g2d.setColor(Color.YELLOW);
            g2d.drawString(line2, centerX - fm2.stringWidth(line2) / 2, centerY + 10);

            // Subtitle: press any key to begin
            g2d.setFont(new Font("Arial", Font.PLAIN, 20));
            String line3 = "press any key to begin";
            FontMetrics fm3 = g2d.getFontMetrics();
            g2d.setColor(Color.WHITE);
            g2d.drawString(line3, centerX - fm3.stringWidth(line3) / 2, centerY + 70);

            return; // Skip normal gameplay rendering until started
        }

        // Camera calculations
        int screenWidth = getWidth();
        int screenHeight = getHeight();
        int cameraX = Math.max(0, Math.min(nave.getX() - screenWidth / 2 + nave.getWidth() / 2, mapWidth - screenWidth));
        int cameraY = Math.max(0, Math.min(nave.getY() - screenHeight / 2 + nave.getHeight() / 2, mapHeight - screenHeight));

        g2d.translate(-cameraX, -cameraY);

        // Draw Background
        drawBackground(g2d, mapWidth, mapHeight);

        // 1. Draw Gems (World Coordinates)
        for (Gem gem : game.getGems()) {
            int drawX = (int) gem.getX();
            int drawY = (int) gem.getY();
            if (gemImage != null) {
                g2d.drawImage(gemImage, drawX, drawY, gem.getWidth(), gem.getHeight(), this);
            } else {
                g2d.setColor(Color.yellow);
                g2d.fillOval(drawX, drawY, gem.getWidth(), gem.getHeight());
            }
        }

        // 2. Draw Asteroids via Renderer
        for (Asteroid ast : game.getAsteroids()) {
            asteroidRenderer.render(g2d, ast, null);
        }

        // 3. Draw Player Ship via Renderer
        int levelIndex = Math.max(0, Math.min(nave.getLevel() - 1, 3));
        naveRenderer.render(g2d, nave, shipImages[levelIndex]);

        // 4. Draw Projectiles with Rotation
        for (Projectile p : game.getProjectiles()) {
            int drawX = (int) p.getX();
            int drawY = (int) p.getY();

            // Select image based on whether it's a piercing Level 4 beam or standard projectile
            BufferedImage pImg;
            if (p.isPiercing()) {
                pImg = (level4LaserImage != null) ? level4LaserImage : projectileImage;
            } else {
                pImg = p.isEnemyProjectile() ? enemyProjectileImage : projectileImage;
            }

            Color fallbackColor = p.isEnemyProjectile() ? Color.orange : (p.isPiercing() ? Color.cyan : Color.green);

            Graphics2D g2dCopy = (Graphics2D) g2d.create();
            double centerX = drawX + p.getWidth() / 2.0;
            double centerY = drawY + p.getHeight() / 2.0;

            g2dCopy.translate(centerX, centerY);
            g2dCopy.rotate(Math.toRadians(p.getAngle()));

            if (pImg != null) {
                g2dCopy.drawImage(pImg, -p.getWidth() / 2, -p.getHeight() / 2, p.getWidth(), p.getHeight(), this);
            } else {
                g2dCopy.setColor(fallbackColor);
                g2dCopy.fillOval(-p.getWidth() / 2, -p.getHeight() / 2, p.getWidth(), p.getHeight());
            }
            g2dCopy.dispose();
        }

        // 5. Draw Enemies via Renderer
        for (Enemy enemy : game.getEnemies()) {
            enemyRenderer.render(g2d, enemy, null);
        }

        g2d.translate(cameraX, cameraY);
        drawHUD(g2d, nave);

        // --- 6. DRAW WIN/LOSS OVERLAYS ON TOP OF THE FROZEN FRAME ---
        if (game.isGameWon()) {
            g2d.setColor(new Color(0, 0, 0, 160));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setFont(new Font("Arial", Font.BOLD, 48));
            String winText = "VICTORY! BOSS DEFEATED";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(winText);

            g2d.setColor(Color.GREEN);
            g2d.drawString(winText, (getWidth() - textWidth) / 2, getHeight() / 2 - 20);

            g2d.setFont(new Font("Arial", Font.PLAIN, 20));
            String subText = "You saved the galaxy!";
            int subWidth = g2d.getFontMetrics().stringWidth(subText);
            g2d.setColor(Color.WHITE);
            g2d.drawString(subText, (getWidth() - subWidth) / 2, getHeight() / 2 + 20);

            // --- RESTART BUTTON ---
            drawRestartButton(g2d);

        } else if (game.isGameOver()) {
            g2d.setColor(new Color(0, 0, 0, 160));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setFont(new Font("Arial", Font.BOLD, 48));
            String loseText = "GAME OVER";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(loseText);

            g2d.setColor(Color.RED);
            g2d.drawString(loseText, (getWidth() - textWidth) / 2, getHeight() / 2 - 20);

            g2d.setFont(new Font("Arial", Font.PLAIN, 20));
            String subText = "Your ship was destroyed.";
            int subWidth = g2d.getFontMetrics().stringWidth(subText);
            g2d.setColor(Color.WHITE);
            g2d.drawString(subText, (getWidth() - subWidth) / 2, getHeight() / 2 + 20);

            // --- RESTART BUTTON ---
            drawRestartButton(g2d);
        }
    }

    private void drawRestartButton(Graphics2D g2d) {
        Rectangle btnBounds = getRestartButtonBounds();

        // Draw button background
        g2d.setColor(new Color(41, 128, 185)); // Sleek blue interactive theme
        g2d.fillRoundRect(btnBounds.x, btnBounds.y, btnBounds.width, btnBounds.height, 12, 12);
        g2d.setColor(Color.WHITE);
        g2d.drawRoundRect(btnBounds.x, btnBounds.y, btnBounds.width, btnBounds.height, 12, 12);

        // Draw button text centered
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        String btnText = "wanna go again?";
        FontMetrics fm = g2d.getFontMetrics();
        int textW = fm.stringWidth(btnText);
        int textH = fm.getAscent();
        g2d.drawString(btnText, btnBounds.x + (btnBounds.width - textW) / 2, btnBounds.y + (btnBounds.height + textH) / 2 - 4);
    }

    private void drawBackground(Graphics2D g2d, int mapWidth, int mapHeight) {
        if (bgImage1 != null && bgImage2 != null && bgImage3 != null && bgImage4 != null) {
            int imgWidth = bgImage1.getWidth();
            int imgHeight = bgImage1.getHeight();
            if (imgWidth > 0 && imgHeight > 0) {
                int col = 0;
                for (int x = 0; x < mapWidth; x += imgWidth, col++) {
                    int row = 0;
                    for (int y = 0; y < mapHeight; y += imgHeight, row++) {
                        int patternIndex = (row + col) % 3;
                        BufferedImage currentBg = switch (patternIndex) {
                            case 0 -> bgImage1;
                            case 1 -> bgImage3;
                            case 2 -> bgImage2;
                            default -> bgImage4;
                        };
                        g2d.drawImage(currentBg, x, y, this);
                    }
                }
            }
        } else {
            g2d.setColor(Color.black);
            g2d.fillRect(0, 0, mapWidth, mapHeight);
        }
    }

    private void drawHUD(Graphics2D g2d, Nave nave) {
        long elapsedSeconds = game.getElapsedSeconds();
        String timeText = String.format("Time: %02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60);

        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(timeText);
        int textX = (getWidth() - textWidth) / 2;
        int textY = 30;

        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(textX - 10, textY - 18, textWidth + 20, 26);
        g2d.setColor(Color.WHITE);
        g2d.drawString(timeText, textX, textY);

        int barX = 20, barY = 20, barWidth = 200, barHeight = 16, spacing = 22;

        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Level: " + nave.getLevel(), barX, barY - 5);

        // HP Bar
        g2d.setColor(new Color(50, 50, 50, 200));
        g2d.fillRect(barX, barY, barWidth, barHeight);
        g2d.setColor(new Color(46, 204, 113));
        g2d.fillRect(barX, barY, (int) (barWidth * Math.max(0, Math.min(1.0, nave.getHp() / nave.getMaxHp()))), barHeight);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(barX, barY, barWidth, barHeight);
        g2d.drawString(String.format("HP: %d/%d", (int)nave.getHp(), (int)nave.getMaxHp()), barX + 8, barY + 13);

        // Energy Bar
        int energyY = barY + spacing;
        g2d.setColor(new Color(50, 50, 50, 200));
        g2d.fillRect(barX, energyY, barWidth, barHeight);
        g2d.setColor(new Color(52, 152, 219));
        g2d.fillRect(barX, energyY, (int) (barWidth * Math.max(0, Math.min(1.0, nave.getEnergy() / nave.getMaxEnergy()))), barHeight);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(barX, energyY, barWidth, barHeight);
        g2d.drawString(String.format("Energy: %d/%d", (int)nave.getEnergy(), (int)nave.getMaxEnergy()), barX + 8, energyY + 13);
    }

    private Rectangle getRestartButtonBounds() {
        int btnWidth = 240;
        int btnHeight = 45;
        int btnX = (getWidth() - btnWidth) / 2;
        int btnY = getHeight() / 2 + 55; // Placed below the subtitle text
        return new Rectangle(btnX, btnY, btnWidth, btnHeight);
    }
}