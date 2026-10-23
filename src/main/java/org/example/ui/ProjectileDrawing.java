package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.UUID;

/**
 * ProjectileDrawing (el disparo)
 *
 * @author Marcos Quispe
 * @since 1.0
 */
public class ProjectileDrawing extends JComponent {
    public static final int SIZE = 10; // ataque pequeño
    private static final int DISTANCIA_MAX = 200; // px
    private static final int TIEMPO_AVANCE = 5; // ms por cada px

    // se carga una sola vez para todos los disparos
    private static final BufferedImage IMAGEN = BufferedImageUtil.readImage("ProjectilePea.png", ProjectileDrawing.class);

    private final String id;
    private final int xInicial;
    private final IEventosShape iEventosShape;
    private long prevTime;
    private boolean finalizar = false;

    public ProjectileDrawing(int x, int y, IEventosShape iEventosShape) {
        id = UUID.randomUUID().toString();
        xInicial = x;
        this.iEventosShape = iEventosShape;
        prevTime = System.currentTimeMillis();
        setBounds(x, y, SIZE, SIZE); // obligatorio
    }

    public String getId() {
        return id;
    }

    public boolean isFinalizar() {
        return finalizar;
    }

    public void revisarAvance() {
        if (finalizar) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - prevTime > TIEMPO_AVANCE) {
            setLocation(getX() + 1, getY()); // update position
            prevTime = currentTime;

            if (getX() - xInicial >= DISTANCIA_MAX) {
                finalizar = true;
                iEventosShape.delete(id);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (IMAGEN != null) {
            g2d.drawImage(IMAGEN, 0, 0, SIZE, SIZE, this);
        } else {
            g2d.setColor(Color.green);
            g2d.fillOval(0, 0, SIZE, SIZE);
        }
    }
}
