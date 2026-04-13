package com.gladiator.factories;

import com.gladiator.entities.Enemy;

/**
 * SlimeFactory - фабрика для создания Слизи (появляется с волны 1).
 */
public class SlimeFactory extends EnemyFactory {

    @Override
    public Enemy create(float x, float y) {
        Enemy enemy = new Enemy();
        enemy.x = x;
        enemy.y = y;
        enemy.maxHp = 20;
        enemy.hp = 20;
        enemy.damage = 5;
        enemy.speed = 60;
        return enemy;
    }
}
