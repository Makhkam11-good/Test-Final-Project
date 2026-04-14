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
import com.gladiator.strategy.EasyDifficulty;
import com.gladiator.strategy.HardDifficulty;
import com.gladiator.strategy.MediumDifficulty;

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
        
        // Рисуем меню выбора сложности с описанием (Фаза 6)
        font.getData().setScale(1.5f);
        font.draw(batch, "[1] Easy   — медленные враги, мало урона", 50, 250);
        font.draw(batch, "[2] Medium — стандартные настройки", 50, 200);
        font.draw(batch, "[3] Hard   — быстрые враги, много урона", 50, 150);
        
        batch.end();
        
        // Обработка клавиш для выбора сложности (Фаза 6)
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            GameManager.getInstance().reset();
            GameManager.getInstance().setDifficulty(new EasyDifficulty());
            System.out.println("Difficulty set: EASY");
            gsm.set(GameStateManager.State.GAME);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            GameManager.getInstance().reset();
            GameManager.getInstance().setDifficulty(new MediumDifficulty());
            System.out.println("Difficulty set: MEDIUM");
            gsm.set(GameStateManager.State.GAME);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            GameManager.getInstance().reset();
            GameManager.getInstance().setDifficulty(new HardDifficulty());
            System.out.println("Difficulty set: HARD");
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
