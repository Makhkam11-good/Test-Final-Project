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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gladiator.decorator.ArmorDecorator;
import com.gladiator.decorator.AttackSpeedDecorator;
import com.gladiator.decorator.FireWeaponDecorator;
import com.gladiator.decorator.PlayerDecorator;
import com.gladiator.decorator.PoisonDecorator;
import com.gladiator.decorator.ShieldDecorator;
import com.gladiator.decorator.SpeedBootsDecorator;
import com.gladiator.entities.Player;
import com.gladiator.managers.GameManager;
import com.gladiator.managers.GameStateManager;

public class UpgradeScreen implements Screen {
    private static final int OPTION_COUNT = 3;

    private final GameStateManager gsm;
    private final Player player;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    private final Vector3 touch = new Vector3();
    private final Rectangle[] cardBounds = new Rectangle[OPTION_COUNT];
    private final PlayerDecorator[] options = new PlayerDecorator[OPTION_COUNT];
    private final StringBuilder waveBuilder = new StringBuilder(32);
    private float time;

    public UpgradeScreen(GameStateManager gsm, Player player) {
        this.gsm = gsm;
        this.player = player;
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        cardBounds[0] = new Rectangle(60, 150, 200, 160);
        cardBounds[1] = new Rectangle(300, 150, 200, 160);
        cardBounds[2] = new Rectangle(540, 150, 200, 160);

        buildOptions();
    }

    @Override
    public void show() {
    }

    private void buildOptions() {
        int[] pool = {0, 1, 2, 3, 4, 5};
        for (int i = pool.length - 1; i > 0; i--) {
            int j = MathUtils.random(i);
            int temp = pool[i];
            pool[i] = pool[j];
            pool[j] = temp;
        }

        for (int i = 0; i < OPTION_COUNT; i++) {
            options[i] = createDecorator(pool[i]);
        }
    }

    private PlayerDecorator createDecorator(int index) {
        switch (index) {
            case 0:
                return new FireWeaponDecorator(player.getStats());
            case 1:
                return new PoisonDecorator(player.getStats());
            case 2:
                return new ShieldDecorator(player.getStats());
            case 3:
                return new ArmorDecorator(player.getStats());
            case 4:
                return new SpeedBootsDecorator(player.getStats());
            case 5:
            default:
                return new AttackSpeedDecorator(player.getStats());
        }
    }

    @Override
    public void render(float delta) {
        time += delta;
        ScreenUtils.clear(0.045f, 0.025f, 0.04f, 1);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        touch.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        camera.unproject(touch);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.11f, 0.08f, 0.055f, 1f);
        shapeRenderer.rect(0f, 0f, 800f, 480f);
        shapeRenderer.setColor(0.42f, 0.12f, 0.08f, 0.28f);
        shapeRenderer.circle(400f, 240f, 190f + MathUtils.sin(time * 1.6f) * 6f);
        shapeRenderer.setColor(0.9f, 0.6f, 0.22f, 0.12f);
        shapeRenderer.circle(400f, 240f, 82f);
        shapeRenderer.end();

        batch.begin();
        font.getData().setScale(1.6f);
        font.setColor(1f, 0.82f, 0.35f, 1f);
        font.draw(batch, "CHOOSE YOUR SPOILS", 236, 452);
        font.getData().setScale(1.0f);
        font.setColor(Color.WHITE);
        waveBuilder.setLength(0);
        waveBuilder.append("Wave ").append(GameManager.getInstance().getCurrentWave() - 1).append(" cleared");
        font.draw(batch, waveBuilder, 342, 416);
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < OPTION_COUNT; i++) {
            Rectangle card = cardBounds[i];
            boolean hovered = card.contains(touch.x, touch.y);
            float pulse = hovered ? 0.08f + MathUtils.sin(time * 12f) * 0.03f : 0f;
            shapeRenderer.setColor(0.16f + pulse, 0.105f + pulse, 0.07f, 1f);
            shapeRenderer.rect(card.x, card.y, card.width, card.height);
            shapeRenderer.setColor(0.7f, 0.28f, 0.12f, hovered ? 0.72f : 0.36f);
            shapeRenderer.rect(card.x, card.y + card.height - 8f, card.width, 8f);
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < OPTION_COUNT; i++) {
            Rectangle card = cardBounds[i];
            shapeRenderer.setColor(card.contains(touch.x, touch.y) ? Color.GOLD : Color.LIGHT_GRAY);
            shapeRenderer.rect(card.x, card.y, card.width, card.height);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        for (int i = 0; i < OPTION_COUNT; i++) {
            Rectangle card = cardBounds[i];
            font.setColor(1f, 0.82f, 0.35f, 1f);
            font.draw(batch, "[" + (i + 1) + "]", card.x + 10, card.y + card.height - 24);
            font.setColor(Color.WHITE);
            font.draw(batch, options[i].getLabel(), card.x + 42, card.y + card.height - 30);
            font.setColor(0.78f, 0.73f, 0.64f, 1f);
            font.draw(batch, getUpgradeDescription(options[i]), card.x + 12, card.y + 78);
            font.setColor(1f, 0.88f, 0.48f, 1f);
            font.draw(batch, "Click or press " + (i + 1), card.x + 46, card.y + 28);
        }
        font.setColor(0.9f, 0.82f, 0.68f, 1f);
        font.draw(batch, "Choose one upgrade. Effects stack for the whole run.", 226, 86);
        font.setColor(Color.WHITE);
        batch.end();

        if (Gdx.input.justTouched()) {
            for (int i = 0; i < OPTION_COUNT; i++) {
                if (cardBounds[i].contains(touch.x, touch.y)) {
                    applyUpgrade(i);
                    break;
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            applyUpgrade(0);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            applyUpgrade(1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            applyUpgrade(2);
        }
    }

    private String getUpgradeDescription(PlayerDecorator decorator) {
        String label = decorator.getLabel();
        if (label.startsWith("Fire")) {
            return "Bigger burst damage on every slash.";
        }
        if (label.startsWith("Poison")) {
            return "Reliable damage scaling for long fights.";
        }
        if (label.startsWith("Shield")) {
            return "More max HP and safer mistakes.";
        }
        if (label.startsWith("Armor")) {
            return "Reduces incoming damage permanently.";
        }
        if (label.startsWith("Speed")) {
            return "Move faster through arrows and runes.";
        }
        return "Slash more often; best with damage upgrades.";
    }

    private void applyUpgrade(int index) {
        player.applyUpgrade(options[index]);
        gsm.pop();
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
