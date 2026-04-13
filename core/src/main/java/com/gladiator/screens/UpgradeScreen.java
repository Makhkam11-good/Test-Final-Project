package com.gladiator.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gladiator.managers.GameStateManager;

/**
 * UpgradeScreen - экран выбора апгрейда после каждой волны (показывает 3 карточки).
 */
public class UpgradeScreen implements Screen {
    
    private GameStateManager gsm;

    public UpgradeScreen(GameStateManager gsm) {
        this.gsm = gsm;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // Очищаем экран тёмно-жёлтым цветом
        ScreenUtils.clear(0.4f, 0.3f, 0.1f, 1);
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
