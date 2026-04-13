package com.gladiator.factories;

import com.badlogic.gdx.math.Rectangle;
import com.gladiator.entities.Enemy;

/**
 * GoblinFactory - фабрика для создания Гоблинов.
 * Гоблин: HP=40, damage=12, speed=100, reward=25.
 */
public class GoblinFactory extends EnemyFactory {

    @Override
    public Enemy create(float x, float y) {
        Enemy enemy = new Enemy();
        enemy.x = x;
        enemy.y = y;
        enemy.maxHp = 40f;
        enemy.hp = 40f;
        enemy.damage = 12f;
        enemy.speed = 100f;
        enemy.scoreReward = 25;
        enemy.alive = true;
        enemy.bounds = new Rectangle(x, y, Enemy.WIDTH, Enemy.HEIGHT);
        return enemy;
    }
}
