package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class Nave {
    private String id;
    private int x, y;
    private int width, height;
    private int level;

    // Ship Stats
    private double hp;
    private double maxHp;
    private double energy;
    private double maxEnergy;
    private int speed;

    public Nave(int x, int y, int width, int height) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.level = 1; // Start at Level 1
        applyLevelStats();

        // Start full on HP and Energy
        this.hp = this.maxHp;
        this.energy = this.maxEnergy;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, Math.min(level, 4));
        applyLevelStats();
    }

    private void applyLevelStats() {
        switch (level) {
            case 1:
                this.speed = 3;
                this.maxHp = 100;
                this.maxEnergy = 100;
                break;
            case 2:
                this.speed = 4;
                this.maxHp = 150;
                this.maxEnergy = 150;
                break;
            case 3:
                this.speed = 4;
                this.maxHp = 200;
                this.maxEnergy = 200;
                break;
            case 4:
                this.speed = 5;
                this.maxHp = 250;
                this.maxEnergy = 250;
                break;
        }
        // Cap current values to new maxes if upgrading
        this.hp = Math.min(this.hp, this.maxHp);
        this.energy = Math.min(this.energy, this.maxEnergy);
    }
}