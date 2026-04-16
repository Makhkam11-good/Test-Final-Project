package com.gladiator;

import com.badlogic.gdx.Game;
import com.gladiator.managers.AssetManager;
import com.gladiator.managers.GameStateManager;
import com.gladiator.screens.GameOverScreen;
import com.gladiator.screens.GameScreen;
import com.gladiator.screens.LoadingScreen;
import com.gladiator.screens.MenuScreen;
import com.gladiator.screens.VictoryScreen;

/**
 * Main game class - сердце приложения, инициализирует менеджеры и экраны.
 */
public class GladiatorGame extends Game {
    
    private GameStateManager gsm;

    @Override
    public void create() {
        // Создаём менеджер состояний перед загрузкой ресурсов
        gsm = new GameStateManager(this);
        
        // Регистрируем все экраны, включая LoadingScreen
        gsm.registerScreen(GameStateManager.State.LOADING, new LoadingScreen(gsm));
        gsm.registerScreen(GameStateManager.State.MENU, new MenuScreen(gsm));
        gsm.registerScreen(GameStateManager.State.GAME, new GameScreen(gsm));
        // Фаза 7: UpgradeScreen создаётся динамически в GameScreen.onWaveCleared с текущим player
        // gsm.registerScreen(GameStateManager.State.UPGRADE, new UpgradeScreen(gsm));
        gsm.registerScreen(GameStateManager.State.GAME_OVER, new GameOverScreen(gsm));
        gsm.registerScreen(GameStateManager.State.VICTORY, new VictoryScreen(gsm));
        
        // Все ресурсы ставятся в очередь загрузки (не блокирует)
        AssetManager.getInstance().loadAll();
        
        // Переходим на экран загрузки (асинхронной)
        gsm.set(GameStateManager.State.LOADING);
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
