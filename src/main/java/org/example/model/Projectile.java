package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Projectile {
    private int x, y;
    private int width, height;
    private int damage;

    public Projectile(int x, int y, int width, int height, int damage) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.damage = damage;
    }
}