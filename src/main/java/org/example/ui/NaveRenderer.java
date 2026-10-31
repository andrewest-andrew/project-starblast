package org.example.ui;

import org.example.model.Nave;
import org.example.ui.interfaces.GameEntityRenderer;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class NaveRenderer implements GameEntityRenderer<Nave> {
    @Override
    public void render(Graphics2D g2d, Nave nave, BufferedImage image) {
        if (image == null) return;

        AffineTransform oldTransform = g2d.getTransform();

        // Calculate center of the ship bounding box
        double centerX = nave.getX() + nave.getWidth() / 2.0;
        double centerY = nave.getY() + nave.getHeight() / 2.0;

        // Translate to ship center, rotate, and draw centered
        g2d.translate(centerX, centerY);
        g2d.rotate(Math.toRadians(nave.getAngle()));

        g2d.drawImage(image, -nave.getWidth() / 2, -nave.getHeight() / 2, nave.getWidth(), nave.getHeight(), null);

        g2d.setTransform(oldTransform);
    }
}