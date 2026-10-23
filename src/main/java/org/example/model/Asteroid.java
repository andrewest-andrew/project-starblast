package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class Asteroid {
    public enum Size { BIG, MID, SMALL }

    private String id;
    private double x, y;
    private int width, height;
    private double vx, vy; // Velocity vectors for floating drift
    private Size sizeType;

    public Asteroid(int x, int y, Size sizeType) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.sizeType = sizeType;

        // Set dimensions based on size type
        switch (sizeType) {
            case BIG:
                this.width = 80;
                this.height = 80;
                break;
            case MID:
                this.width = 50;
                this.height = 50;
                break;
            case SMALL:
            default:
                this.width = 30;
                this.height = 30;
                break;
        }

        // Give them a random slow drift velocity (e.g., between -1.5 and 1.5)
        this.vx = (Math.random() * 2.0 - 1.0) * 1.5;
        this.vy = (Math.random() * 2.0 - 1.0) * 1.5;
    }

    // Bounce off map walls (0 to mapWidth/mapHeight)
    public void checkBounds(int mapWidth, int mapHeight) {
        if (x <= 0) {
            x = 0;
            vx = -vx; // Reverse horizontal direction
        } else if (x + width >= mapWidth) {
            x = mapWidth - width;
            vx = -vx;
        }

        if (y <= 0) {
            y = 0;
            vy = -vy; // Reverse vertical direction
        } else if (y + height >= mapHeight) {
            y = mapHeight - height;
            vy = -vy;
        }
    }
}