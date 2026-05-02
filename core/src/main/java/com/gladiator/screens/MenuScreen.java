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
import com.gladiator.strategy.EasyDifficulty;
import com.gladiator.strategy.HardDifficulty;
import com.gladiator.strategy.MediumDifficulty;

public class MenuScreen implements Screen {

    private final GameStateManager gsm;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final OrthographicCamera camera;
    private final ShapeRenderer shapeRenderer;

    private final String title = "GLADIATOR ARENA";
    private final String easyLabel = "[1] Recruit  - slower enemies, lower damage";
    private final String mediumLabel = "[2] Veteran  - intended challenge";
    private final String hardLabel = "[3] Champion - faster, lethal arena";
    private float time;

    public MenuScreen(GameStateManager gsm) {
        this.gsm = gsm;
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
    }

    @Override
    public void show() {
        AudioManager.getInstance().playMusic(AudioManager.MusicMode.MENU);
    }

    @Override
    public void render(float delta) {
        time += delta;
        ScreenUtils.clear(0.025f, 0.022f, 0.02f, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.09f, 0.065f, 0.04f, 1f);
        shapeRenderer.rect(0f, 0f, 800f, 480f);
        shapeRenderer.setColor(0.42f, 0.08f, 0.04f, 0.38f);
        shapeRenderer.circle(400f, 236f, 210f + MathUtils.sin(time * 0.7f) * 7f);
        shapeRenderer.setColor(0.96f, 0.62f, 0.22f, 0.18f);
        shapeRenderer.circle(400f, 236f, 94f);
        shapeRenderer.setColor(0.02f, 0.018f, 0.016f, 0.72f);
        shapeRenderer.rect(0f, 0f, 800f, 80f);
        shapeRenderer.rect(0f, 400f, 800f, 80f);
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.96f, 0.67f, 0.26f, 0.24f);
        shapeRenderer.circle(400f, 236f, 125f);
        shapeRenderer.circle(400f, 236f, 205f);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        font.getData().setScale(2.8f);
        font.setColor(1f, 0.77f, 0.26f, 1f);
        font.draw(batch, title, 118, 350);

        font.getData().setScale(1.0f);
        font.setColor(0.85f, 0.78f, 0.66f, 1f);
        font.draw(batch, "Survive 12 waves, master stacked upgrades, and break the Demon King's court.", 100, 302);

        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        font.draw(batch, easyLabel, 74, 242);
        font.draw(batch, mediumLabel, 74, 196);
        font.draw(batch, hardLabel, 74, 150);

        font.getData().setScale(0.95f);
        font.setColor(0.78f, 0.74f, 0.66f, 1f);
        font.draw(batch, "WASD to move. Your gladiator auto-slashes when the gold meter is full.", 132, 76);
        font.setColor(Color.WHITE);

        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            GameManager.getInstance().startNewRun(new EasyDifficulty());
            gsm.set(GameStateManager.State.GAME);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            GameManager.getInstance().startNewRun(new MediumDifficulty());
            gsm.set(GameStateManager.State.GAME);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            GameManager.getInstance().startNewRun(new HardDifficulty());
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
        shapeRenderer.dispose();
    }
}
