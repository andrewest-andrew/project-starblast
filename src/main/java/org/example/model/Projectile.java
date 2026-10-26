package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.model.interfaces.Collidable;

import java.awt.*;

@Getter
@Setter
@ToString
public class Projectile implements Collidable {
    private double x, y; // Sub-pixel precision
    private int width, height;
    private int damage;
    private double vx, vy; // Velocity components for directional movement
    private boolean enemyProjectile; // True if fired by an enemy

    public Projectile(double x, double y, int width, int height, int damage, double vx, double vy, boolean enemyProjectile) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.damage = damage;
        this.vx = vx;
        this.vy = vy;
        this.enemyProjectile = enemyProjectile;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }
}