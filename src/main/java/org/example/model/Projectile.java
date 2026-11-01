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
    private double startX, startY; // Track origin to measure max travel distance
    private double x, y; // Sub-pixel precision
    private int width, height;
    private double damage;
    private double vx, vy; // Velocity components for directional movement
    private boolean enemyProjectile; // True if fired by an enemy
    private boolean piercing;      // Pass-through flag for level 4 alt-fire
    private double maxRange;       // Max travel distance in pixels
    private double angle;

    public Projectile(double x, double y, int width, int height, double damage, double vx, double vy, boolean enemyProjectile, boolean piercing, double maxRange) {
        this.x = x;
        this.y = y;
        this.startX = x;
        this.startY = y;
        this.width = width;
        this.height = height;
        this.damage = damage;
        this.vx = vx;
        this.vy = vy;
        this.enemyProjectile = enemyProjectile;
        this.piercing = piercing;
        this.maxRange = maxRange;

        this.angle = Math.toDegrees(Math.atan2(vy, vx)) + 90;
    }

    public boolean hasExceededRange() {
        double dx = x - startX;
        double dy = y - startY;
        return Math.sqrt(dx * dx + dy * dy) >= maxRange;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }
}