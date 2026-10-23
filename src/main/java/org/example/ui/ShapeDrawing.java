package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

/**
 * ShapeDrawing (la nave)
 *
 * @author Marcos Quispe
 * @since 1.0
 */
public class ShapeDrawing extends JComponent {
    public static final int SIZE = 30;
    private static final int TIEMPO_DISPARO = 1000; // 1seg

    private final String id;
    private long prevTimeDisparo;

    private final IEventosShape iEventosShape;

    public ShapeDrawing(int x, int y, IEventosShape iEventosShape) {
        id = UUID.randomUUID().toString();
        this.iEventosShape = iEventosShape;
        prevTimeDisparo = System.currentTimeMillis();
        setBounds(x, y, SIZE, SIZE); // obligatorio
    }

    public String getId() {
        return id;
    }

    /**
     * Devuelve true cuando ya paso 1 segundo desde el ultimo disparo.
     */
    public boolean puedeDisparar() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - prevTimeDisparo >= TIEMPO_DISPARO) {
            prevTimeDisparo = currentTime;
            return true;
        }
        return false;
    }

    /**
     * Crea un disparo que sale desde el lado derecho de la nave, centrado verticalmente.
     */
    public ProjectileDrawing disparar() {
        int px = getX() + SIZE;
        int py = getY() + (SIZE - ProjectileDrawing.SIZE) / 2;
        return new ProjectileDrawing(px, py, iEventosShape);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.yellow);
        g2d.fillRect(0, 0, SIZE, SIZE);

        g2d.setColor(Color.black);
        g2d.drawRect(0, 0, SIZE - 1, SIZE - 1);
    }
}
