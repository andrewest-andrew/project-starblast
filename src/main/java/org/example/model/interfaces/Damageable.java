package org.example.model.interfaces;

public interface Damageable {
    double getHp();
    void takeDamage(double amount);
    boolean isDestroyed();
}