package com.gladiator.screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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

/**
 * UpgradeScreen - экран выбора апгрейда после каждой волны (показывает 3 карточки).
 * Фаза 7: полная реализация с Decorator паттерном.
 */
public class UpgradeScreen implements Screen {
    
    private GameStateManager gsm;
    private Player player;
    private BitmapFont font;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private List<PlayerDecorator> options;  // 3 случайных апгрейда

    public UpgradeScreen(GameStateManager gsm, Player player) {
        this.gsm = gsm;
        this.player = player;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        shapeRenderer = new ShapeRenderer();
        
        // Создаём список всех 6 возможных декораторов
        List<PlayerDecorator> allUpgrades = new ArrayList<>();
        allUpgrades.add(new FireWeaponDecorator(player.getStats()));
        allUpgrades.add(new PoisonDecorator(player.getStats()));
        allUpgrades.add(new ShieldDecorator(player.getStats()));
        allUpgrades.add(new ArmorDecorator(player.getStats()));
        allUpgrades.add(new SpeedBootsDecorator(player.getStats()));
        allUpgrades.add(new AttackSpeedDecorator(player.getStats()));
        
        // Перемешиваем и берём первые 3
        Collections.shuffle(allUpgrades);
        options = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            options.add(allUpgrades.get(i));
        }
    }

    @Override
    public void render(float delta) {
        // Очищаем экран тёмно-фиолетовым цветом
        ScreenUtils.clear(0.1f, 0.0f, 0.15f, 1);
        
        // Рисуем заголовок
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "CHOOSE YOUR UPGRADE", 250, 450);
        font.draw(batch, "Wave " + GameManager.getInstance().getCurrentWave() + " cleared!", 300, 420);
        batch.end();
        
        // Рисуем 3 карточки
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1);  // Тёмно-серый
        
        // Координаты карточек
        float[][] cardPositions = {
            {60, 150, 200, 160},   // Карточка 1: x, y, width, height
            {300, 150, 200, 160},  // Карточка 2
            {540, 150, 200, 160}   // Карточка 3
        };
        
        // Рисуем фоны карточек
        for (float[] card : cardPositions) {
            shapeRenderer.rect(card[0], card[1], card[2], card[3]);
        }
        shapeRenderer.end();
        
        // Рисуем рамки карточек
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        for (float[] card : cardPositions) {
            shapeRenderer.rect(card[0], card[1], card[2], card[3]);
        }
        shapeRenderer.end();
        
        // Рисуем текст на карточках и подсказки выбора
        batch.begin();
        font.setColor(Color.WHITE);
        
        for (int i = 0; i < options.size(); i++) {
            PlayerDecorator dec = options.get(i);
            float[] card = cardPositions[i];
            
            // Название апгрейда в центре карточки
            String description = dec.getDescription();
            float textY = card[1] + card[3] - 30;  // Верхняя часть карточки
            font.draw(batch, description, card[0] + 10, textY);
            
            // Подсказка выбора внизу карточки
            String hint = "[" + (i + 1) + "]";
            font.draw(batch, hint, card[0] + 75, card[1] + 20);
        }
        
        // Подсказка внизу экрана
        font.draw(batch, "Press 1, 2 or 3 to choose", 220, 80);
        batch.end();
        
        // Обработка ввода
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            applyUpgrade(0);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            applyUpgrade(1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            applyUpgrade(2);
        }
    }

    /**
     * Применяет выбранный апгрейд к игроку и возвращает в GameScreen.
     * Фаза 7: Decorator паттерн.
     */
    private void applyUpgrade(int index) {
        PlayerDecorator chosen = options.get(index);
        System.out.println("Player chose: " + chosen.getDescription());
        player.applyUpgrade(chosen);
        
        // Фаза 9: воспроизведи звук выбора апгрейда
        com.badlogic.gdx.audio.Sound upgradeSound = 
            com.gladiator.managers.AssetManager.getInstance().getSound("upgrade");
        if (upgradeSound != null) {
            upgradeSound.play(1.0f);
        }
        
        gsm.pop();  // Возвращаемся в GameScreen
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
        // Очищаем ресурсы
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
