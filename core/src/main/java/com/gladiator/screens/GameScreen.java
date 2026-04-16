package com.gladiator.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gladiator.entities.Boss;
import com.gladiator.entities.Enemy;
import com.gladiator.entities.Player;
import com.gladiator.events.EventBus;
import com.gladiator.events.GameEvent;
import com.gladiator.factories.BossFactory;
import com.gladiator.factories.EnemyFactory;
import com.gladiator.factories.GoblinFactory;
import com.gladiator.factories.SlimeFactory;
import com.gladiator.managers.GameManager;
import com.gladiator.managers.GameStateManager;
import com.gladiator.managers.LevelManager;

/**
 * GameScreen - основной экран игры, где происходит вся игровая логика.
 * Фаза 8: добавлена поддержка Boss волны 10.
 */
public class GameScreen implements Screen {
    
    private GameStateManager gsm;
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private Player player;
    private LevelManager levelManager;
    
    // Враги
    private List<Enemy> enemies;
    private float spawnInterval;  // Фаза 6: интервал спавна из GameManager
    private float spawnTimer;
    private boolean waveCleared = false;  // Флаг для отложенной обработки окончания волны
    private boolean enemiesListModified = false;  // Флаг для защиты от модификации листа
    
    
    // Босс (Фаза 8)
    private Boss boss = null;
    private boolean bossWave = false;
    
    public GameScreen(GameStateManager gsm) {
        this.gsm = gsm;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        shapeRenderer = new ShapeRenderer();  // Фаза 8: для рисования Boss
        
        // Создаём Player в центре экрана
        player = new Player();
        
        // Передаём текущего игрока в GameStateManager для UpgradeScreen (Фаза 7)
        gsm.setCurrentPlayer(player);
        
        // Создаём LevelManager
        levelManager = new LevelManager();
        
        // Инициализируем враги (Фаза 6: используем GameManager)
        enemies = new ArrayList<>();
        
        // Получаем интервал спавна из стратегии сложности (Фаза 6)
        spawnInterval = GameManager.getInstance().getDifficulty().getSpawnInterval();
        spawnTimer = spawnInterval;
        
        // Спавним первую волну
        spawnWave(GameManager.getInstance().getCurrentWave());
        
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
        
        // Фаза 8: подписываемся на смерть босса
        EventBus.getInstance().subscribe(
            GameEvent.Type.BOSS_DIED,
            event -> {
                System.out.println("Victory! Boss is dead!");
                gsm.set(GameStateManager.State.VICTORY);
            }
        );
        
        // Фаза 9: подписываемся на звуки через Observer
        EventBus.getInstance().subscribe(GameEvent.Type.ENEMY_DIED, event -> {
            Sound sound = com.gladiator.managers.AssetManager.getInstance().getSound("enemy_death");
            if (sound != null) sound.play(0.8f);
        });
        
        EventBus.getInstance().subscribe(GameEvent.Type.PLAYER_HURT, event -> {
            Sound sound = com.gladiator.managers.AssetManager.getInstance().getSound("hit");
            if (sound != null) sound.play(1.0f);
        });
        
        EventBus.getInstance().subscribe(GameEvent.Type.BOSS_DIED, event -> {
            Music bgm = com.gladiator.managers.AssetManager.getInstance().getBgm();
            if (bgm != null) {
                bgm.stop();
            }
        });
    }

