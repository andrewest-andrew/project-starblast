package org.example.ui;

import org.example.model.Asteroid;
import org.example.ui.interfaces.GameEntityRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;

public class AsteroidRenderer implements GameEntityRenderer<Asteroid> {
    private final BufferedImage bigImg, midImg, smallImg;

    public AsteroidRenderer(BufferedImage big, BufferedImage mid, BufferedImage small) {
        this.bigImg = big;
        this.midImg = mid;
        this.smallImg = small;
    }

    @Override
    public void render(Graphics2D g2d, Asteroid ast, BufferedImage ignored) {
        BufferedImage img = switch (ast.getSizeType()) {
            case BIG -> bigImg;
            case MID -> midImg;
            case SMALL -> smallImg;
        };

        int drawX = (int) ast.getX();
        int drawY = (int) ast.getY();

        if (img != null) {
            g2d.drawImage(img, drawX, drawY, ast.getWidth(), ast.getHeight(), null);
        } else {
            g2d.setColor(Color.gray);
            g2d.fillOval(drawX, drawY, ast.getWidth(), ast.getHeight());
        }
    }
}