package com.gladiator.entities;

/**
 * Enemy (враг) - базовый класс для Слизи, Гоблина и других врагов.
 */
public class Enemy {
    public float x, y;
    public int hp, maxHp;
    public float speed, damage;

    public Enemy() {
    }

    public void update(float delta) {
        // TODO: Реализовать в Фазе 5
    }

    public void render(Object batch) {
        // TODO: Реализовать в Фазе 5
    }

    public void takeDamage(int dmg) {
        hp -= dmg;
    }
}
