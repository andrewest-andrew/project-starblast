package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

/**
 * Projectile
 *
 * @author Marcos Quispe
 * @since 1.0
 */
@Getter
@Setter
@ToString
public class Projectile {
    private String id;
    private int x;
    private int y;
    private int width;
    private int height;
    private int timeAdvance = 5; // 5ms
    private long prevTime;

    public Projectile(int x, int y, int width, int height) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.prevTime = System.currentTimeMillis();
    }

    public void avanzar() {
        x = x + 1;
    }
}