    /**
     * Спавнит волну врагов. Фаза 8: добавлена волна 10 с Боссом.
     */
    private void spawnWave(int wave) {
        waveCleared = false;  // Сбрасываем флаг при спавне новой волны
        enemiesListModified = false;  // Сбрасываем флаг модификации
        
        if (wave == 10) {
            bossWave = true;
            enemies.clear();
            enemiesListModified = true;  // Отмечаем что лист был изменён
            // Спавни Босса по центру верхнего края
            boss = (Boss) new BossFactory().create(400 - 40, 440);
            levelManager.startWave(1);  // 1 враг (Босс считается как 1)
            System.out.println("WAVE 10 — BOSS APPEARS!");
            
            // Фаза 9: воспроизведи звук появления босса
            Sound bossRoarSound = 
                com.gladiator.managers.AssetManager.getInstance().getSound("boss_roar");
            if (bossRoarSound != null) {
                bossRoarSound.play(1.0f);
            }
            
            return;
        }
        
        bossWave = false;
        boss = null;
        
        enemies.clear();
        
        List<EnemyFactory> waveFactories = new ArrayList<>();
        
        // Определяем состав врагов для каждой волны (GDD раздел 4)
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
            case 6:
                // 6 Гоблинов + 4 Слизи (Фаза 6)
                for (int i = 0; i < 6; i++) {
                    waveFactories.add(new GoblinFactory());
                }
                for (int i = 0; i < 4; i++) {
                    waveFactories.add(new SlimeFactory());
                }
                break;
            case 7:
                // 10 Гоблинов (Фаза 6)
                for (int i = 0; i < 10; i++) {
                    waveFactories.add(new GoblinFactory());
                }
                break;
            case 8:
                // 8 Гоблинов + 5 Слизей (Фаза 6)
                for (int i = 0; i < 8; i++) {
                    waveFactories.add(new GoblinFactory());
                }
                for (int i = 0; i < 5; i++) {
                    waveFactories.add(new SlimeFactory());
                }
                break;
            case 9:
                // 12 Гоблинов (Фаза 6)
                for (int i = 0; i < 12; i++) {
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
     * Вызывается когда волна завершена. Фаза 8: обновляет логику для волны 10 (Boss).
     */
    private void onWaveCleared() {
        System.out.println("Wave cleared! Current wave: " + GameManager.getInstance().getCurrentWave());
        int nextWave = GameManager.getInstance().getCurrentWave() + 1;
        GameManager.getInstance().setCurrentWave(nextWave);
        
        // Фаза 8: волна 10 (босс) не показывает UpgradeScreen - переходим на VICTORY
        if (nextWave == 11) {
            System.out.println("GAME WON! Transitioning to Victory Screen...");
            // Victory экран уже установлен в подписчике на BOSS_DIED в show()
            // Здесь мы просто логируем
            return;
        }
        
        if (nextWave <= 9) {
            // Фаза 7: создаём новый UpgradeScreen с текущим игроком
            UpgradeScreen upgradeScreen = new UpgradeScreen(gsm, player);
            gsm.registerScreen(GameStateManager.State.UPGRADE, upgradeScreen);
            gsm.push(GameStateManager.State.UPGRADE);
        } else if (nextWave == 10) {
            // Фаза 8: волна 10 - босс
            System.out.println("WAVE 10 — BOSS WAVE! Spawning boss...");
            spawnWave(10);
        }
    }

    @Override
    public void render(float delta) {
        // Очищаем экран чёрным
        ScreenUtils.clear(0, 0, 0, 1);
        
        // Рисуем фон арены (Фаза 9)
        batch.begin();
        Texture bgTexture = 
            com.gladiator.managers.AssetManager.getInstance().getTexture("backgrounds/arena.png");
        if (bgTexture != null) {
            batch.draw(bgTexture, 0, 0, 800, 480);
        } else {
            // Fallback: если фон не найден, рисуем тёмно-зелёный прямоугольник как раньше
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0.1f, 0.3f, 0.1f, 1);
            shapeRenderer.rect(0, 0, 800, 480);
            shapeRenderer.end();
        }
        batch.end();
        
        // Обновляем Player
        player.update(delta);
        
        // Фаза 8: обновляем Boss если волна 10
        if (bossWave && boss != null && boss.alive) {
            boss.update(delta, player.x + 24, player.y + 32);  // Центр игрока
            
            // Урон игроку от Босса
            if (boss.bounds.overlaps(player.bounds)) {
                player.takeDamage(boss.getContactDamage() * delta);
            }
            
            // Урон Боссу от атаки игрока
            if (player.isAttacking()) {
                float centerPlayerX = player.x + 24;
                float centerPlayerY = player.y + 32;
                float centerEnemyX = boss.x + 40;
                float centerEnemyY = boss.y + 40;
                
                float dist = (float) Math.sqrt(
                    (centerPlayerX - centerEnemyX) * (centerPlayerX - centerEnemyX) +
                    (centerPlayerY - centerEnemyY) * (centerPlayerY - centerEnemyY)
                );
                
                if (dist <= 80f && boss.alive) {  // ATTACK_RADIUS = 80
                    boss.takeDamage(player.getStats().getDamage());
                    System.out.println("Hit BOSS! HP: " + boss.hp);
                }
            }
        } else if (!enemiesListModified) {
            // Обновляем обычных врагов - создаём копию листа для безопасного итерирования
            List<Enemy> enemiesCopy = new ArrayList<>(enemies);
            for (Enemy e : enemiesCopy) {
                if (!e.isAlive()) continue;  // Пропускаем мёртвых в копии
                
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
                        e.takeDamage(player.getStats().getDamage());  // Фаза 7: используем stats.getDamage()
                        System.out.println("Hit enemy! Enemy HP: " + e.hp);
                    }
                }
                
                // Отмечаем мёртвых врагов но НЕ удаляем в итератор
                if (!e.isAlive()) {
                    GameManager.getInstance().addScore(e.scoreReward);
                    EventBus.getInstance().post(new GameEvent(GameEvent.Type.ENEMY_DIED));
                }
            }
            
            // Удаляем мёртвых врагов ПОСЛЕ итерирования
            if (enemies.removeIf(e -> !e.isAlive())) {
                enemiesListModified = true;
            }
            
            // Отмечаем что волна очищена если все враги мертвы
            if (enemies.isEmpty() && !waveCleared) {
                waveCleared = true;
            }
            
            // Сбрасываем флаг модификации для следующего кадра
            enemiesListModified = false;
        }
        
