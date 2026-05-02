package com.gladiator.ai;

import com.gladiator.entities.Enemy;

public interface EnemyAI {
    void update(Enemy enemy, float delta, float playerX, float playerY);
}
