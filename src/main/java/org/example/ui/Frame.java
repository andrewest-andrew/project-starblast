package org.example.ui;

import org.example.logic.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Frame extends JFrame {
    private final GamePanel gamePanel;
    private final Game game;

    public Frame(Game game) throws HeadlessException {
        this.game = game;
        setTitle("Starblast - Andrew's Build");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Ensure BorderLayout is set so components can expand
        setLayout(new BorderLayout());

        // 1. Initialize and add the GamePanel to the CENTER first
        gamePanel = new GamePanel(game);
        add(gamePanel, BorderLayout.CENTER);

        // 2. Configure window states
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);

        // 3. Add Keyboard Listener for WASD controls
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                updateKeyState(e.getKeyCode(), true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                updateKeyState(e.getKeyCode(), false);
            }
        });

        setFocusable(true);
        requestFocusInWindow();

        // 4. Make visible ONCE at the very end of initialization
        setVisible(true);
        System.out.println("Frame window created with WASD controls.");
    }

    private void updateKeyState(int keyCode, boolean isPressed) {
        // Handle Spacebar for shooting
        if (keyCode == KeyEvent.VK_SPACE) {
            game.setShooting(isPressed);
            return;
        }

        switch (Character.toLowerCase((char) keyCode)) {
            case 'w': game.setMovingUp(isPressed); break;
            case 's': game.setMovingDown(isPressed); break;
            case 'a': game.setMovingLeft(isPressed); break;
            case 'd': game.setMovingRight(isPressed); break;
            default:
                // Handle arrow keys as alternatives if desired
                if (keyCode == KeyEvent.VK_UP) game.setMovingUp(isPressed);
                if (keyCode == KeyEvent.VK_DOWN) game.setMovingDown(isPressed);
                if (keyCode == KeyEvent.VK_LEFT) game.setMovingLeft(isPressed);
                if (keyCode == KeyEvent.VK_RIGHT) game.setMovingRight(isPressed);
                break;
        }
    }

    public void repaintCanvas() {
        gamePanel.repaint();
    }
}