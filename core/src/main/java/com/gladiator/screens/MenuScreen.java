package com.gladiator.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gladiator.managers.GameStateManager;

/**
 * MenuScreen - главное меню с выбором сложности (Easy/Medium/Hard).
 */
public class MenuScreen implements Screen {
    
    private GameStateManager gsm;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;

    public MenuScreen(GameStateManager gsm) {
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
        // Очищаем экран тёмно-синим цветом
        ScreenUtils.clear(0.05f, 0.05f, 0.3f, 1);
        
        // Обновляем камеру
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        batch.begin();
        
        // Рисуем заголовок игры
        font.getData().setScale(2.5f);
        font.draw(batch, "GLADIATOR ARENA", 150, 340);
        
        // Рисуем меню выбора сложности
        font.getData().setScale(1.5f);
        font.draw(batch, "[1] Easy", 300, 250);
        font.draw(batch, "[2] Medium", 280, 200);
        font.draw(batch, "[3] Hard", 300, 150);
        
        batch.end();
        
        // Обработка клавиш для выбора сложности
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            gsm.set(GameStateManager.State.GAME);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            gsm.set(GameStateManager.State.GAME);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            gsm.set(GameStateManager.State.GAME);
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
    }
}
