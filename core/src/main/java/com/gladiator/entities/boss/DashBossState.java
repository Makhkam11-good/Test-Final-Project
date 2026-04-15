package com.gladiator.entities.boss;

import com.gladiator.entities.Boss;

/**
 * DashBossState - Босс делает рывок 400 px/sec в направлении игрока (0.6 сек), потом в Idle.
 */
public class DashBossState implements BossState {
    
    private float timer = 0f;
    private static final float DURATION = 0.6f;
    private static final float DASH_SPEED = 400f;
    private float dashDirX;
    private float dashDirY;

    @Override
    public void enter(Boss boss) {
        timer = 0f;
        System.out.println("Boss: DASH");
        // Фиксируем направление рывка в момент начала
        dashDirX = boss.lastDirX;
        dashDirY = boss.lastDirY;
        boss.isDashing = true;
    }

    @Override
    public void update(Boss boss, float delta, float playerX, float playerY) {
        timer += delta;
        boss.velocityX = dashDirX * DASH_SPEED;
        boss.velocityY = dashDirY * DASH_SPEED;
        
        if (timer >= DURATION) {
            boss.isDashing = false;
            boss.changeState(new IdleBossState());
        }
    }

    @Override
    public void exit(Boss boss) {
        boss.velocityX = 0;
        boss.velocityY = 0;
        boss.isDashing = false;
    }
}
