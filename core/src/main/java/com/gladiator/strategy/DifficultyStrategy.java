package com.gladiator.strategy;

/**
 * DifficultyStrategy (Strategy паттерн) - интерфейс для стратегии сложности.
 */
public interface DifficultyStrategy {
    float getEnemySpeedMult();
    float getEnemyDamageMult();
    int getBossHp();
    float getSpawnInterval();
    String getName();
}
