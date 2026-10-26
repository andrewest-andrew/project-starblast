package org.example.logic;

import lombok.Getter;
import org.example.model.Asteroid;
import org.example.model.Enemy;
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
    private final List<Enemy> enemies = new ArrayList<>();

    // --- CENTRALIZED MAP DIMENSIONS ---
    private final int mapWidth = 2500;
    private final int mapHeight = 1500;

    private long gameStartTime = System.currentTimeMillis();
    private boolean wave1 = false;
    private boolean wave2 = false;
    private boolean wave3 = false;

    private long lastShotTime;
    private static final long SHOOT_COOLDOWN = 250; // 250ms

    private boolean movingUp, movingDown, movingLeft, movingRight;
    private boolean shooting;

    public Game() {
        // Initialize player ship at x=100, y=300
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
            do {
                x = rand.nextInt(mapWidth - 200) + 100;
                y = rand.nextInt(mapHeight - 200) + 100;
            } while (Math.abs(x - safeZoneX) < safeRadius && Math.abs(y - safeZoneY) < safeRadius);

            Asteroid.Size size;
            int roll = rand.nextInt(3);
            if (roll == 0) size = Asteroid.Size.BIG;
            else if (roll == 1) size = Asteroid.Size.MID;
            else size = Asteroid.Size.SMALL;

            asteroids.add(new Asteroid(x, y, size));
        }
    }

    private void spawnEnemies(int soldiers, int officers, int bosses) {
        Random rand = new Random();
        for (int i = 0; i < soldiers; i++) {
            enemies.add(new Enemy(rand.nextInt(mapWidth - 200) + 100, rand.nextInt(mapHeight - 200) + 100, Enemy.Type.SOLDIER));
        }
        for (int i = 0; i < officers; i++) {
            enemies.add(new Enemy(rand.nextInt(mapWidth - 200) + 100, rand.nextInt(mapHeight - 200) + 100, Enemy.Type.OFFICER));
        }
        for (int i = 0; i < bosses; i++) {
            enemies.add(new Enemy(rand.nextInt(mapWidth - 200) + 100, rand.nextInt(mapHeight - 200) + 100, Enemy.Type.BOSS));
        }
    }

    // Input handlers called by the View layer
    public void setMovingUp(boolean movingUp) { this.movingUp = movingUp; }
    public void setMovingDown(boolean movingDown) { this.movingDown = movingDown; }
    public void setMovingLeft(boolean movingLeft) { this.movingLeft = movingLeft; }
    public void setMovingRight(boolean movingRight) { this.movingRight = movingRight; }
    public void setShooting(boolean shooting) { this.shooting = shooting; }

    // Called every frame by Hilo
    public void actualizar() {
        // 1. Handle Ship Movement using dynamic ship speed
        int shipSpeed = nave.getSpeed();
        int newX = nave.getX();
        int newY = nave.getY();

        if (movingUp) newY -= shipSpeed;
        if (movingDown) newY += shipSpeed;
        if (movingLeft) newX -= shipSpeed;
        if (movingRight) newX += shipSpeed;

        // Keep ship inside dynamic map bounds
        newX = Math.max(0, Math.min(newX, mapWidth - nave.getWidth()));
        newY = Math.max(0, Math.min(newY, mapHeight - nave.getHeight()));

        nave.setX(newX);
        nave.setY(newY);

        // --- PASSIVE REGENERATION (Assuming ~60 ticks per second) ---
        // HP regenerates at 3 HP/s -> 3.0 / 60 per tick = 0.05 per tick
        if (nave.getHp() < nave.getMaxHp()) {
            nave.setHp(Math.min(nave.getMaxHp(), nave.getHp() + (3.0 / 60.0)));
        }
        // Energy regenerates at 4 Energy/s -> 4.0 / 60 per tick = ~0.0667 per tick
        if (nave.getEnergy() < nave.getMaxEnergy()) {
            nave.setEnergy(Math.min(nave.getMaxEnergy(), nave.getEnergy() + (6.0 / 60.0)));
        }

        // 2. Wave Timer & Enemy Spawning Logic
        long elapsedSeconds = getElapsedSeconds();

        if (elapsedSeconds >= 30 && !wave1) {
            spawnEnemies(3, 0, 0);
            wave1 = true;
        }
        if (elapsedSeconds >= 60 && !wave2) {
            spawnEnemies(3, 2, 0);
            wave2 = true;
        }
        if (elapsedSeconds >= 180 && !wave3) {
            spawnEnemies(3, 2, 1);
            wave3 = true;
        }

        // 3. Update Enemies
        for (Enemy enemy : enemies) {
            enemy.setX(enemy.getX() + enemy.getVx());
            enemy.setY(enemy.getY() + enemy.getVy());
            enemy.checkBounds(mapWidth, mapHeight);
        }

        // 4. Manual Shooting Logic with Energy & Level Rules
        if (shooting) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShotTime >= SHOOT_COOLDOWN) {
                tryShoot();
            }
        }

        // 5. Update Projectiles
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile p = iterator.next();
            p.setY(p.getY() - 6);

            if (p.getY() < 0) {
                iterator.remove();
            }
        }

        // 6. Update Asteroids
        for (Asteroid ast : asteroids) {
            ast.setX(ast.getX() + ast.getVx());
            ast.setY(ast.getY() + ast.getVy());
            ast.checkBounds(mapWidth, mapHeight);
        }
    }

    private void tryShoot() {
        int level = nave.getLevel();
        int energyCost = (level == 1) ? 5 : 10;

        // Check if ship has enough energy for at least one shot
        if (nave.getEnergy() >= energyCost) {
            nave.setEnergy(nave.getEnergy() - energyCost);
            dispararProjectile();
            lastShotTime = System.currentTimeMillis();
        }
    }

    private void dispararProjectile() {
        int pWidth = 10;
        int pHeight = 15;
        int damage = 10;
        int level = nave.getLevel();

        if (level == 1) {
            // Level 1: Single shot
            int px = nave.getX() + (nave.getWidth() - pWidth) / 2;
            int py = nave.getY();
            projectiles.add(new Projectile(px, py, pWidth, pHeight, damage));
        } else {
            // Level 2, 3, 4: 3-shot burst spread out horizontally
            int leftX = nave.getX() + 5;
            int centerX = nave.getX() + (nave.getWidth() - pWidth) / 2;
            int rightX = nave.getX() + nave.getWidth() - pWidth - 5;
            int py = nave.getY();

            projectiles.add(new Projectile(leftX, py + 10, pWidth, pHeight, damage));
            projectiles.add(new Projectile(centerX, py, pWidth, pHeight, damage));
            projectiles.add(new Projectile(rightX, py + 10, pWidth, pHeight, damage));
        }
    }

    public long getElapsedSeconds() {
        return (System.currentTimeMillis() - gameStartTime) / 1000;
    }
}