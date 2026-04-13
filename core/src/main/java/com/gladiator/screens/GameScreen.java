package com.gladiator.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gladiator.entities.Enemy;
import com.gladiator.entities.Player;
import com.gladiator.events.EventBus;
import com.gladiator.events.GameEvent;
import com.gladiator.factories.EnemyFactory;
import com.gladiator.factories.GoblinFactory;
import com.gladiator.factories.SlimeFactory;
import com.gladiator.managers.GameStateManager;
import com.gladiator.managers.LevelManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * GameScreen - основной экран игры, где происходит вся игровая логика.
 * Фаза 5: добавлены враги, спавн, коллизии и рендеринг.
 */
public class GameScreen implements Screen {
    
    private GameStateManager gsm;
    private SpriteBatch batch;
    private BitmapFont font;
    private Player player;
    private LevelManager levelManager;
    
    // Враги
    private List<Enemy> enemies;
    private int currentWave;
    private int score;
    
    public GameScreen(GameStateManager gsm) {
        this.gsm = gsm;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        
        // Создаём Player в центре экрана
        player = new Player();
        
        // Создаём LevelManager
        levelManager = new LevelManager();
        
        // Инициализируем враги
        enemies = new ArrayList<>();
        currentWave = 1;
        score = 0;
        
        // Спавним первую волну
        spawnWave(currentWave);
        
        // Подписываемся на событие завершения волны
        EventBus.getInstance().subscribe(
            GameEvent.Type.WAVE_CLEARED,
            event -> onWaveCleared()
        );
        
        // Подписываемся на событие смерти игрока
        EventBus.getInstance().subscribe(
            GameEvent.Type.PLAYER_DIED,
            event -> gsm.set(GameStateManager.State.GAME_OVER)
        );
    }

    /**
     * Спавнит волну врагов.
     */
    private void spawnWave(int wave) {
        enemies.clear();
        
        List<EnemyFactory> waveFactories = new ArrayList<>();
        
        // Определяем состав врагов для каждой волны
        switch (wave) {
            case 1:
                // 4 Слизи
                for (int i = 0; i < 4; i++) {
                    waveFactories.add(new SlimeFactory());
                }
                break;
            case 2:
                // 6 Слизей
                for (int i = 0; i < 6; i++) {
                    waveFactories.add(new SlimeFactory());
                }
                break;
            case 3:
                // 4 Слизи + 2 Гоблина
                for (int i = 0; i < 4; i++) {
                    waveFactories.add(new SlimeFactory());
                }
                for (int i = 0; i < 2; i++) {
                    waveFactories.add(new GoblinFactory());
                }
                break;
            case 4:
                // 5 Гоблинов + 3 Слизи
                for (int i = 0; i < 5; i++) {
                    waveFactories.add(new GoblinFactory());
                }
                for (int i = 0; i < 3; i++) {
                    waveFactories.add(new SlimeFactory());
                }
                break;
            case 5:
                // 8 Гоблинов
                for (int i = 0; i < 8; i++) {
                    waveFactories.add(new GoblinFactory());
                }
                break;
        }
        
        // Спавним врагов на случайных краях экрана
        for (EnemyFactory factory : waveFactories) {
            Enemy enemy = spawnAtRandomEdge(factory);
            enemies.add(enemy);
        }
        
        levelManager.startWave(enemies.size());
        System.out.println("Spawned " + enemies.size() + " enemies for wave " + wave);
    }
    
    /**
     * Спавнит врага на случайном краю экрана.
     */
    private Enemy spawnAtRandomEdge(EnemyFactory factory) {
        int edge = MathUtils.random(3);  // 0=верх, 1=низ, 2=лево, 3=право
        float x, y;
        
        switch (edge) {
            case 0:  // Верх
                x = MathUtils.random(0, 800);
                y = 480;
                break;
            case 1:  // Низ
                x = MathUtils.random(0, 800);
                y = -40;
                break;
            case 2:  // Лево
                x = -40;
                y = MathUtils.random(0, 480);
                break;
            case 3:  // Право
                x = 840;
                y = MathUtils.random(0, 480);
                break;
            default:
                x = 0;
                y = 0;
        }
        
        return factory.create(x, y);
    }

    /**
     * Вызывается когда волна завершена.
     */
    private void onWaveCleared() {
        System.out.println("Wave cleared! Showing upgrade screen...");
        if (currentWave < 10) {
            gsm.push(GameStateManager.State.UPGRADE);
        } else {
            System.out.println("BOSS WAVE!");
        }
    }

    @Override
    public void render(float delta) {
        // Очищаем экран тёмно-зелёным цветом (арена)
        ScreenUtils.clear(0.1f, 0.3f, 0.1f, 1);
        
        // Обновляем Player
        player.update(delta);
        
        // Обновляем врагов
        Iterator<Enemy> it = enemies.iterator();
        while (it.hasNext()) {
            Enemy e = it.next();
            e.update(delta, player.x + 24, player.y + 32);  // Центр игрока
            
            // Урон игроку при контакте
            if (e.bounds.overlaps(player.bounds) && e.isAlive()) {
                player.takeDamage(e.damage * delta);
            }
            
            // Урон врагу от атаки игрока
            if (player.isAttacking()) {
                float centerPlayerX = player.x + 24;
                float centerPlayerY = player.y + 32;
                float centerEnemyX = e.x + 20;
                float centerEnemyY = e.y + 20;
                
                float dist = (float) Math.sqrt(
                    (centerPlayerX - centerEnemyX) * (centerPlayerX - centerEnemyX) +
                    (centerPlayerY - centerEnemyY) * (centerPlayerY - centerEnemyY)
                );
                
                if (dist <= 80f && e.isAlive()) {  // ATTACK_RADIUS = 80
                    e.takeDamage(10);  // BASE_DAMAGE = 10
                    System.out.println("Hit enemy! Enemy HP: " + e.hp);
                }
            }
            
            // Удалить мёртвых врагов
            if (!e.isAlive()) {
                score += e.scoreReward;
                it.remove();
            }
        }
        
        // Рисуем врагов
        batch.begin();
        for (Enemy e : enemies) {
            if (e.isAlive()) {
                e.render(batch);
            }
        }
        batch.end();
        
        // Рисуем Player
        batch.begin();
        player.render(batch);
        batch.end();
        
        // Рисуем HUD
        batch.begin();
        String hudText = "HP: " + (int)player.hp + "/" + (int)player.maxHp + 
                         "  |  Wave: " + currentWave + 
                         "  |  Score: " + score +
                         "  |  Enemies: " + enemies.size();
        font.draw(batch, hudText, 10, 470);
        batch.end();
        
        // Обработка клавиш
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gsm.set(GameStateManager.State.GAME_OVER);
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        // Очищаем все подписчики перед переходом на другой экран
        EventBus.getInstance().clear();
        dispose();
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (font != null) {
            font.dispose();
        }
        if (player != null) {
            Player.dispose();
        }
        if (enemies != null) {
            Enemy.dispose();
        }
    }
}
