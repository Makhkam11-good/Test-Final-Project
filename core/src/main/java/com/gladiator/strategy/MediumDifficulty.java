package com.gladiator.strategy;

/**
 * MediumDifficulty - стратегия сложности MEDIUM (базовые враги).
 */
public class MediumDifficulty implements DifficultyStrategy {

    @Override
    public float getEnemySpeedMult() {
        return 1.0f;
    }

    @Override
    public float getEnemyDamageMult() {
        return 1.0f;
    }

    @Override
    public int getBossHp() {
        return 500;
    }

    @Override
    public float getSpawnInterval() {
        return 1.5f;
    }
}
