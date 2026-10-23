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
    private int x;
    private int y;
    private int width;
    private int height;
    private int level;

    public Nave(int x, int y, int width, int height) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.level = 1;
    }
}
