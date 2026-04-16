package com.gladiator.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gladiator.managers.AssetManager;
import com.gladiator.managers.GameStateManager;

/**
 * LoadingScreen - экран загрузки ресурсов.
 * Показывает прогресс-бар и процент загрузки.
 */
public class LoadingScreen implements Screen {
    private GameStateManager gsm;
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer sr;

    public LoadingScreen(GameStateManager gsm) {
        this.gsm = gsm;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.getRegion().getTexture().setFilter(com.badlogic.gdx.graphics.Texture.TextureFilter.Linear, com.badlogic.gdx.graphics.Texture.TextureFilter.Linear);
        this.sr = new ShapeRenderer();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // Обновляй загрузку ресурсов
        AssetManager.getInstance().update();

        // Очисти экран чёрным
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Получи прогресс загрузки (0.0 - 1.0)
        float progress = AssetManager.getInstance().getManager().getProgress();

        // Рисуй прогресс-бар
        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Фон бара (серый)
        sr.setColor(0.3f, 0.3f, 0.3f, 1f);
        sr.rect(300, 220, 200, 20);

        // Прогресс (золотой)
        sr.setColor(1f, 0.84f, 0f, 1f);
        sr.rect(300, 220, 200 * progress, 20);

        sr.end();

        // Рамка бара
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.WHITE);
        sr.rect(300, 220, 200, 20);
        sr.end();

        // Текст загрузки
        batch.begin();
        font.draw(batch, "Loading... " + (int)(progress * 100) + "%", 350, 190);
        batch.end();

        // Если загрузка завершена
        if (AssetManager.getInstance().isLoaded()) {
            // Запусти фоновую музыку
            Music bgm = AssetManager.getInstance().getBgm();
            if (bgm != null) {
                bgm.play();
            }

            // Переди в главное меню
            gsm.set(GameStateManager.State.MENU);
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
        batch.dispose();
        font.dispose();
        sr.dispose();
    }
}