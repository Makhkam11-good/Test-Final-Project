package com.gladiator.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.gladiator.decorator.BasePlayerStats;
import com.gladiator.decorator.DamageModifier;
import com.gladiator.decorator.PlayerDecorator;
import com.gladiator.decorator.PlayerStats;
import com.gladiator.entities.states.AttackState;
import com.gladiator.entities.states.DeadState;
import com.gladiator.entities.states.IdleState;
import com.gladiator.entities.states.PlayerState;
import com.gladiator.entities.states.RunState;
import com.gladiator.events.EventBus;
import com.gladiator.events.GameEvent;
import com.gladiator.managers.AssetManager;
import com.gladiator.managers.GameManager;

public class Player implements Renderable, Updatable {
    public static final float WIDTH = 48f;
    public static final float HEIGHT = 64f;
    public static final float ATTACK_RADIUS = 80f;
    private static final float ATTACK_DURATION = 0.3f;

    private float x;
    private float y;
    private float velocityX;
    private float velocityY;
    private float moveDirX;
    private float moveDirY;

    private float hp;
    private boolean alive;
    private boolean attacking;
    private boolean attackTriggered;
    private float attackCooldownTimer;
    private boolean attackReady;
    private float damageTakenMultiplier;
    private float stateTime;
    private float damageFlashTimer;
    private float facingX;
    private float facingY;

    private final Rectangle bounds;
    private PlayerState currentState;
    private final IdleState idleState;
    private final RunState runState;
    private final AttackState attackState;
    private final DeadState deadState;

    private PlayerStats stats;

    public Player() {
        x = 400f - WIDTH / 2f;
        y = 240f - HEIGHT / 2f;
        moveDirX = 0f;
        moveDirY = 0f;
        stats = new BasePlayerStats();
        hp = stats.getMaxHp();
        alive = true;
        attacking = false;
        attackTriggered = false;
        attackCooldownTimer = stats.getAttackCooldown();
        attackReady = false;
        damageTakenMultiplier = 1f;
        stateTime = 0f;
        damageFlashTimer = 0f;
        facingX = 1f;
        facingY = 0f;
        bounds = new Rectangle(x, y, WIDTH, HEIGHT);
        idleState = new IdleState(this);
        runState = new RunState(this);
        attackState = new AttackState(this);
        deadState = new DeadState(this);
        currentState = idleState;
        currentState.enter();
    }

