package com.gladiator.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.gladiator.entities.boss.BossState;
import com.gladiator.entities.boss.IdleBossState;
import com.gladiator.events.EventBus;
import com.gladiator.events.GameEvent;
import com.gladiator.managers.AssetManager;

/**
 * Boss (Финальный Босс) - враг на волне 10, использует State паттерн для смены поведения.
 * Фаза 8: полная реализация с State паттерном.
 * Фаза 9: анимация спрайтов
 */
public class Boss extends Enemy {
    // Константы
    public static final float BOSS_WIDTH = 80f;
    public static final float BOSS_HEIGHT = 80f;
    private static final float DASH_DAMAGE = 40f;
    private static final float CONTACT_DAMAGE = 20f;
    
    // Белый пиксель для отрисовки HP бара
    private static Texture whitePixel;
    
    // State паттерн
    public BossState currentState;
    public boolean isDashing;
    public float lastDirX, lastDirY;
    
    // Скорость
    public float velocityX, velocityY;
    
    // Поле для анимации (Фаза 9)
    private float stateTime = 0f;
    
    public Boss(float x, float y, int hp) {
        super();
        this.x = x;
        this.y = y;
        this.maxHp = hp;
        this.hp = hp;
        this.alive = true;
        // Используем свои размеры через bounds - WIDTH и HEIGHT из Parent остаются теми же
        this.bounds = new Rectangle(x, y, BOSS_WIDTH, BOSS_HEIGHT);
        this.currentState = new IdleBossState();
        this.isDashing = false;
        this.velocityX = 0;
        this.velocityY = 0;
        this.lastDirX = 0;
        this.lastDirY = -1;  // Направление вниз по умолчанию
        
        // Инициализируем белый пиксель если ещё не создан
        if (whitePixel == null) {
            createWhitePixel();
        }
        
        currentState.enter(this);
    }
    
    private static void createWhitePixel() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1f, 1f, 1f, 1f);
        pixmap.drawPixel(0, 0);
        whitePixel = new Texture(pixmap);
        pixmap.dispose();
    }
    
    public void changeState(BossState newState) {
        currentState.exit(this);
        currentState = newState;
        currentState.enter(this);
    }
    
    @Override
    public void update(float delta, float playerX, float playerY) {
        if (!alive) {
            return;
        }
        
        // Обновляем время для анимации (Фаза 9)
        stateTime += delta;
        
        // Сохраняй направление для Dash
        float dx = playerX - x;
        float dy = playerY - y;
        float len = (float) Math.sqrt(dx*dx + dy*dy);
        if (len > 0) {
            lastDirX = dx/len;
            lastDirY = dy/len;
        }
        
        // Делегируй State
        currentState.update(this, delta, playerX, playerY);
        
        // Применяй скорость
        x += velocityX * delta;
        y += velocityY * delta;
        
        // Ограничь позицию в пределах арены
        x = MathUtils.clamp(x, 0, 800 - BOSS_WIDTH);
        y = MathUtils.clamp(y, 0, 480 - BOSS_HEIGHT);
        
        // Обнови bounds
        bounds.set(x, y, BOSS_WIDTH, BOSS_HEIGHT);
    }
    
    @Override
    public void render(SpriteBatch batch) {
        // Используем ShapeRenderer для рисования, это будет делаться в GameScreen
        // Этот метод может остаться пустым, так как Boss рисуется через GameScreen
    }
    
    public void renderBoss(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        if (!alive) {
            return;
        }
        
        // Получаем анимацию босса (Фаза 9)
        Animation<TextureRegion> animation = AssetManager.getInstance().getAnimation("boss_walk");
        
        if (animation != null) {
            // Получаем текущий кадр анимации
            TextureRegion frame = animation.getKeyFrame(stateTime, true);
            
            // Проверяем что кадр не null
            if (frame != null) {
                // Во время Dash рисуй с красным оттенком, иначе белый
                if (isDashing) {
                    batch.setColor(1f, 0.3f, 0.3f, 1f);
                } else {
                    batch.setColor(Color.WHITE);
                }
                
                // Рисуем спрайт с стандартным размером босса
                batch.draw(frame, x, y, BOSS_WIDTH, BOSS_HEIGHT);
                batch.setColor(Color.WHITE);
            } else {
                // Fallback если кадр null - рисуем цветной прямоугольник через batch
                if (whitePixel != null) {
                    if (isDashing) {
                        batch.setColor(1f, 0f, 0f, 1f);
                    } else {
                        batch.setColor(0.5f, 0f, 0.8f, 1f);
                    }
                    batch.draw(whitePixel, x, y, BOSS_WIDTH, BOSS_HEIGHT);
                    batch.setColor(Color.WHITE);
                }
            }
        } else {
            // Fallback: если спрайт не найден, рисуем цветной прямоугольник через batch
            if (whitePixel != null) {
                if (isDashing) {
                    batch.setColor(1f, 0f, 0f, 1f);
                } else {
                    batch.setColor(0.5f, 0f, 0.8f, 1f);
                }
                batch.draw(whitePixel, x, y, BOSS_WIDTH, BOSS_HEIGHT);
                batch.setColor(Color.WHITE);
            }
        }
        
        // Рисуй HP бар над Боссом через batch (не ShapeRenderer!)
        if (whitePixel != null) {
            float barX = x;
            float barY = y + BOSS_HEIGHT + 4;
            float barWidth = BOSS_WIDTH;
            float barHeight = 8;
            
            // Фон HP бара (серый)
            batch.setColor(0.3f, 0.3f, 0.3f, 1f);
            batch.draw(whitePixel, barX, barY, barWidth, barHeight);
            
            // Полоска HP (зелёная)
            batch.setColor(Color.GREEN);
            float hpBarWidth = barWidth * (hp / maxHp);
            batch.draw(whitePixel, barX, barY, hpBarWidth, barHeight);
            
            batch.setColor(Color.WHITE);
        }
    }
    
    @Override
    public void takeDamage(float amount) {
        if (!alive) {
            return;
        }
        hp -= amount;
        System.out.println("Boss HP: " + hp + "/" + maxHp);
        if (hp <= 0) {
            alive = false;
            // Публикуем ENEMY_DIED чтобы LevelManager узнал об окончании волны
            EventBus.getInstance().post(new GameEvent(GameEvent.Type.ENEMY_DIED));
            // Публикуем BOSS_DIED для перехода на VictoryScreen
            EventBus.getInstance().post(new GameEvent(GameEvent.Type.BOSS_DIED));
            System.out.println("BOSS DEFEATED!");
        }
    }
    
    public float getContactDamage() {
        return isDashing ? DASH_DAMAGE : CONTACT_DAMAGE;
    }
    
    public static void disposeBoss() {
        if (whitePixel != null) {
            whitePixel.dispose();
            whitePixel = null;
        }
    }
}
