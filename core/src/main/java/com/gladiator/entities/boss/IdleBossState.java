package com.gladiator.entities.boss;

import com.gladiator.entities.Boss;

public class IdleBossState implements BossState {

    private float timer = 0f;
    private static final float DURATION = 1.5f;

    @Override
    public void enter(Boss boss) {
        boss.velocityX = 0;
        boss.velocityY = 0;
        timer = 0f;
    }

    @Override
    public void update(Boss boss, float delta, float playerX, float playerY) {
        timer += delta;
        if (timer >= DURATION) {
            boss.changeState(boss.getChaseState());
        }
    }

    @Override
    public void exit(Boss boss) {
        // пусто
    }
}
