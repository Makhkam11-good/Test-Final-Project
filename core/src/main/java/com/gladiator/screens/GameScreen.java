package com.gladiator.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gladiator.managers.GameStateManager;

/**
 * GameScreen - основной экран игры, где происходит вся игровая логика.
 */
public class GameScreen implements Screen {
    
    private GameStateManager gsm;

    public GameScreen(GameStateManager gsm) {
        this.gsm = gsm;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // Очищаем экран тёмно-зелёным цветом (арена)
        ScreenUtils.clear(0.1f, 0.3f, 0.1f, 1);
        
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
    }

    @Override
    public void dispose() {
    }
}
