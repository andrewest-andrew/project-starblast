package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.model.interfaces.Collidable;

import java.awt.*;

@Getter
@Setter
@ToString
public class Gem implements Collidable {
    private double x, y;
    private int width = 24, height = 24;
    private double vx, vy;

    public Gem(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }
}