package com.gladiator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gladiator.managers.AssetManager;

/**
 * Main game class - сердце приложения, инициализирует AssetManager при старте.
 */
public class GladiatorGame extends ApplicationAdapter {

    @Override
    public void create() {
        // Загружаем все игровые ресурсы через Singleton AssetManager
        AssetManager.getInstance().loadAll();
    }

    @Override
    public void render() {
        // Очищаем экран чёрным цветом
        ScreenUtils.clear(0, 0, 0, 1);
    }

    @Override
    public void dispose() {
        // Освобождаем все ресурсы
        AssetManager.getInstance().dispose();
    }
}
