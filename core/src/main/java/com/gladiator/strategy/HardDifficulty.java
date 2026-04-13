package com.gladiator.strategy;

/**
 * HardDifficulty - стратегия сложности HARD (враги быстрые и сильные).
 */
public class HardDifficulty implements DifficultyStrategy {

    @Override
    public float getEnemySpeedMult() {
        return 1.3f;
    }

    @Override
    public float getEnemyDamageMult() {
        return 1.5f;
    }

    @Override
    public int getBossHp() {
        return 1000;
    }

    @Override
    public float getSpawnInterval() {
        return 1.0f;
    }
}
