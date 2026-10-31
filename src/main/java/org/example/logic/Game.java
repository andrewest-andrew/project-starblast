package org.example.logic;

import lombok.Getter;
import org.example.logic.interfaces.GameStateListener;
import org.example.model.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Getter
public class Game implements GameStateListener {
    private Nave nave;
    private List<Projectile> projectiles;
    private List<Asteroid> asteroids;
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Gem> gems = new ArrayList<>();

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

    private boolean gameWon = false;
    private boolean gameOver = false;

    @Getter
    private int collectedGems = 0;

    @Override
    public void onBossDefeated() {
        this.gameWon = true;
    }

    @Override
    public void onPlayerDefeated() {
        this.gameOver = true;
    }

    public void resetGame() {
        this.nave = new Nave(1250, 750);
        this.projectiles.clear();
        this.asteroids.clear();
        this.enemies.clear();
        this.gems.clear();
        this.collectedGems = 0;
        this.gameStartTime = System.currentTimeMillis();
        this.wave1 = false;
        this.wave2 = false;
        this.wave3 = false;
        this.gameWon = false;
        this.gameOver = false;
        spawnAsteroids();
    }

    public Game() {
        // Initialize player ship at x,y
        this.nave = new Nave(1250, 750);
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
        if (gameWon || gameOver) return; // Stop updating game logic once won

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
        if (nave.getHp() < nave.getMaxHp()) {
            nave.setHp(Math.min(nave.getMaxHp(), nave.getHp() + (3.0 / 60.0)));
        }
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
        if (elapsedSeconds >= 90 && !wave3) {
            spawnEnemies(3, 2, 1);
            wave3 = true;
        }

        // 3. Update Enemies
        double playerCenterX = nave.getX() + nave.getWidth() / 2.0;
        double playerCenterY = nave.getY() + nave.getHeight() / 2.0;

        for (Enemy enemy : enemies) {
            double enemyCenterX = enemy.getX() + enemy.getWidth() / 2.0;
            double enemyCenterY = enemy.getY() + enemy.getHeight() / 2.0;

            double dx = playerCenterX - enemyCenterX;
            double dy = playerCenterY - enemyCenterY;
            double distance = Math.sqrt(dx * dx + dy * dy);

            // --- AGGRO & CHASING LOGIC ---
            if (distance <= 400.0) {
                enemy.setAggro(true);
            }

            if (enemy.isAggro()) {
                double speed = (enemy.getEnemyType() == Enemy.Type.BOSS) ? 1.5 : 2.0;
                if (distance > 5) {
                    enemy.setVx((dx / distance) * speed);
                    enemy.setVy((dy / distance) * speed);
                } else {
                    enemy.setVx(0);
                    enemy.setVy(0);
                }
            }

            // Apply movement
            enemy.setX(enemy.getX() + enemy.getVx());
            enemy.setY(enemy.getY() + enemy.getVy());
            enemy.checkBounds(mapWidth, mapHeight);

            // --- ENEMY SHOOTING LOGIC ---
            if (distance <= 350.0) {
                long currentTime = System.currentTimeMillis();
                long fireCooldown = (enemy.getEnemyType() == Enemy.Type.BOSS) ? 800 : 1200;

                if (currentTime - enemy.getLastShotTime() >= fireCooldown) {
                    double length = distance;
                    if (length > 0) {
                        double bulletSpeed = 5.0;
                        double pvx = (dx / length) * bulletSpeed;
                        double pvy = (dy / length) * bulletSpeed;

                        int pWidth = 10;
                        int pHeight = 25;
                        double px = enemyCenterX - pWidth / 2.0;
                        double py = enemyCenterY - pHeight / 2.0;

                        projectiles.add(new Projectile(px, py, pWidth, pHeight, 10, pvx, pvy, true));
                        enemy.setLastShotTime(currentTime);
                    }
                }
            }
        }

        // 4. Manual Shooting Logic with Energy & Level Rules
        if (shooting) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShotTime >= SHOOT_COOLDOWN) {
                tryShoot();
            }
        }

        // 5. Update Projectiles & Check Collisions
        Iterator<Projectile> projectileIterator = projectiles.iterator();
        while (projectileIterator.hasNext()) {
            Projectile p = projectileIterator.next();

            p.setX(p.getX() + p.getVx());
            p.setY(p.getY() + p.getVy());

            if (p.getX() < 0 || p.getX() > mapWidth || p.getY() < 0 || p.getY() > mapHeight) {
                projectileIterator.remove();
                continue;
            }

            Rectangle pBounds = p.getBounds();
            boolean projectileRemoved = false;

            if (p.isEnemyProjectile()) {
                if (pBounds.intersects(nave.getBounds())) {
                    nave.takeDamage(10);
                    projectileRemoved = true;
                }
            } else {
                Iterator<Enemy> enemyIterator = enemies.iterator();
                while (enemyIterator.hasNext()) {
                    Enemy enemy = enemyIterator.next();
                    if (pBounds.intersects(enemy.getBounds())) {
                        enemy.takeDamage(p.getDamage());
                        projectileRemoved = true;
                        break;
                    }
                }

                if (!projectileRemoved) {
                    Iterator<Asteroid> asteroidIterator = asteroids.iterator();
                    while (asteroidIterator.hasNext()) {
                        Asteroid ast = asteroidIterator.next();
                        if (pBounds.intersects(ast.getBounds())) {
                            ast.takeDamage(p.getDamage());
                            projectileRemoved = true;
                            break;
                        }
                    }
                }
            }

            if (projectileRemoved) {
                projectileIterator.remove();
            }
        }

        // 6. Update Asteroids (Drift and Bounce)
        for (Asteroid ast : asteroids) {
            ast.setX(ast.getX() + ast.getVx());
            ast.setY(ast.getY() + ast.getVy());
            ast.checkBounds(mapWidth, mapHeight);
        }

        // --- 7. CHECK PLAYER VS ASTEROID COLLISIONS & KNOCKBACK ---
        Rectangle naveBounds = nave.getBounds();
        for (Asteroid ast : asteroids) {
            if (naveBounds.intersects(ast.getBounds())) {
                double damage = switch (ast.getSizeType()) {
                    case BIG -> 30.0;
                    case MID -> 15.0;
                    case SMALL -> 5.0;
                };
                nave.takeDamage(damage);

                double astCenterX = ast.getX() + ast.getWidth() / 2.0;
                double astCenterY = ast.getY() + ast.getHeight() / 2.0;

                double dx = playerCenterX - astCenterX;
                double dy = playerCenterY - astCenterY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance == 0) {
                    dx = 1;
                    dy = 0;
                    distance = 1;
                }

                int pushBackDistance = 60;
                int pushedX = (int) (nave.getX() + (dx / distance) * pushBackDistance);
                int pushedY = (int) (nave.getY() + (dy / distance) * pushBackDistance);

                pushedX = Math.max(0, Math.min(pushedX, mapWidth - nave.getWidth()));
                pushedY = Math.max(0, Math.min(pushedY, mapHeight - nave.getHeight()));

                nave.setX(pushedX);
                nave.setY(pushedY);
                break;
            }
        }

        // --- 8. UPDATE & ATTRACT GEMS ---
        Iterator<Gem> gemIterator = gems.iterator();
        while (gemIterator.hasNext()) {
            Gem gem = gemIterator.next();
            double gemCenterX = gem.getX() + gem.getWidth() / 2.0;
            double gemCenterY = gem.getY() + gem.getHeight() / 2.0;

            double gdx = playerCenterX - gemCenterX;
            double gdy = playerCenterY - gemCenterY;
            double gdist = Math.sqrt(gdx * gdx + gdy * gdy);

            // Attraction radius (250px)
            if (gdist <= 250.0) {
                double speed = 4.5;
                if (gdist > 3) {
                    gem.setVx((gdx / gdist) * speed);
                    gem.setVy((gdy / gdist) * speed);
                } else {
                    gem.setVx(0);
                    gem.setVy(0);
                }
            } else {
                gem.setVx(0);
                gem.setVy(0);
            }

            gem.setX(gem.getX() + gem.getVx());
            gem.setY(gem.getY() + gem.getVy());

            // Collection check with player
            if (gem.getBounds().intersects(nave.getBounds())) {
                collectedGems++;

                // Progression Rules
                if (collectedGems >= 25) {
                    nave.setLevel(4);
                    nave.setHp(Math.min(nave.getMaxHp(), nave.getHp() + 10));
                    nave.setEnergy(Math.min(nave.getMaxEnergy(), nave.getEnergy() + 10));
                } else if (collectedGems >= 15) {
                    nave.setLevel(3);
                } else if (collectedGems >= 5) {
                    nave.setLevel(2);
                }

                gemIterator.remove();
            }
        }

        // 9. Cleanup Destroyed Entities, Spawn Gems, & Check Win/Loss Conditions
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (enemy.isDestroyed()) {
                if (enemy.getEnemyType() == Enemy.Type.BOSS) {
                    onBossDefeated();
                } else {
                    int dropCount = (enemy.getEnemyType() == Enemy.Type.OFFICER) ? 8 : 4;
                    for (int i = 0; i < dropCount; i++) {
                        gems.add(new Gem(enemy.getX() + enemy.getWidth() / 2.0, enemy.getY() + enemy.getHeight() / 2.0));
                    }
                }
                enemyIterator.remove();
            }
        }

        Iterator<Asteroid> asteroidIterator = asteroids.iterator();
        while (asteroidIterator.hasNext()) {
            Asteroid ast = asteroidIterator.next();
            if (ast.isDestroyed()) {
                int dropCount = switch (ast.getSizeType()) {
                    case BIG -> 4;
                    case MID -> 2;
                    case SMALL -> 1;
                };
                for (int i = 0; i < dropCount; i++) {
                    gems.add(new Gem(ast.getX() + ast.getWidth() / 2.0, ast.getY() + ast.getHeight() / 2.0));
                }
                asteroidIterator.remove();
            }
        }

        if (nave.isDestroyed()) {
            onPlayerDefeated();
        }
    }

    public void tryShoot() {
        int level = nave.getLevel();
        int energyCost = (level == 1) ? 5 : 10;

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

        // Calculate velocity vectors based on the ship's current rotation angle
        double angleRad = Math.toRadians(nave.getAngle() - 90);
        double bulletSpeed = 8.0;
        double pvx = Math.cos(angleRad) * bulletSpeed;
        double pvy = Math.sin(angleRad) * bulletSpeed;

        // Center spawn point relative to the ship
        int px = nave.getX() + (nave.getWidth() - pWidth) / 2;
        int py = nave.getY() + (nave.getHeight() - pHeight) / 2;

        if (level == 1) {
            projectiles.add(new Projectile(px, py, pWidth, pHeight, damage, pvx, pvy, false));
        } else {
            // Multi-shot spread or parallel projectiles traveling in the aim direction
            projectiles.add(new Projectile(px - 15, py, pWidth, pHeight, damage, pvx, pvy, false));
            projectiles.add(new Projectile(px, py, pWidth, pHeight, damage, pvx, pvy, false));
            projectiles.add(new Projectile(px + 15, py, pWidth, pHeight, damage, pvx, pvy, false));
        }
    }

    public long getElapsedSeconds() {
        return (System.currentTimeMillis() - gameStartTime) / 1000;
    }
}