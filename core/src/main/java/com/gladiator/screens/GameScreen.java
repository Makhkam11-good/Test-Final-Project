package com.gladiator.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gladiator.entities.Player;
import com.gladiator.managers.GameStateManager;

/**
 * GameScreen - основной экран игры, где происходит вся игровая логика.
 * Фаза 3: добавлено создание Player и простой HUD.
 */
public class GameScreen implements Screen {
    
    private GameStateManager gsm;
    private SpriteBatch batch;
    private BitmapFont font;
    private Player player;

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
        
        // Рисуем простой HUD
        batch.begin();
        font.draw(batch, "HP: " + (int)player.hp + " / " + (int)player.maxHp, 10, 470);
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
