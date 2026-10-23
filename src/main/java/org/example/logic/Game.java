package org.example.logic;

import lombok.Getter;
import org.example.model.Asteroid;
import org.example.model.Nave;
import org.example.model.Projectile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Getter
public class Game {
    private Nave nave;
    private List<Projectile> projectiles;
    private List<Asteroid> asteroids;

    private long lastShotTime;
    private static final long SHOOT_COOLDOWN = 250; // 1 second

    private boolean movingUp, movingDown, movingLeft, movingRight;
    private boolean shooting; // New: Tracks spacebar state
    private static final int SHIP_SPEED = 4; // Pixels per tick

    public Game() {
        // Initialize player ship at x=100, y=300 with size 30x30
        this.nave = new Nave(100, 300, 100, 100);
        this.projectiles = new ArrayList<>();
        this.asteroids = new ArrayList<>();
        this.lastShotTime = System.currentTimeMillis();

        spawnAsteroids();
    }

    private void spawnAsteroids() {
        Random rand = new Random();
        int totalAsteroids = 25;
        int safeZoneX = nave.getX();
        int safeZoneY = nave.getY();
        int safeRadius = 300; // No asteroids within 300px of player spawn

        for (int i = 0; i < totalAsteroids; i++) {
            int x, y;
            // Keep generating random coordinates until outside the safe zone
            do {
                x = rand.nextInt(2800) + 100; // Keep inside map borders
                y = rand.nextInt(2800) + 100;
            } while (Math.abs(x - safeZoneX) < safeRadius && Math.abs(y - safeZoneY) < safeRadius);

            // Randomly assign a size (Big, Mid, Small)
            Asteroid.Size size;
            int roll = rand.nextInt(3);
            if (roll == 0) size = Asteroid.Size.BIG;
            else if (roll == 1) size = Asteroid.Size.MID;
            else size = Asteroid.Size.SMALL;

            asteroids.add(new Asteroid(x, y, size));
        }
    }

    // Input handlers called by the View layer
    public void setMovingUp(boolean movingUp) { this.movingUp = movingUp; }
    public void setMovingDown(boolean movingDown) { this.movingDown = movingDown; }
    public void setMovingLeft(boolean movingLeft) { this.movingLeft = movingLeft; }
    public void setMovingRight(boolean movingRight) { this.movingRight = movingRight; }
    public void setShooting(boolean shooting) { this.shooting = shooting; } // New setter

    // Called every frame by Hilo
    public void actualizar() {
        // 1. Handle Ship Movement based on held keys
        int newX = nave.getX();
        int newY = nave.getY();

        if (movingUp) newY -= SHIP_SPEED;
        if (movingDown) newY += SHIP_SPEED;
        if (movingLeft) newX -= SHIP_SPEED;
        if (movingRight) newX += SHIP_SPEED;

        // Optional: Keep ship inside the 3000x3000 map bounds
        newX = Math.max(0, Math.min(newX, 3000 - nave.getWidth()));
        newY = Math.max(0, Math.min(newY, 3000 - nave.getHeight()));

        nave.setX(newX);
        nave.setY(newY);

        // Manual Shooting Logic (Triggers when spacebar is held, subject to cooldown)
        if (shooting) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShotTime >= SHOOT_COOLDOWN) {
                dispararProjectile();
                lastShotTime = currentTime;
            }
        }

        // Update all active projectiles' positions
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile p = iterator.next();
            p.setY(p.getY() - 6); // Laser speed

            if (p.getY() < 0) {
                iterator.remove();
            }
        }

        // Update Asteroids (floating drift & screen wrapping)
        for (Asteroid ast : asteroids) {
            ast.setX((int) (ast.getX() + ast.getVx()));
            ast.setY((int) (ast.getY() + ast.getVy()));

            // Bounce off the 3000x3000 world boundaries
            ast.checkBounds(3000, 3000);
        }
    }

    private void dispararProjectile() {
        int pWidth = 10;
        int pHeight = 15;
        int px = nave.getX() + (nave.getWidth() - pWidth) / 2;
        int py = nave.getY(); // Spawns right at the nose of the ship
        projectiles.add(new Projectile(px, py, pWidth, pHeight));
    }
}