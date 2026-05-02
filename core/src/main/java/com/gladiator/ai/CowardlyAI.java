package com.gladiator.ai;

import com.gladiator.entities.Enemy;

public class CowardlyAI implements EnemyAI {
    @Override
    public void update(Enemy enemy, float delta, float playerX, float playerY) {
        float dx = enemy.getX() - playerX;
        float dy = enemy.getY() - playerY;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len == 0f) {
            enemy.setMoveDirection(0f, 0f);
            return;
        }
        enemy.setMoveDirection(dx / len, dy / len);
    }
}
