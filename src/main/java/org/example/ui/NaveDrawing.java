package org.example.ui;

import lombok.Getter;
import org.example.model.Nave;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class NaveDrawing extends JComponent {
    private BufferedImage bi;

    private Nave nave;
    private boolean finalizar = false;
    private int frame = 0; // 0 - 7
    private int cantframes = 8;
    private long prevTime;

    public NaveDrawing(Nave nave) {
        this.nave = nave;
        prevTime = System.currentTimeMillis();
        setBounds(nave.getX(), nave.getY(), nave.getWidth(), nave.getHeight()); // obligatorio

        InputStream inputStream = null;
        try {
            // cuando no logre leer de resources, borrar en nombre con las comillas mas y escribirlo manualmente. a veces se copia algun caracter raro
            inputStream = this.getClass().getClassLoader().getResourceAsStream("Nave.png"); // no funciona bien con webp
            bi = ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getId() {
        return nave.getId();
    }

//    public void startMovement() {
//        new Thread(() -> {
////            long prevTime = System.currentTimeMillis();
////            long currentTime;
//            while (!isFinalizar()) {
////                currentTime = System.currentTimeMillis();
////                if (currentTime - prevTime > timeAttack) {
////                    iImageShape.throwProjectile(this);
////                    prevTime = currentTime;
////                }
//
//                try {
//                    Thread.sleep(300);//timeAttack);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                //frame = ((frame + 1) % 8) + 1;
//                frame = (frame + 1) % cantframes;
//                //System.out.println("frame: " + frame);
//                repaint();
//            }
//            System.out.println("Hilo dentro de image finalizado!");
//        }).start();
//    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

//        g2d.setColor(Color.red);
//        g2d.fillRect(0, 0, WIDTH, WIDTH);

        long currentTime = System.currentTimeMillis();
        if (currentTime - prevTime > 300) {
            frame = (frame + 1) % cantframes;
            prevTime = currentTime;
        }

        if (bi != null) {
            //System.out.println("dibujando frame: " + frame);
            int fxInitial = 170;
            int fyInitial = 13;
            //frame = 9; // quitar luego
            int xSprite = fxInitial + (frame * 26) + (frame * 3);
            g.drawImage(bi, 0, 0, nave.getWidth(), nave.getHeight()
                    , xSprite, fyInitial
                    , xSprite + 26, fyInitial + 32, this);
        }
    }
}
