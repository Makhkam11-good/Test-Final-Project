package com.gladiator.factories;

import com.gladiator.entities.Enemy;

/**
 * GoblinFactory - фабрика для создания Гоблина (появляется с волны 3).
 */
public class GoblinFactory extends EnemyFactory {

    @Override
    public Enemy create(float x, float y) {
        Enemy enemy = new Enemy();
        enemy.x = x;
        enemy.y = y;
        enemy.maxHp = 40;
        enemy.hp = 40;
        enemy.damage = 12;
        enemy.speed = 100;
        return enemy;
    }
}