    @Override
    public void update(float delta) {
        if (!alive) {
            changeState(deadState);
            return;
        }

        stateTime += delta;
        damageFlashTimer = Math.max(0f, damageFlashTimer - delta);
        attackCooldownTimer -= delta;
        if (attackCooldownTimer <= 0f) {
            attackCooldownTimer = stats.getAttackCooldown();
            attackReady = true;
        }

        float speed = stats.getSpeed();
        velocityX = moveDirX * speed;
        velocityY = moveDirY * speed;

        if (moveDirX != 0f || moveDirY != 0f) {
            facingX = moveDirX;
            facingY = moveDirY;
        }

        x += velocityX * delta;
        y += velocityY * delta;

        x = MathUtils.clamp(x, 0f, 800f - WIDTH);
        y = MathUtils.clamp(y, 0f, 480f - HEIGHT);
        bounds.set(x, y, WIDTH, HEIGHT);

        if (currentState != null) {
            currentState.update(delta);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        String animationKey = AssetManager.ANIM_PLAYER_IDLE;
        float width = WIDTH;
        float height = HEIGHT;
        float drawX = x;
        float drawY = y;
        float frameDuration = 0.12f;

        if (!alive) {
            animationKey = AssetManager.ANIM_PLAYER_DEAD;
            width = 64f;
            height = 40f;
            drawX = x - 8f;
            drawY = y + 8f;
        } else if (attacking) {
            animationKey = AssetManager.ANIM_PLAYER_ATTACK;
            width = 64f;
            height = 64f;
            drawX = x - 8f;
            frameDuration = 0.07f;
        } else if (isMoving()) {
            animationKey = AssetManager.ANIM_PLAYER_RUN;
            frameDuration = 0.08f;
            drawY += MathUtils.sin(stateTime * 18f) * 1.6f;
        } else {
            drawY += MathUtils.sin(stateTime * 5f) * 0.7f;
        }

        TextureRegion frame = AssetManager.getInstance().getAnimationFrame(animationKey, stateTime, frameDuration);
        Texture pixel = AssetManager.getInstance().getPixel();
        if (damageFlashTimer > 0f) {
            batch.setColor(1f, 0.45f, 0.35f, 1f);
        }
        if (frame != null) {
            float scaleX = facingX < -0.05f ? -1f : 1f;
            float renderX = scaleX < 0f ? drawX + width : drawX;
            batch.draw(frame, renderX, drawY, width / 2f, height / 2f, width, height, scaleX, 1f, 0f);
        } else if (pixel != null) {
            batch.draw(pixel, x, y, WIDTH, HEIGHT);
        }
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void takeDamage(float amount, String cause) {
        if (!alive) {
            return;
        }
        float actualDamage = amount * damageTakenMultiplier;
        hp -= actualDamage;
        damageFlashTimer = 0.16f;
        EventBus.getInstance().post(new GameEvent(GameEvent.Type.PLAYER_HURT, actualDamage));
        if (hp <= 0f) {
            hp = 0f;
            alive = false;
            GameManager.getInstance().setLastDeathCause(cause);
            EventBus.getInstance().post(new GameEvent(GameEvent.Type.PLAYER_DIED));
        }
    }

    public void performAttack() {
        attackTriggered = true;
        changeState(attackState);
        EventBus.getInstance().post(new GameEvent(GameEvent.Type.PLAYER_ATTACK));
    }

    public boolean consumeAttackTriggered() {
        if (attackTriggered) {
            attackTriggered = false;
            return true;
        }
        return false;
    }

    public boolean consumeAttackReady() {
        if (attackReady) {
            attackReady = false;
            return true;
        }
        return false;
    }


    public boolean isAlive() {
        return alive;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public void changeState(PlayerState newState) {
        if (currentState != null) {
            currentState.exit();
        }
        currentState = newState;
        if (currentState != null) {
            currentState.enter();
        }
    }

    public void applyUpgrade(PlayerDecorator decorator) {
        int oldMaxHp = stats.getMaxHp();
        stats = decorator;
        int newMaxHp = stats.getMaxHp();
        if (newMaxHp > oldMaxHp) {
            hp += (newMaxHp - oldMaxHp);
        }
        if (decorator instanceof DamageModifier) {
            damageTakenMultiplier *= ((DamageModifier) decorator).getDamageMultiplier();
        }
        attackCooldownTimer = Math.min(attackCooldownTimer, stats.getAttackCooldown());
        GameManager.getInstance().addUpgrade();
        EventBus.getInstance().post(new GameEvent(GameEvent.Type.UPGRADE_SELECTED, decorator.getLabel()));
    }

    public PlayerStats getStats() {
        return stats;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getCenterX() {
        return x + WIDTH / 2f;
    }

    public float getCenterY() {
        return y + HEIGHT / 2f;
    }

    public float getHp() {
        return hp;
    }

    public void setMoveDirection(float x, float y) {
        moveDirX = x;
        moveDirY = y;
    }

    public float getMoveDirX() {
        return moveDirX;
    }

    public float getMoveDirY() {
        return moveDirY;
    }

    public float getFacingX() {
        return facingX;
    }

    public float getFacingY() {
        return facingY;
    }

    public boolean isMoving() {
        return moveDirX != 0f || moveDirY != 0f;
    }

    public float getAttackCooldownProgress() {
        float cooldown = stats.getAttackCooldown();
        if (cooldown <= 0f) {
            return 1f;
        }
        return 1f - MathUtils.clamp(attackCooldownTimer / cooldown, 0f, 1f);
    }

    public float getAttackDuration() {
        return ATTACK_DURATION;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public IdleState getIdleState() {
        return idleState;
    }

    public RunState getRunState() {
        return runState;
    }

    public AttackState getAttackState() {
        return attackState;
    }

    public DeadState getDeadState() {
        return deadState;
    }
}
