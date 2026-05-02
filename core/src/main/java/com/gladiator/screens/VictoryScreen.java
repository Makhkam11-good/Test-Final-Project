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

public class VictoryScreen implements Screen {

    private final GameStateManager gsm;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final OrthographicCamera camera;
    private final ShapeRenderer shapeRenderer;
    private final StringBuilder lineBuilder = new StringBuilder(64);
    private float time;

    public VictoryScreen(GameStateManager gsm) {
        this.gsm = gsm;
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
    }

    @Override
    public void show() {
        AudioManager.getInstance().playMusic(AudioManager.MusicMode.VICTORY);
    }

    @Override
    public void render(float delta) {
        time += delta;
        ScreenUtils.clear(0.08f, 0.055f, 0.025f, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.94f, 0.62f, 0.16f, 0.38f);
        shapeRenderer.circle(400f, 260f, 160f + MathUtils.sin(time * 1.8f) * 9f);
        shapeRenderer.setColor(0.12f, 0.07f, 0.025f, 0.82f);
        shapeRenderer.rect(92f, 82f, 616f, 304f);
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 0.76f, 0.28f, 0.42f);
        shapeRenderer.circle(400f, 260f, 112f);
        shapeRenderer.circle(400f, 260f, 166f);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        font.getData().setScale(3.0f);
        font.setColor(1f, 0.82f, 0.28f, 1f);
        font.draw(batch, "VICTORY", 265, 350);

        font.getData().setScale(2.0f);
        font.setColor(Color.WHITE);
        font.draw(batch, "The Demon King is broken.", 145, 292);

        font.getData().setScale(1.5f);
        lineBuilder.setLength(0);
        lineBuilder.append("Score: ").append(GameManager.getInstance().getScore());
        font.draw(batch, lineBuilder, 200, 230);

        lineBuilder.setLength(0);
        lineBuilder.append("Enemies killed: ").append(GameManager.getInstance().getEnemiesKilled());
        font.draw(batch, lineBuilder, 200, 190);

        lineBuilder.setLength(0);
        lineBuilder.append("Upgrades: ").append(GameManager.getInstance().getUpgradesCollected());
        font.draw(batch, lineBuilder, 200, 150);

        lineBuilder.setLength(0);
        lineBuilder.append("Time survived: ").append((int) GameManager.getInstance().getTimeSurvived()).append("s");
        font.draw(batch, lineBuilder, 200, 110);

        font.getData().setScale(1.2f);
        font.setColor(1f, 0.82f, 0.36f, 1f);
        font.draw(batch, "[R] Play Again   [ESC] Menu", 200, 70);
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
