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
 * GameOverScreen - экран поражения (показывает счёт и достигнутую волну).
 */
public class GameOverScreen implements Screen {
    
    private GameStateManager gsm;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;

    public GameOverScreen(GameStateManager gsm) {
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
        // Очищаем экран тёмно-красным цветом
        ScreenUtils.clear(0.5f, 0.1f, 0.1f, 1);
        
        // Обновляем камеру
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        batch.begin();
        
        // Рисуем текст Game Over
        font.getData().setScale(3.0f);
        font.draw(batch, "GAME OVER", 250, 350);
        
        // Фаза 6: показываем счёт, волну и сложность
        font.getData().setScale(1.5f);
        font.draw(batch, "Score: " + GameManager.getInstance().getScore(), 200, 280);
        font.draw(batch, "Wave reached: " + GameManager.getInstance().getCurrentWave(), 200, 230);
        font.draw(batch, "Difficulty: " + GameManager.getInstance().getDifficulty().getName(), 200, 180);
        
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
        batch.dispose();
        font.dispose();
    }
}
