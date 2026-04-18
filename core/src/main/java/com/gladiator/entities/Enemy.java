package com.gladiator.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.gladiator.events.EventBus;
import com.gladiator.events.GameEvent;
import com.gladiator.managers.AssetManager;

/**
 * Enemy - базовый класс врага (Слизь, Гоблин).
 * Враги движутся к игроку и наносят урон при контакте.
 * Фаза 5: реализация с движением и коллизией.
 * Фаза 9: анимация спрайтов
 */
public class Enemy {
    // Размеры
    public static final float WIDTH = 40f;
    public static final float HEIGHT = 40f;
    
    // Позиция и движение
    public float x, y;
    public float speed;
    
    // Здоровье
    public float hp, maxHp;
    public float damage;  // урон в секунду
    
    // Состояние и награда
    public boolean alive;
    public int scoreReward;
    
    // Хитбокс
    public Rectangle bounds;
    
    // Поля для анимации (Фаза 9)
    public String animKey = "slime_walk";  // Ключ анимации, устанавливается фабриками
    private float stateTime = 0f;
    
    // Ресурс для рисования красного пикселя (fallback если спрайт не найден)
    private static com.badlogic.gdx.graphics.Texture redPixel;
    
    public Enemy() {
        this.bounds = new Rectangle(x, y, WIDTH, HEIGHT);
        this.alive = true;
        
        // Создаём красный пиксель для рисования, если его нет
        if (redPixel == null) {
            createRedPixel();
        }
    }
    
    private static void createRedPixel() {
        com.badlogic.gdx.graphics.Pixmap pixmap = 
            new com.badlogic.gdx.graphics.Pixmap(1, 1, 
                com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 0, 0, 1);  // Красный
        pixmap.fill();
        redPixel = new com.badlogic.gdx.graphics.Texture(pixmap);
        pixmap.dispose();
    }
    
    /**
     * Обновляет позицию врага, направляя его к игроку.
     * PHASE 11: Враги не удаляются за экран, они остаются в игре пока не убиты.
     */
    public void update(float delta, float playerX, float playerY) {
        if (!alive) {
            return;
        }
        
        // Обновляем время для анимации
        stateTime += delta;
        
        // Вычисляем вектор к игроку
        float dx = playerX - x;
        float dy = playerY - y;
        
        // Нормализуем вектор
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len > 0) {
            dx /= len;
            dy /= len;
        }
        
        // Перемещаем врага
        x += dx * speed * delta;
        y += dy * speed * delta;
        
        // Обновляем bounds
        bounds.set(x, y, WIDTH, HEIGHT);
        
        // PHASE 11: Враг НЕ удаляется если ушел за экран
        // Враг остается в игре пока не убит вручную игроком
    }
    
    /**
     * Рисует врага с анимацией спрайтов (Фаза 9).
     * Fallback: если спрайт не найден, рисует красный квадрат как раньше.
     */
    public void render(SpriteBatch batch) {
        if (!alive) {
            return;
        }
        
        // Получаем анимацию по ключу
        Animation<TextureRegion> animation = AssetManager.getInstance().getAnimation(animKey);
        
        if (animation != null) {
            // Получаем текущий кадр анимации
            TextureRegion frame = animation.getKeyFrame(stateTime, true);
            
            // Проверяем что кадр не null
            if (frame != null) {
                // Флипируем если враг движется влево
                // (можно определить по velocityX если надо, но пока используем простую логику)
                
                // Рисуем спрайт с стандартным размером врага
                batch.draw(frame, x, y, WIDTH, HEIGHT);
            } else {
                // Fallback если кадр null - рисуем красный квадрат
                if (redPixel != null) {
                    batch.setColor(Color.RED);
                    batch.draw(redPixel, x, y, WIDTH, HEIGHT);
                    batch.setColor(Color.WHITE);
                }
            }
        } else {
            // Fallback: если спрайт не найден, рисуем красный квадрат как раньше
            if (redPixel != null) {
                batch.setColor(Color.RED);
                batch.draw(redPixel, x, y, WIDTH, HEIGHT);
                batch.setColor(Color.WHITE);
            }
        }
    }
    
    /**
     * Получает урон.
     * PHASE 11: Добавление очков при смерти врага
     */
    public void takeDamage(float amount) {
        hp -= amount;
        
        if (hp <= 0 && alive) {
            alive = false;
            System.out.println("Enemy died! Score: " + scoreReward);
            
            // Добавляем очки когда враг умирает
            com.gladiator.managers.GameManager.getInstance().addScore(scoreReward);
            
            // Публикуем событие смерти врага
            EventBus.getInstance().post(
                new GameEvent(GameEvent.Type.ENEMY_DIED, this)
            );
        }
    }
    
    /**
     * Проверяет жив ли враг.
     */
    public boolean isAlive() {
        return alive;
    }
    
    /**
     * Очищает ресурсы.
     */
    public static void dispose() {
        if (redPixel != null) {
            redPixel.dispose();
            redPixel = null;
        }
    }
}
