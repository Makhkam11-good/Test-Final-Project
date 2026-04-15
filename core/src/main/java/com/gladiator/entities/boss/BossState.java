package com.gladiator.entities.boss;

import com.gladiator.entities.Boss;

/**
 * BossState (State паттерн) - интерфейс для состояний Босса (Demon King).
 */
public interface BossState {
    void enter(Boss boss);
    void update(Boss boss, float delta, float playerX, float playerY);
    void exit(Boss boss);
}
