package org.example.ui;

import org.example.model.Enemy;
import org.example.ui.interfaces.GameEntityRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EnemyRenderer implements GameEntityRenderer<Enemy> {
    private final BufferedImage soldierImg, officerImg, bossImg;

    public EnemyRenderer(BufferedImage soldier, BufferedImage officer, BufferedImage boss) {
        this.soldierImg = soldier;
        this.officerImg = officer;
        this.bossImg = boss;
    }

    @Override
    public void render(Graphics2D g2d, Enemy enemy, BufferedImage ignored) {
        BufferedImage img = switch (enemy.getEnemyType()) {
            case OFFICER -> officerImg;
            case BOSS -> bossImg;
            case SOLDIER -> soldierImg;
        };

        int drawX = (int) enemy.getX();
        int drawY = (int) enemy.getY();

        if (img != null) {
            g2d.drawImage(img, drawX, drawY, enemy.getWidth(), enemy.getHeight(), null);
        } else {
            g2d.setColor(Color.red);
            g2d.fillRect(drawX, drawY, enemy.getWidth(), enemy.getHeight());
        }
    }
}