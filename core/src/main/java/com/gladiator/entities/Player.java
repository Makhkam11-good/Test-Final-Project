package com.gladiator.entities;

/**
 * Player (Рыцарь) - главный персонаж, управляется игроком через WASD.
 */
public class Player {
    public float x, y;
    public int hp, maxHp;
    public float speed, attackCooldown;

    public Player() {
        this.maxHp = 100;
        this.hp = maxHp;
        this.speed = 150;
        this.attackCooldown = 1.0f;
    }

    public void update(float delta) {
        // TODO: Реализовать в Фазе 3
    }

    public void render(Object batch) {
        // TODO: Реализовать в Фазе 3
    }
}
