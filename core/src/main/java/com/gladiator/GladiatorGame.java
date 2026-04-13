package com.gladiator;

import com.badlogic.gdx.Game;
import com.gladiator.managers.AssetManager;
import com.gladiator.managers.GameStateManager;
import com.gladiator.screens.MenuScreen;
import com.gladiator.screens.GameScreen;
import com.gladiator.screens.UpgradeScreen;
import com.gladiator.screens.GameOverScreen;
import com.gladiator.screens.VictoryScreen;

/**
 * Main game class - сердце приложения, инициализирует менеджеры и экраны.
 */
public class GladiatorGame extends Game {
    
    private GameStateManager gsm;

    @Override
    public void create() {
        // Загружаем все игровые ресурсы через Singleton AssetManager
        AssetManager.getInstance().loadAll();
        
        // Создаём менеджер состояний
        gsm = new GameStateManager(this);
        
        // Регистрируем все экраны
        gsm.registerScreen(GameStateManager.State.MENU, new MenuScreen(gsm));
        gsm.registerScreen(GameStateManager.State.GAME, new GameScreen(gsm));
        gsm.registerScreen(GameStateManager.State.UPGRADE, new UpgradeScreen(gsm));
        gsm.registerScreen(GameStateManager.State.GAME_OVER, new GameOverScreen(gsm));
        gsm.registerScreen(GameStateManager.State.VICTORY, new VictoryScreen(gsm));
        
        // Переходим на начальный экран (MenuScreen)
        gsm.set(GameStateManager.State.MENU);
    }

    @Override
    public void dispose() {
        // Освобождаем все ресурсы
        if (screen != null) {
            screen.dispose();
        }
        AssetManager.getInstance().dispose();
    }
}
