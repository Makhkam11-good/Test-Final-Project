package com.gladiator.ai;

import com.gladiator.entities.Enemy;

public class ArcherAI implements EnemyAI {
    private static final float MIN_RANGE = 120f;
    private static final float MAX_RANGE = 200f;

    @Override
    public void update(Enemy enemy, float delta, float playerX, float playerY) {
        float dx = playerX - enemy.getX();
        float dy = playerY - enemy.getY();
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist < 0.001f) {
            enemy.setMoveDirection(0f, 0f);
            return;
        }

        if (dist < MIN_RANGE) {
            enemy.setMoveDirection(-dx / dist, -dy / dist);
        } else if (dist > MAX_RANGE) {
            enemy.setMoveDirection(dx / dist, dy / dist);
        } else {
            enemy.setMoveDirection(0f, 0f);
        }
    }
}
