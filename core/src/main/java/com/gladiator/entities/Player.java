package com.gladiator.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.gladiator.entities.states.*;

/**
 * Player (Рыцарь) - главный персонаж, управляется игроком через WASD.
 * Фаза 3: полная реализация с движением, автоатакой и паттерном State.
 */
public class Player {
    // Константы
    private static final float SPEED = 150f;              // px/сек
    private static final float ATTACK_COOLDOWN = 1.0f;    // сек
    private static final float ATTACK_RADIUS = 80f;       // px
    private static final float WIDTH = 48f;
    private static final float HEIGHT = 64f;
    private static final int BASE_DAMAGE = 10;
    
    // Поля - позиция и скорость
    public float x, y;
    public float velocityX, velocityY;
    
    // Поля - здоровье и атака
    public float hp, maxHp;
    public float attackTimer;
    public boolean alive;
    public int damage;
    
    // Поля - состояние и коллизии
    public Rectangle bounds;
    private PlayerState currentState;
    
    // Ресурс для рисования белого пикселя
    private static Texture whitePixel;
    
    public Player() {
        this.x = 400 - WIDTH / 2;      // центр по X (800/2 - 24)
        this.y = 240 - HEIGHT / 2;     // центр по Y (480/2 - 32)
        this.velocityX = 0;
        this.velocityY = 0;
        this.maxHp = 100;
        this.hp = maxHp;
        this.attackTimer = ATTACK_COOLDOWN;
        this.alive = true;
        this.damage = BASE_DAMAGE;
        this.bounds = new Rectangle(x, y, WIDTH, HEIGHT);
        this.currentState = new IdleState(this);
        this.currentState.enter();
        
        // Создаём белый пиксель для рисования, если его нет
        if (whitePixel == null) {
            createWhitePixel();
        }
    }
    
    private static void createWhitePixel() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();
    }

    /**
     * Основной цикл обновления Player.
     * Читает WASD, обновляет позицию, таймер атаки и состояние.
     */
    public void update(float delta) {
        if (!alive) {
            changeState(new DeadState(this));
            return;
        }
        
        // Читаем WASD и обновляем velocityX/Y
        velocityX = 0;
        velocityY = 0;
        
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            velocityY = SPEED;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocityY = -SPEED;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocityX = SPEED;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocityX = -SPEED;
        }
        
        // Нормализуем диагональное движение
        float length = (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        if (length > 0) {
            velocityX = (velocityX / length) * SPEED;
            velocityY = (velocityY / length) * SPEED;
        }
        
        // Применяем скорость к позиции
        x += velocityX * delta;
        y += velocityY * delta;
        
        // Ограничиваем позицию в пределах экрана (800×480)
        x = MathUtils.clamp(x, 0, 800 - WIDTH);
        y = MathUtils.clamp(y, 0, 480 - HEIGHT);
        
        // Обновляем bounds
        bounds.set(x, y, WIDTH, HEIGHT);
        
        // Обновляем таймер атаки
        attackTimer -= delta;
        if (attackTimer <= 0) {
            attackTimer = ATTACK_COOLDOWN;
            performAttack();
            changeState(new AttackState(this));
        }
        
        // Обновляем текущее состояние
        if (currentState != null) {
            currentState.update(delta);
        }
    }

    /**
     * Рисует Player как белый прямоугольник.
     */
    public void render(SpriteBatch batch) {
        if (whitePixel != null) {
            batch.setColor(1, 1, 1, 1);
            batch.draw(whitePixel, x, y, WIDTH, HEIGHT);
            batch.setColor(1, 1, 1, 1);
        }
    }

    /**
     * Получает урон и проверяет смерть.
     */
    public void takeDamage(float amount) {
        hp -= amount;
        if (hp <= 0) {
            hp = 0;
            alive = false;
        }
    }

    /**
     * Выполняет автоатаку (пока просто лог).
     */
    public void performAttack() {
        System.out.println("Player attacks!");
    }

    /**
     * Проверяет, жив ли игрок.
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Проверяет, атакует ли игрок (находится в AttackState).
     */
    public boolean isAttacking() {
        return currentState instanceof AttackState;
    }

    /**
     * Переключает состояние Player.
     */
    public void changeState(PlayerState newState) {
        if (currentState != null) {
            currentState.exit();
        }
        currentState = newState;
        if (currentState != null) {
            currentState.enter();
        }
    }
    
    /**
     * Очищает ресурсы.
     */
    public static void dispose() {
        if (whitePixel != null) {
            whitePixel.dispose();
            whitePixel = null;
        }
    }
}
