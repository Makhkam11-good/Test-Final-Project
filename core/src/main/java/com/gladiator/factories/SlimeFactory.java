package com.gladiator.factories;

import com.badlogic.gdx.math.Rectangle;
import com.gladiator.entities.Enemy;

/**
 * SlimeFactory - фабрика для создания Слизи.
 * Слизь: HP=20, damage=5, speed=60, reward=10.
 */
public class SlimeFactory extends EnemyFactory {

    @Override
    public Enemy create(float x, float y) {
        Enemy enemy = new Enemy();
        enemy.x = x;
        enemy.y = y;
        enemy.maxHp = 20f;
        enemy.hp = 20f;
        enemy.damage = 5f;
        enemy.speed = 60f;
        enemy.scoreReward = 10;
        enemy.alive = true;
        enemy.bounds = new Rectangle(x, y, Enemy.WIDTH, Enemy.HEIGHT);
        return enemy;
    }
}
