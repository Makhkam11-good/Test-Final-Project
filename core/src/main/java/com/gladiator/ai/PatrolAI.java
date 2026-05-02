package com.gladiator.ai;

import com.badlogic.gdx.math.MathUtils;
import com.gladiator.entities.Enemy;

public class PatrolAI implements EnemyAI {
    private float dirX;
    private float dirY;
    private float changeTimer;

    public PatrolAI() {
        pickNewDirection();
    }

    @Override
    public void update(Enemy enemy, float delta, float playerX, float playerY) {
        changeTimer -= delta;
        if (changeTimer <= 0f) {
            pickNewDirection();
        }

        // Nudge away from edges to keep patrol inside the arena.
        if (enemy.getX() < 10f) dirX = Math.abs(dirX);
        if (enemy.getX() > 760f) dirX = -Math.abs(dirX);
        if (enemy.getY() < 10f) dirY = Math.abs(dirY);
        if (enemy.getY() > 440f) dirY = -Math.abs(dirY);

        enemy.setMoveDirection(dirX, dirY);
    }

    private void pickNewDirection() {
        dirX = MathUtils.random(-1f, 1f);
        dirY = MathUtils.random(-1f, 1f);
        float len = (float) Math.sqrt(dirX * dirX + dirY * dirY);
        if (len == 0f) {
            dirX = 1f;
            dirY = 0f;
            len = 1f;
        }
        dirX /= len;
        dirY /= len;
        changeTimer = MathUtils.random(0.5f, 1.5f);
    }
}
