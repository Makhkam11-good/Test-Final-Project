package com.gladiator.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gladiator.managers.GameManager;
import com.gladiator.managers.GameStateManager;

/**
 * VictoryScreen - экран победы (появляется после победы над Боссом на волне 10).
 * Фаза 6: показывает счёт, сложность и опции для повтора.
 */
public class VictoryScreen implements Screen {
    
    private GameStateManager gsm;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;

    public VictoryScreen(GameStateManager gsm) {
        this.gsm = gsm;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // Очищаем экран золотым цветом
        ScreenUtils.clear(1.0f, 0.84f, 0.0f, 1);
        
        // Обновляем камеру
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        batch.begin();
        
        // Рисуем текст Victory
        font.getData().setScale(3.0f);
        font.draw(batch, "VICTORY!", 250, 350);
        
        // Фаза 6: показываем счёт и сложность
        font.getData().setScale(1.5f);
        font.draw(batch, "Score: " + GameManager.getInstance().getScore(), 200, 280);
        font.draw(batch, "Difficulty: " + GameManager.getInstance().getDifficulty().getName(), 200, 230);
        
        font.getData().setScale(1.2f);
        font.draw(batch, "[R] Play Again  [ESC] Menu", 200, 100);
        
        batch.end();
        
        // Обработка клавиш (Фаза 6: сбрасываем GameManager при возврате в меню)
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            GameManager.getInstance().reset();
            gsm.set(GameStateManager.State.MENU);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            GameManager.getInstance().reset();
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
        if (batch != null) {
            batch.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }
}
