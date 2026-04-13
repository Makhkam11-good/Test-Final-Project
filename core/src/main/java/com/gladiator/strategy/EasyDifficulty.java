package com.gladiator.strategy;

/**
 * EasyDifficulty - стратегия сложности EASY (враги медленнее и слабее).
 */
public class EasyDifficulty implements DifficultyStrategy {

    @Override
    public float getEnemySpeedMult() {
        return 0.8f;
    }

    @Override
    public float getEnemyDamageMult() {
        return 0.7f;
    }

    @Override
    public int getBossHp() {
        return 300;
    }

    @Override
    public float getSpawnInterval() {
        return 2.0f;
    }
}
