package org.example.ui;

import org.example.logic.Game;
import org.example.model.Asteroid;
import org.example.model.Enemy;
import org.example.model.Nave;
import org.example.model.Projectile;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GamePanel extends JPanel {
    private final Game game;
    private final BufferedImage bgImage1, bgImage2, bgImage3, bgImage4;
    private final BufferedImage projectileImage, enemyProjectileImage;
    private final BufferedImage[] shipImages = new BufferedImage[4];

    // Renderers
    private final NaveRenderer naveRenderer = new NaveRenderer();
    private final AsteroidRenderer asteroidRenderer;
    private final EnemyRenderer enemyRenderer;

    public GamePanel(Game game) {
        this.game = game;
        setDoubleBuffered(true);

        // Load backgrounds
        bgImage1 = BufferedImageUtil.readImage("SpaceBG.png", GamePanel.class);
        bgImage2 = BufferedImageUtil.readImage("SpaceBG1.png", GamePanel.class);
        bgImage3 = BufferedImageUtil.readImage("SpaceBG2.png", GamePanel.class);
        bgImage4 = BufferedImageUtil.readImage("SpaceBG3.png", GamePanel.class);

        // Load project/laser images
        projectileImage = BufferedImageUtil.readImage("ProjectilePea.png", GamePanel.class);
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
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Nave nave = game.getNave();
        int mapWidth = game.getMapWidth();
        int mapHeight = game.getMapHeight();

        // Camera calculations
        int screenWidth = getWidth();
        int screenHeight = getHeight();
        int cameraX = Math.max(0, Math.min(nave.getX() - screenWidth / 2 + nave.getWidth() / 2, mapWidth - screenWidth));
        int cameraY = Math.max(0, Math.min(nave.getY() - screenHeight / 2 + nave.getHeight() / 2, mapHeight - screenHeight));

        g2d.translate(-cameraX, -cameraY);

        // Draw Background
        drawBackground(g2d, mapWidth, mapHeight);

        // 1. Draw Asteroids via Renderer
        for (Asteroid ast : game.getAsteroids()) {
            asteroidRenderer.render(g2d, ast, null);
        }

        // 2. Draw Player Ship via Renderer
        int levelIndex = Math.max(0, Math.min(nave.getLevel() - 1, 3));
        naveRenderer.render(g2d, nave, shipImages[levelIndex]);

        // 3. Draw Projectiles
        for (Projectile p : game.getProjectiles()) {
            int drawX = (int) p.getX();
            int drawY = (int) p.getY();
            BufferedImage pImg = p.isEnemyProjectile() ? enemyProjectileImage : projectileImage;
            Color fallbackColor = p.isEnemyProjectile() ? Color.orange : Color.green;

            if (pImg != null) {
                g2d.drawImage(pImg, drawX, drawY, p.getWidth(), p.getHeight(), this);
            } else {
                g2d.setColor(fallbackColor);
                g2d.fillOval(drawX, drawY, p.getWidth(), p.getHeight());
            }
        }

        // 4. Draw Enemies via Renderer
        for (Enemy enemy : game.getEnemies()) {
            enemyRenderer.render(g2d, enemy, null);
        }

        g2d.translate(cameraX, cameraY);
        drawHUD(g2d, nave);
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
}