package com.gladiator.factories;

import com.gladiator.entities.Boss;
import com.gladiator.entities.Enemy;
import com.gladiator.managers.GameManager;

/**
 * BossFactory - фабрика для создания Финального Босса (Demon King на волне 10).
 * Фаза 8: полная реализация.
 */
public class BossFactory extends EnemyFactory {

    @Override
    public Enemy create(float x, float y) {
        int bossHp = GameManager.getInstance().getDifficulty().getBossHp();
        Boss boss = new Boss(x, y, bossHp);
        System.out.println("Boss created! HP: " + bossHp + " (difficulty: " +
                GameManager.getInstance().getDifficulty().getName() + ")");
        return boss;
    }
}
