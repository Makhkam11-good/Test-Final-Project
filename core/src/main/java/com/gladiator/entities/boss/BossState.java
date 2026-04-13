package com.gladiator.entities.boss;

/**
 * BossState (State паттерн) - интерфейс для состояний Босса (Demon King).
 */
public interface BossState {
    void enter();
    void update(float delta, float playerX, float playerY);
    void exit();
}
