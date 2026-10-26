package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class Enemy {
    public enum Type { SOLDIER, OFFICER, BOSS }

    private String id;
    private double x, y;
    private int width, height;
    private double vx, vy;
    private Type enemyType;
    private long lastShotTime = 0;
    private boolean aggro = false;

    public Enemy(double x, double y, Type enemyType) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.enemyType = enemyType;

        // Size scaling based on type
        switch (enemyType) {
            case OFFICER:
                this.width = 80;
                this.height = 80;
                break;
            case BOSS:
                this.width = 200;
                this.height = 200;
                break;
            case SOLDIER:
            default:
                this.width = 60;
                this.height = 60;
                break;
        }

        // Gentle drift velocity
        this.vx = (Math.random() * 2.0 - 1.0) * 0.8;
        this.vy = (Math.random() * 2.0 - 1.0) * 0.8;
    }

    // Keep enemies inside the 3000x3000 map bounds just like asteroids
    public void checkBounds(int mapWidth, int mapHeight) {
        if (x <= 0) {
            x = 0;
            vx = -vx;
        } else if (x + width >= mapWidth) {
            x = mapWidth - width;
            vx = -vx;
        }

        if (y <= 0) {
            y = 0;
            vy = -vy;
        } else if (y + height >= mapHeight) {
            y = mapHeight - height;
            vy = -vy;
        }
    }
}