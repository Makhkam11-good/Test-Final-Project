package com.gladiator.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.gladiator.ai.EnemyAI;
import com.gladiator.events.EventBus;
import com.gladiator.events.GameEvent;
import com.gladiator.managers.AssetManager;
import com.gladiator.managers.GameManager;

public class Enemy implements Renderable, Updatable {
    private float x;
    private float y;
    private float width;
    private float height;
    private float speed;
    private float hp;
    private float maxHp;
    private float damage;
    private boolean alive;
    private int scoreReward;
    private String typeName;

    private float targetX;
    private float targetY;
    private float moveDirX;
    private float moveDirY;

    private final Rectangle bounds;
    private Texture texture;
    private String animationKey;
    private float colorR;
    private float colorG;
    private float colorB;
    private float stateTime;
    private float damageFlashTimer;
    private float spawnAge;

    private EnemyAI ai;
    private EnemyAI patrolAi;
    private EnemyAI aggressiveAi;
    private float aggroRange;

    private boolean ranged;
    private float rangedCooldown;
    private float rangedTimer;
    private float projectileDamage;
    private float projectileSpeed;
    private float projectileRange;

    public Enemy(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle(x, y, width, height);
        this.alive = true;
        this.colorR = 1f;
        this.colorG = 1f;
        this.colorB = 1f;
        this.stateTime = 0f;
        this.damageFlashTimer = 0f;
        this.spawnAge = 0f;
        this.ranged = false;
    }

    @Override
    public void update(float delta) {
        if (!alive) {
            return;
        }

        stateTime += delta;
        spawnAge += delta;
        damageFlashTimer = Math.max(0f, damageFlashTimer - delta);
        if (rangedTimer > 0f) {
            rangedTimer -= delta;
        }

        if (patrolAi != null && aggressiveAi != null) {
            float dx = targetX - x;
            float dy = targetY - y;
            if ((dx * dx + dy * dy) <= (aggroRange * aggroRange)) {
                ai = aggressiveAi;
            }
        }

        if (ai != null) {
            ai.update(this, delta, targetX, targetY);
        }

        x += moveDirX * speed * delta;
        y += moveDirY * speed * delta;

        x = MathUtils.clamp(x, 0f, 800f - width);
        y = MathUtils.clamp(y, 0f, 480f - height);
        bounds.set(x, y, width, height);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!alive) {
            return;
        }

        Texture pixel = AssetManager.getInstance().getPixel();
        TextureRegion frame = animationKey == null
            ? null
            : AssetManager.getInstance().getAnimationFrame(animationKey, stateTime, 0.12f);
        float bob = MathUtils.sin(stateTime * 8f) * 1.1f;
        float spawnScale = MathUtils.clamp(spawnAge / 0.25f, 0.1f, 1f);
        if (damageFlashTimer > 0f) {
            batch.setColor(1f, 0.35f, 0.25f, 1f);
        } else {
            batch.setColor(colorR, colorG, colorB, 1f);
        }
        if (frame != null) {
            batch.draw(frame, x + width * (1f - spawnScale) / 2f, y + bob,
                width / 2f, height / 2f, width, height, spawnScale, spawnScale, 0f);
        } else if (texture != null) {
            batch.draw(texture, x, y, width, height);
        } else if (pixel != null) {
            batch.draw(pixel, x, y, width, height);
        }
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void takeDamage(float amount) {
        if (!alive) {
            return;
        }
        hp -= amount;
        damageFlashTimer = 0.14f;
        EventBus.getInstance().post(new GameEvent(GameEvent.Type.ENEMY_HURT, this));
        if (hp <= 0f) {
            alive = false;
            GameManager.getInstance().addScore(scoreReward);
            GameManager.getInstance().addEnemyKill();
            EventBus.getInstance().post(new GameEvent(GameEvent.Type.ENEMY_DIED, this));
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getDamage() {
        return damage;
    }

    public float getSpeed() {
        return speed;
    }

    public String getTypeName() {
        return typeName;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getCenterX() {
        return x + width / 2f;
    }

    public float getCenterY() {
        return y + height / 2f;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getHp() {
        return hp;
    }

    public float getMaxHp() {
        return maxHp;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        bounds.set(x, y, width, height);
    }

    public void setTarget(float x, float y) {
        targetX = x;
        targetY = y;
    }

    public void setMoveDirection(float x, float y) {
        moveDirX = x;
        moveDirY = y;
    }

    public void setStats(float hp, float damage, float speed) {
        this.hp = hp;
        this.maxHp = hp;
        this.damage = damage;
        this.speed = speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setScoreReward(int scoreReward) {
        this.scoreReward = scoreReward;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setAnimationKey(String animationKey) {
        this.animationKey = animationKey;
    }

    public void setRenderColor(float r, float g, float b) {
        colorR = r;
        colorG = g;
        colorB = b;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setAi(EnemyAI ai) {
        this.ai = ai;
        this.patrolAi = null;
        this.aggressiveAi = null;
    }

    public void setPatrolAggroAi(EnemyAI patrolAi, EnemyAI aggressiveAi, float aggroRange) {
        this.patrolAi = patrolAi;
        this.aggressiveAi = aggressiveAi;
        this.aggroRange = aggroRange;
        this.ai = patrolAi;
    }

    public void configureRanged(float cooldown, float damage, float speed, float range) {
        ranged = true;
        rangedCooldown = cooldown;
        rangedTimer = MathUtils.random(0.25f, cooldown);
        projectileDamage = damage;
        projectileSpeed = speed;
        projectileRange = range;
    }

    public boolean consumeRangedAttackReady(float playerX, float playerY) {
        if (!ranged || rangedTimer > 0f) {
            return false;
        }
        float dx = playerX - getCenterX();
        float dy = playerY - getCenterY();
        if ((dx * dx + dy * dy) > projectileRange * projectileRange) {
            return false;
        }
        rangedTimer = rangedCooldown;
        return true;
    }

    public float getProjectileDamage() {
        return projectileDamage;
    }

    public float getProjectileSpeed() {
        return projectileSpeed;
    }
}
