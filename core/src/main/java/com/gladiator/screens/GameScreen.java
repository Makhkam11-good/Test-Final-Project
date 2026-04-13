package com.gladiator.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gladiator.entities.Player;
import com.gladiator.events.EventBus;
import com.gladiator.events.GameEvent;
import com.gladiator.managers.GameStateManager;
import com.gladiator.managers.LevelManager;

/**
 * GameScreen - основной экран игры, где происходит вся игровая логика.
 * Фаза 4: добавлены EventBus и LevelManager.
 */
public class GameScreen implements Screen {
    
    private GameStateManager gsm;
    private SpriteBatch batch;
    private BitmapFont font;
    private Player player;
    private LevelManager levelManager;

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
        
        // Создаём LevelManager и начинаем волну
        levelManager = new LevelManager();
        levelManager.startWave(3);  // Тестовая волна из 3 врагов
        
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
     * Вызывается когда волна завершена.
     */
    private void onWaveCleared() {
        System.out.println("Wave cleared! Showing upgrade screen...");
        gsm.push(GameStateManager.State.UPGRADE);
    }

    @Override
    public void render(float delta) {
        // Очищаем экран тёмно-зелёным цветом (арена)
        ScreenUtils.clear(0.1f, 0.3f, 0.1f, 1);
        
        // Обновляем Player
        player.update(delta);
        
        // Рисуем Player
        batch.begin();
        player.render(batch);
        batch.end();
        
        // Рисуем HUD с информацией о волне и врагах
        batch.begin();
        String hudText = "HP: " + (int)player.hp + " / " + (int)player.maxHp + 
                         "  |  Wave: " + levelManager.getCurrentWave() + 
                         "  |  Enemies: " + levelManager.getEnemiesAlive();
        font.draw(batch, hudText, 10, 470);
        batch.end();
        
        // Обработка клавиш
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gsm.set(GameStateManager.State.GAME_OVER);
        }
        
        // DEBUG: Временная кнопка Q для теста EventBus
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            System.out.println("DEBUG: Enemy died event sent");
            EventBus.getInstance().post(new GameEvent(GameEvent.Type.ENEMY_DIED));
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
    }
}
