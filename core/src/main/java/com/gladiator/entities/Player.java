package com.gladiator.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.gladiator.decorator.BasePlayer;
import com.gladiator.decorator.PlayerDecorator;
import com.gladiator.decorator.PlayerStats;
import com.gladiator.entities.states.AttackState;
import com.gladiator.entities.states.DeadState;
import com.gladiator.entities.states.IdleState;
import com.gladiator.entities.states.PlayerState;
import com.gladiator.events.EventBus;
import com.gladiator.events.GameEvent;
import com.gladiator.managers.AssetManager;

/**
 * Player (Рыцарь) - главный персонаж, управляется игроком через WASD.
 * Фаза 3: полная реализация с движением, автоатакой и паттерном State.
 * Фаза 9: анимация спрайтов
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
    public float hp;
    public float attackTimer;
    public boolean alive;
    
    // Поля - состояние и коллизии
    public Rectangle bounds;
    private PlayerState currentState;
    
    // Поле - характеристики Рыцаря (Decorator паттерн)
    private PlayerStats stats;
    
    // Поле - для анимации (Фаза 9)
    private float stateTime = 0f;
    
    // Ресурс для рисования белого пикселя (fallback если спрайт не найден)
    private static Texture whitePixel;
    
    public Player() {
        this.x = 400 - WIDTH / 2;      // центр по X (800/2 - 24)
        this.y = 240 - HEIGHT / 2;     // центр по Y (480/2 - 32)
        this.velocityX = 0;
        this.velocityY = 0;
        this.stats = new BasePlayer();  // Базовые характеристики
        this.hp = stats.getMaxHp();
        this.attackTimer = stats.getAttackCooldown();
        this.alive = true;
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
        
        // Обновляем время для анимации
        stateTime += delta;
        
        // Читаем WASD и обновляем velocityX/Y
        velocityX = 0;
        velocityY = 0;
        float playerSpeed = stats.getSpeed();  // Берём скорость из stats
        
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            velocityY = playerSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocityY = -playerSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocityX = playerSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocityX = -playerSpeed;
        }
        
        // Нормализуем диагональное движение
        float length = (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        if (length > 0) {
            velocityX = (velocityX / length) * playerSpeed;
            velocityY = (velocityY / length) * playerSpeed;
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
            attackTimer = stats.getAttackCooldown();  // Берём кулдаун из stats
            performAttack();
            changeState(new AttackState(this));
        }
        
        // Обновляем текущее состояние
        if (currentState != null) {
            currentState.update(delta);
        }
    }

    /**
     * Рисует Player с анимацией спрайтов (Фаза 9).
     * Fallback: если спрайт не найден, рисует белый прямоугольник как раньше.
     */
    public void render(SpriteBatch batch) {
        // Выбираем анимацию по текущему состоянию
        String animKey = "player_idle";  // по умолчанию
        if (currentState instanceof IdleState) {
            animKey = "player_idle";
        } else if (currentState instanceof com.gladiator.entities.states.RunState) {
            animKey = "player_run";
        } else if (currentState instanceof AttackState) {
            animKey = "player_attack";
        } else if (currentState instanceof DeadState) {
            animKey = "player_dead";
        }
        
        // Получаем анимацию
        Animation<TextureRegion> animation = AssetManager.getInstance().getAnimation(animKey);
        
        if (animation != null) {
            // Получаем текущий кадр анимации
            TextureRegion frame = animation.getKeyFrame(stateTime, true);
            
            // Проверяем что кадр не null
            if (frame != null) {
                // Флипируем по горизонтали если идём влево
                if (velocityX < 0 && !frame.isFlipX()) {
                    frame.flip(true, false);
                } else if (velocityX > 0 && frame.isFlipX()) {
                    frame.flip(true, false);
                }
                
                // Рисуем спрайт с стандартным размером игрока
                batch.draw(frame, x, y, WIDTH, HEIGHT);
            } else {
                // Fallback если кадр null - рисуем белый квадрат
                if (whitePixel != null) {
                    batch.setColor(1, 1, 1, 1);
                    batch.draw(whitePixel, x, y, WIDTH, HEIGHT);
                    batch.setColor(1, 1, 1, 1);
                }
            }
        } else {
            // Fallback: если спрайт не найден, рисуем белый прямоугольник как раньше
            if (whitePixel != null) {
                batch.setColor(1, 1, 1, 1);
                batch.draw(whitePixel, x, y, WIDTH, HEIGHT);
                batch.setColor(1, 1, 1, 1);
            }
        }
    }

    /**
     * Получает урон и проверяет смерть.
     * Применяет снижение урона от ArmorDecorator.
     */
    public void takeDamage(float amount) {
        // Применяем снижение урона (DamageReduction от ArmorDecorator)
        float reduction = stats.getDamageReduction();
        float actualDamage = amount * (1f - reduction);
        hp -= actualDamage;
        
        // Публикуем событие урона
        EventBus.getInstance().post(
            new GameEvent(GameEvent.Type.PLAYER_HURT, actualDamage)
        );
        
        if (hp <= 0) {
            hp = 0;
            alive = false;
            
            // Публикуем событие смерти
            EventBus.getInstance().post(
                new GameEvent(GameEvent.Type.PLAYER_DIED)
            );
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
     * Сбрасывает stateTime для новой анимации.
     */
    public void changeState(PlayerState newState) {
        if (currentState != null) {
            currentState.exit();
        }
        currentState = newState;
        stateTime = 0f;  // Сбрасываем время анимации
        if (currentState != null) {
            currentState.enter();
        }
    }

    /**
     * Применяет апгрейд (Decorator) к характеристикам игрока.
     * Фаза 7: Decorator паттерн.
     */
    public void applyUpgrade(PlayerDecorator decorator) {
        int oldMaxHp = stats.getMaxHp();
        this.stats = decorator;  // Оборачиваем новым декоратором
        
        // Если новый maxHp больше старого — добавляем разницу к текущему HP
        int newMaxHp = stats.getMaxHp();
        if (newMaxHp > oldMaxHp) {
            hp += (newMaxHp - oldMaxHp);
        }
        
        // Логируем применённый апгрейд и новые характеристики
        System.out.println("Upgrade applied: " + stats.getDescription());
        System.out.println("Stats -> HP:" + stats.getMaxHp() +
                           " DMG:" + stats.getDamage() +
                           " SPD:" + stats.getSpeed() +
                           " CD:" + stats.getAttackCooldown());
    }

    /**
     * Возвращает объект с характеристиками игрока.
     */
    public PlayerStats getStats() {
        return stats;
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
