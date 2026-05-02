package com.gladiator.entities;

import com.badlogic.gdx.math.MathUtils;
import com.gladiator.entities.boss.BossState;
import com.gladiator.entities.boss.ChaseBossState;
import com.gladiator.entities.boss.DashBossState;
import com.gladiator.entities.boss.IdleBossState;
import com.gladiator.events.EventBus;
import com.gladiator.events.GameEvent;
import com.gladiator.managers.AssetManager;

public class Boss extends Enemy {
    public static final float BOSS_WIDTH = 80f;
    public static final float BOSS_HEIGHT = 80f;
    private static final float DASH_DAMAGE = 40f;
    private static final float CONTACT_DAMAGE = 20f;

    public float velocityX;
    public float velocityY;
    public float lastDirX;
    public float lastDirY;
    public boolean isDashing;

    private boolean dashHitApplied;
    private float targetX;
    private float targetY;

    private final BossState idleState = new IdleBossState();
    private final BossState chaseState = new ChaseBossState();
    private final BossState dashState = new DashBossState();
    private BossState currentState = idleState;

    public Boss(float x, float y, int hp) {
        super(x, y, BOSS_WIDTH, BOSS_HEIGHT);
        setStats(hp, CONTACT_DAMAGE, 0f);
        setScoreReward(1000);
        setTypeName("Demon King");
        setRenderColor(1f, 1f, 1f);
        setTexture(AssetManager.getInstance().getTexture(AssetManager.TEX_BOSS));
        setAnimationKey(AssetManager.ANIM_BOSS);
        lastDirX = 0f;
        lastDirY = -1f;
        currentState.enter(this);
    }

    @Override
    public void update(float delta) {
        if (!isAlive()) {
            return;
        }

        float dx = targetX - getX();
        float dy = targetY - getY();
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len > 0f) {
            lastDirX = dx / len;
            lastDirY = dy / len;
        }

        currentState.update(this, delta, targetX, targetY);

        float nextX = getX() + velocityX * delta;
        float nextY = getY() + velocityY * delta;
        float clampedX = MathUtils.clamp(nextX, 0f, 800f - BOSS_WIDTH);
        float clampedY = MathUtils.clamp(nextY, 0f, 480f - BOSS_HEIGHT);

        setPosition(clampedX, clampedY);
    }

    @Override
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        super.render(batch);
    }

    @Override
    public void takeDamage(float amount) {
        boolean wasAlive = isAlive();
        super.takeDamage(amount);
        if (wasAlive && !isAlive()) {
            EventBus.getInstance().post(new GameEvent(GameEvent.Type.BOSS_DIED, this));
        }
    }

    public void changeState(BossState newState) {
        currentState.exit(this);
        currentState = newState;
        currentState.enter(this);
    }

    public BossState getIdleState() {
        return idleState;
    }

    public BossState getChaseState() {
        return chaseState;
    }

    public BossState getDashState() {
        return dashState;
    }

    public float getContactDamage() {
        return isDashing ? DASH_DAMAGE : CONTACT_DAMAGE;
    }

    public boolean isDashHitApplied() {
        return dashHitApplied;
    }

    public void setDashHitApplied(boolean dashHitApplied) {
        this.dashHitApplied = dashHitApplied;
    }

    public void setTarget(float x, float y) {
        targetX = x;
        targetY = y;
    }
}
