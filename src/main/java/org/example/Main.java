package org.example;

import org.example.logic.Game;
import org.example.logic.Hilo;
import org.example.ui.Frame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Initialize Model / Logic
            Game game = new Game();

            // 2. Initialize View (passing game data so it can draw it)
            Frame frame = new Frame(game);

            // 3. Initialize Controller / Game Loop
            Hilo hilo = new Hilo(game, frame);

            // Optional: Start the thread
            new Thread(hilo).start();

            System.out.println("Starblast.io initialized with MVC architecture!");
        });
    }
}