        // Рисуем врагов (если не волна босса)
        if (!bossWave) {
            batch.begin();
            for (Enemy e : enemies) {
                if (e.isAlive()) {
                    e.render(batch);
                }
            }
            batch.end();
        }
        
        // Фаза 8: рисуем Босса если волна 10
        if (bossWave && boss != null && boss.alive) {
            batch.begin();
            boss.renderBoss(batch, shapeRenderer);
            batch.end();
        }
        
        // Рисуем Player
        batch.begin();
        player.render(batch);
        batch.end();
        
        // Рисуем HUD (Фаза 8: обновлены для волны 10)
        batch.begin();
        int maxHp = player.getStats().getMaxHp();
        int damage = player.getStats().getDamage();
        float speed = player.getStats().getSpeed();
        float cooldown = player.getStats().getAttackCooldown();
        
        String hudText;
        if (bossWave && boss != null) {
            hudText = "HP: " + (int)player.hp + "/" + maxHp + 
                     "  |  WAVE 10 — BOSS FIGHT!  |  Score: " + GameManager.getInstance().getScore();
        } else {
            hudText = "HP: " + (int)player.hp + "/" + maxHp + 
                     "  |  Wave: " + GameManager.getInstance().getCurrentWave() + "/10" +
                     "  |  Score: " + GameManager.getInstance().getScore() +
                     "  |  [" + GameManager.getInstance().getDifficulty().getName() + "]";
        }
        font.draw(batch, hudText, 10, 470);
        
        // Вторая строка HUD с дополнительными характеристиками (Фаза 7)
        String statsText = "DMG: " + damage + "  |  SPD: " + (int)speed + "  |  CD: " + String.format("%.2f", cooldown);
        font.draw(batch, statsText, 10, 450);
        batch.end();
        
        // Фаза 8: нарисуй большой HP бар Босса внизу экрана
        if (bossWave && boss != null && boss.alive) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            
            // Фон HP бара (серый): x=200, y=10, width=400, height=16
            shapeRenderer.setColor(Color.DARK_GRAY);
            shapeRenderer.rect(200, 10, 400, 16);
            
            // Полоска HP (красная): ширина = 400 * (boss.hp/boss.maxHp)
            shapeRenderer.setColor(Color.SCARLET);
            shapeRenderer.rect(200, 10, 400 * (boss.hp / boss.maxHp), 16);
            
            shapeRenderer.end();
            
            // Рисуй надпись над HP баром
            batch.begin();
            font.setColor(Color.RED);
            font.getData().setScale(1.0f);
            font.draw(batch, "DEMON KING  " + (int)boss.hp + " / " + (int)boss.maxHp + " HP", 250, 30);
            font.setColor(Color.WHITE);
            batch.end();
        }
        
        // Проверяем завершение волны (все враги мертвы или босс мёртв) - ПОСЛЕ всех batch.end()
        // Отложенная обработка - флаг устанавливается в update, обрабатывается здесь после render завершён
        
        // Фаза 8: проверяем смерть Босса для волны 10
        if (bossWave && boss != null && !boss.alive && !waveCleared) {
            waveCleared = true;
            System.out.println("Boss is dead! Wave 10 will be marked as cleared!");
        }
        
        if (waveCleared) {
            waveCleared = false;
            // Полностью очищаем врагов перед сменой экрана
            enemies.clear();
            EventBus.getInstance().post(new GameEvent(GameEvent.Type.WAVE_CLEARED));
        }
        
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
        // Пересоздаём batch в случае если он был disposed при hide()
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        shapeRenderer = new ShapeRenderer();
        
        // Спавним следующую волну после UpgradeScreen (Фаза 7-8)
        spawnWave(GameManager.getInstance().getCurrentWave());
    }

    @Override
    public void hide() {
        // Очищаем все подписчики перед переходом на другой экран
        EventBus.getInstance().clear();
        // Закончим любые открытые batches
        if (batch != null && batch.isDrawing()) {
            batch.end();
        }
        // Не удаляем ресурсы - пусть dispose() сделает это при полном завершении
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (font != null) {
            font.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
