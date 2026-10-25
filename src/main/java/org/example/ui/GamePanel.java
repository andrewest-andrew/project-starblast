package org.example.ui;

import org.example.logic.Game;
import org.example.model.Asteroid;
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

    private static final int MAP_WIDTH = 3000;
    private static final int MAP_HEIGHT = 3000;

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
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Nave nave = game.getNave();

        // --- 1. CAMERA VIEWPORT CALCULATIONS ---
        int screenWidth = getWidth();
        int screenHeight = getHeight();

        // Center camera on the player ship
        int cameraX = nave.getX() - screenWidth / 2 + nave.getWidth() / 2;
        int cameraY = nave.getY() - screenHeight / 2 + nave.getHeight() / 2;

        // Clamp camera to stay strictly within the 3000x3000 world bounds
        cameraX = Math.max(0, Math.min(cameraX, MAP_WIDTH - screenWidth));
        cameraY = Math.max(0, Math.min(cameraY, MAP_HEIGHT - screenHeight));

        // --- 2. APPLY CAMERA TRANSLATION ---
        // Everything drawn after this point shifts relative to the camera viewport
        g2d.translate(-cameraX, -cameraY);

        // --- 3. DRAW BACKGROUND ACROSS THE ENTIRE 3000x3000 WORLD ---
        if (bgImage1 != null && bgImage2 != null && bgImage3 != null && bgImage4 != null) {
            int imgWidth = bgImage1.getWidth();
            int imgHeight = bgImage1.getHeight();

            if (imgWidth > 0 && imgHeight > 0) {
                int col = 0;
                for (int x = 0; x < MAP_WIDTH; x += imgWidth, col++) {
                    int row = 0;
                    for (int y = 0; y < MAP_HEIGHT; y += imgHeight, row++) {
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
            g2d.fillRect(0, 0, MAP_WIDTH, MAP_HEIGHT);
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

        // Reset transform
        g2d.translate(cameraX, cameraY);
    }
}