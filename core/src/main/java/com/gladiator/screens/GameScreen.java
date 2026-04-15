package com.gladiator.screens;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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
    }

    /**
     * Спавнит волну врагов. Фаза 8: добавлена волна 10 с Боссом.
     */
    private void spawnWave(int wave) {
        if (wave == 10) {
            bossWave = true;
            enemies.clear();
            // Спавни Босса по центру верхнего края
            boss = (Boss) new BossFactory().create(400 - 40, 440);
            levelManager.startWave(1);  // 1 враг (Босс считается как 1)
            System.out.println("WAVE 10 — BOSS APPEARS!");
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
        System.out.println("Wave cleared! Showing upgrade screen...");
        int nextWave = GameManager.getInstance().getCurrentWave() + 1;
        GameManager.getInstance().setCurrentWave(nextWave);
        if (nextWave <= 9) {
            // Фаза 7: создаём новый UpgradeScreen с текущим игроком
            UpgradeScreen upgradeScreen = new UpgradeScreen(gsm, player);
            gsm.registerScreen(GameStateManager.State.UPGRADE, upgradeScreen);
            gsm.push(GameStateManager.State.UPGRADE);
        } else if (nextWave == 10) {
            // Фаза 8: волна 10 - босс
            System.out.println("WAVE 10 — BOSS WAVE!");
            spawnWave(10);
        }
    }

    @Override
    public void render(float delta) {
        // Очищаем экран тёмно-зелёным цветом (арена)
        ScreenUtils.clear(0.1f, 0.3f, 0.1f, 1);
        
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
        } else {
            // Обновляем обычных врагов
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
                        e.takeDamage(player.getStats().getDamage());  // Фаза 7: используем stats.getDamage()
                        System.out.println("Hit enemy! Enemy HP: " + e.hp);
                    }
                }
                
                // Удалить мёртвых врагов (Фаза 6: публикуем событие и добавляем счёт)
                if (!e.isAlive()) {
                    GameManager.getInstance().addScore(e.scoreReward);
                    EventBus.getInstance().post(new GameEvent(GameEvent.Type.ENEMY_DIED));
                    it.remove();
                }
            }
            
            // Проверяем завершение волны (все враги мертвы)
            if (enemies.isEmpty()) {
                EventBus.getInstance().post(new GameEvent(GameEvent.Type.WAVE_CLEARED));
            }
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
            boss.renderBoss(shapeRenderer);
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
        // Спавним следующую волну после UpgradeScreen (Фаза 7-8)
        spawnWave(GameManager.getInstance().getCurrentWave());
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
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (player != null) {
            Player.dispose();
        }
        if (enemies != null) {
            Enemy.dispose();
        }
    }
}
