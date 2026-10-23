package org.example.logic;

import org.example.ui.Frame;

public class Hilo implements Runnable {
    private final Game game;
    private final Frame frame;
    private volatile boolean running = true;

    public Hilo(Game game, Frame frame) {
        this.game = game;
        this.frame = frame;
    }

    @Override
    public void run() {
        final double targetFPS = 60.0;
        final double nsPerTick = 1000000000.0 / targetFPS;
        long lastTime = System.nanoTime();
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;

            while (delta >= 1) {
                game.actualizar();
                delta--;
            }

            frame.repaintCanvas();

//            if (delta >= 1) {
//                // 1. Update game state (Model / Logic)
//                game.actualizar();
//                // 2. Refresh screen (View)
//                frame.repaintCanvas();
//                delta--;
//            }

            // Small CPU breather
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running = false;
    }
}