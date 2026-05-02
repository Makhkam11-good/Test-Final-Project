package com.gladiator.entities.boss;

import com.gladiator.entities.Boss;

public class DashBossState implements BossState {

    private float timer = 0f;
    private static final float DURATION = 0.6f;
    private static final float DASH_SPEED = 400f;
    private float dashDirX;
    private float dashDirY;

    @Override
    public void enter(Boss boss) {
        timer = 0f;
        dashDirX = boss.lastDirX;
        dashDirY = boss.lastDirY;
        boss.isDashing = true;
        boss.setDashHitApplied(false);
    }

    @Override
    public void update(Boss boss, float delta, float playerX, float playerY) {
        timer += delta;
        boss.velocityX = dashDirX * DASH_SPEED;
        boss.velocityY = dashDirY * DASH_SPEED;
        
        if (timer >= DURATION) {
            boss.isDashing = false;
            boss.changeState(boss.getIdleState());
        }
    }

    @Override
    public void exit(Boss boss) {
        boss.velocityX = 0;
        boss.velocityY = 0;
        boss.isDashing = false;
    }
}
