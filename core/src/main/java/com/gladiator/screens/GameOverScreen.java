package com.gladiator.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gladiator.managers.AudioManager;
import com.gladiator.managers.GameManager;
import com.gladiator.managers.GameStateManager;

public class GameOverScreen implements Screen {

    private final GameStateManager gsm;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final OrthographicCamera camera;
    private final ShapeRenderer shapeRenderer;
    private final StringBuilder lineBuilder = new StringBuilder(64);
    private float time;

    public GameOverScreen(GameStateManager gsm) {
        this.gsm = gsm;
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
    }

    @Override
    public void show() {
        AudioManager.getInstance().playMusic(AudioManager.MusicMode.GAME_OVER);
    }

    @Override
    public void render(float delta) {
        time += delta;
        ScreenUtils.clear(0.055f, 0.018f, 0.018f, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.32f, 0.02f, 0.025f, 0.55f);
        shapeRenderer.circle(400f, 250f, 155f + MathUtils.sin(time * 2f) * 5f);
        shapeRenderer.setColor(0.02f, 0.01f, 0.01f, 0.78f);
        shapeRenderer.rect(90f, 95f, 620f, 280f);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        font.getData().setScale(3.0f);
        font.setColor(1f, 0.32f, 0.22f, 1f);
        font.draw(batch, "FALLEN GLADIATOR", 120, 350);

        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        lineBuilder.setLength(0);
        lineBuilder.append("Score: ").append(GameManager.getInstance().getScore());
        font.draw(batch, lineBuilder, 200, 280);

        lineBuilder.setLength(0);
        lineBuilder.append("Wave reached: ").append(GameManager.getInstance().getCurrentWave());
        font.draw(batch, lineBuilder, 200, 230);

        lineBuilder.setLength(0);
        lineBuilder.append("Cause: ").append(GameManager.getInstance().getLastDeathCause());
        font.draw(batch, lineBuilder, 200, 180);

        font.getData().setScale(1.2f);
        font.setColor(1f, 0.78f, 0.36f, 1f);
        font.draw(batch, "[R] Try Again  [ESC] Menu", 200, 100);
        font.setColor(Color.WHITE);

        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            GameManager.getInstance().resetRun();
            gsm.set(GameStateManager.State.GAME);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            GameManager.getInstance().resetRun();
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
        shapeRenderer.dispose();
    }
}
