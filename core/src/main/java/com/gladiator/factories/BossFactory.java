package com.gladiator.factories;

import com.gladiator.entities.Boss;
import com.gladiator.entities.Enemy;
import com.gladiator.managers.GameManager;

public class BossFactory extends EnemyFactory {

    @Override
    public Enemy create(float x, float y) {
        int bossHp = GameManager.getInstance().getDifficulty().getBossHp();
        return new Boss(x, y, bossHp);
    }
}
