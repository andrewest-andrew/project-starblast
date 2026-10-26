package org.example.ui.interfaces;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public interface GameEntityRenderer<T> {
    void render(Graphics2D g2d, T entity, BufferedImage image);
}