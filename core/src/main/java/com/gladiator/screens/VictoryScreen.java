package com.gladiator.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gladiator.managers.GameStateManager;

/**
 * VictoryScreen - экран победы (появляется после победы над Боссом на волне 10).
 */
public class VictoryScreen implements Screen {
    
    private GameStateManager gsm;

    public VictoryScreen(GameStateManager gsm) {
        this.gsm = gsm;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // Очищаем экран золотым цветом
        ScreenUtils.clear(1.0f, 0.84f, 0.0f, 1);
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
