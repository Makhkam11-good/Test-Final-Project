package com.gladiator.factories;

import com.gladiator.entities.Boss;
import com.gladiator.entities.Enemy;

/**
 * BossFactory - фабрика для создания Финального Босса (Demon King на волне 10).
 */
public class BossFactory extends EnemyFactory {

    @Override
    public Enemy create(float x, float y) {
        Boss boss = new Boss();
        boss.x = x;
        boss.y = y;
        boss.maxHp = 500; // TODO: будет меняться от сложности в Фазе 6
        boss.hp = 500;
        boss.damage = 20;
        boss.speed = 0; // Босс не двигается сам, его движет State
        return boss;
    }
}
