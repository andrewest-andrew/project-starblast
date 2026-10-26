package org.example.ui;

import org.example.model.Nave;
import org.example.ui.interfaces.GameEntityRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NaveRenderer implements GameEntityRenderer<Nave> {
    @Override
    public void render(Graphics2D g2d, Nave nave, BufferedImage image) {
        if (image != null) {
            g2d.drawImage(image, nave.getX(), nave.getY(), nave.getWidth(), nave.getHeight(), null);
        } else {
            g2d.setColor(Color.yellow);
            g2d.fillRect(nave.getX(), nave.getY(), nave.getWidth(), nave.getHeight());
        }
    }
}