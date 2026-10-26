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
    private final BufferedImage bgImage1;
    private final BufferedImage bgImage2;
    private final BufferedImage bgImage3;
    private final BufferedImage bgImage4;
    private final BufferedImage projectileImage;
    private final BufferedImage[] shipImages = new BufferedImage[4];

    private BufferedImage asteroidBigImage;
    private BufferedImage asteroidMidImage;
    private BufferedImage asteroidSmallImage;

    private BufferedImage enemySoldierImage;
    private BufferedImage enemyOfficerImage;
    private BufferedImage enemyBossImage;

//    private static final int MAP_WIDTH = 1000;
//    private static final int MAP_HEIGHT = 1000;

    public GamePanel(Game game) {
        this.game = game;
        setDoubleBuffered(true);

        // Load backgrounds
        bgImage1 = BufferedImageUtil.readImage("SpaceBG.png", GamePanel.class);
        bgImage2 = BufferedImageUtil.readImage("SpaceBG1.png", GamePanel.class);
        bgImage3 = BufferedImageUtil.readImage("SpaceBG2.png", GamePanel.class);
        bgImage4 = BufferedImageUtil.readImage("SpaceBG3.png", GamePanel.class);

        // Load project/laser image
        projectileImage = BufferedImageUtil.readImage("ProjectilePea.png", GamePanel.class);

        // Load ship assets
        shipImages[0] = BufferedImageUtil.readImage("ship_lvl1.png", GamePanel.class);
        shipImages[1] = BufferedImageUtil.readImage("ship_lvl2.png", GamePanel.class);
        shipImages[2] = BufferedImageUtil.readImage("ship_lvl3.png", GamePanel.class);
        shipImages[3] = BufferedImageUtil.readImage("ship_lvl4.png", GamePanel.class);

        // Load Asteroid Assets
        asteroidBigImage = BufferedImageUtil.readImage("asteroid_big.png", GamePanel.class);
        asteroidMidImage = BufferedImageUtil.readImage("asteroid_mid.png", GamePanel.class);
        asteroidSmallImage = BufferedImageUtil.readImage("asteroid_small.png", GamePanel.class);

        enemySoldierImage = BufferedImageUtil.readImage("enemy_soldier.png", GamePanel.class);
        enemyOfficerImage = BufferedImageUtil.readImage("enemy_officer.png", GamePanel.class);
        enemyBossImage = BufferedImageUtil.readImage("enemy_boss.png", GamePanel.class);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Nave nave = game.getNave();

        // --- 1. GET DYNAMIC MAP SIZES FROM GAME ---
        int mapWidth = game.getMapWidth();
        int mapHeight = game.getMapHeight();

        // --- 2. CAMERA VIEWPORT CALCULATIONS ---
        int screenWidth = getWidth();
        int screenHeight = getHeight();

        int cameraX = nave.getX() - screenWidth / 2 + nave.getWidth() / 2;
        int cameraY = nave.getY() - screenHeight / 2 + nave.getHeight() / 2;

        // Clamp camera using dynamic map bounds
        cameraX = Math.max(0, Math.min(cameraX, mapWidth - screenWidth));
        cameraY = Math.max(0, Math.min(cameraY, mapHeight - screenHeight));

        // --- 3. APPLY CAMERA TRANSLATION ---
        g2d.translate(-cameraX, -cameraY);

        // --- 4. DRAW BACKGROUND ACROSS THE ENTIRE WORLD ---
        if (bgImage1 != null && bgImage2 != null && bgImage3 != null && bgImage4 != null) {
            int imgWidth = bgImage1.getWidth();
            int imgHeight = bgImage1.getHeight();

            if (imgWidth > 0 && imgHeight > 0) {
                int col = 0;
                // Use dynamic mapWidth and mapHeight here!
                for (int x = 0; x < mapWidth; x += imgWidth, col++) {
                    int row = 0;
                    for (int y = 0; y < mapHeight; y += imgHeight, row++) {
                        int patternIndex = (row + col) % 3;
                        BufferedImage currentBg;

                        switch (patternIndex) {
                            case 0:  currentBg = bgImage1; break;
                            case 1:  currentBg = bgImage3; break;
                            case 2:  currentBg = bgImage2; break;
                            default: currentBg = bgImage4; break;
                        }
                        g2d.drawImage(currentBg, x, y, this);
                    }
                }
            }
        } else {
            g2d.setColor(Color.black);
            g2d.fillRect(0, 0, mapWidth, mapHeight);
        }

        // --- 4. DRAW ASTEROIDS ---
        for (Asteroid ast : game.getAsteroids()) {
            BufferedImage astImage = switch (ast.getSizeType()) {
                case BIG -> asteroidBigImage;
                case MID -> asteroidMidImage;
                case SMALL -> asteroidSmallImage;
            };

            int drawX = (int) ast.getX();
            int drawY = (int) ast.getY();

            if (astImage != null) {
                g2d.drawImage(astImage, drawX, drawY, ast.getWidth(), ast.getHeight(), this);
            } else {
                g2d.setColor(Color.gray);
                g2d.fillOval(drawX, drawY, ast.getWidth(), ast.getHeight());
            }
        }

        // --- 5. DRAW PLAYER SHIP ---
        int levelIndex = Math.max(0, Math.min(nave.getLevel() - 1, 3));
        BufferedImage currentShipImage = shipImages[levelIndex];

        if (currentShipImage != null) {
            g2d.drawImage(currentShipImage, nave.getX(), nave.getY(), nave.getWidth(), nave.getHeight(), this);
        } else {
            g2d.setColor(Color.yellow);
            g2d.fillRect(nave.getX(), nave.getY(), nave.getWidth(), nave.getHeight());
        }

        // --- 6. DRAW PROJECTILES ---
        for (Projectile p : game.getProjectiles()) {
            if (projectileImage != null) {
                g2d.drawImage(projectileImage, p.getX(), p.getY(), p.getWidth(), p.getHeight(), this);
            } else {
                g2d.setColor(Color.green);
                g2d.fillOval(p.getX(), p.getY(), p.getWidth(), p.getHeight());
            }
        }

        // --- DRAW ENEMIES ---
        for (Enemy enemy : game.getEnemies()) {
            BufferedImage enemyImg = switch (enemy.getEnemyType()) {
                case OFFICER -> enemyOfficerImage;
                case BOSS -> enemyBossImage;
                case SOLDIER -> enemySoldierImage;
            };

            int drawX = (int) enemy.getX();
            int drawY = (int) enemy.getY();

            if (enemyImg != null) {
                g2d.drawImage(enemyImg, drawX, drawY, enemy.getWidth(), enemy.getHeight(), this);
            } else {
                // Fallback red shape if images are missing
                g2d.setColor(Color.red);
                g2d.fillRect(drawX, drawY, enemy.getWidth(), enemy.getHeight());
            }
        }

        // Reset transform
        g2d.translate(cameraX, cameraY);

        // --- 1. DRAW HUD TIMER (Top Center) ---
        long elapsedSeconds = game.getElapsedSeconds();
        long minutes = elapsedSeconds / 60;
        long seconds = elapsedSeconds % 60;
        String timeText = String.format("Time: %02d:%02d", minutes, seconds);

        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(timeText);

        int textX = (getWidth() - textWidth) / 2;
        int textY = 30;

        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(textX - 10, textY - 18, textWidth + 20, 26);

        g2d.setColor(Color.WHITE);
        g2d.drawString(timeText, textX, textY);


        // --- 2. DRAW PLAYER STATS HUD (Top Left: HP & Energy Bars) ---
        int barX = 20;
        int barY = 20;
        int barWidth = 200;
        int barHeight = 16;
        int spacing = 22;

        // Draw Ship Level Indicator
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Level: " + nave.getLevel(), barX, barY - 5);

        // --- HP Bar ---
        g2d.setColor(new Color(50, 50, 50, 200)); // Background track
        g2d.fillRect(barX, barY, barWidth, barHeight);

        double hpPercent = Math.max(0, Math.min(1.0, nave.getHp() / nave.getMaxHp()));
        g2d.setColor(new Color(46, 204, 113)); // Green HP fill
        g2d.fillRect(barX, barY, (int) (barWidth * hpPercent), barHeight);

        g2d.setColor(Color.WHITE);
        g2d.drawRect(barX, barY, barWidth, barHeight); // Outline
        g2d.drawString(String.format("HP: %d/%d", (int)nave.getHp(), (int)nave.getMaxHp()), barX + 8, barY + 13);

        // --- Energy Bar ---
        int energyY = barY + spacing;
        g2d.setColor(new Color(50, 50, 50, 200)); // Background track
        g2d.fillRect(barX, energyY, barWidth, barHeight);

        double energyPercent = Math.max(0, Math.min(1.0, nave.getEnergy() / nave.getMaxEnergy()));
        g2d.setColor(new Color(52, 152, 219)); // Blue Energy fill
        g2d.fillRect(barX, energyY, (int) (barWidth * energyPercent), barHeight);

        g2d.setColor(Color.WHITE);
        g2d.drawRect(barX, energyY, barWidth, barHeight); // Outline
        g2d.drawString(String.format("Energy: %d/%d", (int)nave.getEnergy(), (int)nave.getMaxEnergy()), barX + 8, energyY + 13);
    }
}