package com.gladiator;

import com.badlogic.gdx.Game;
import com.gladiator.managers.AudioManager;
import com.gladiator.managers.AssetManager;
import com.gladiator.managers.GameStateManager;
import com.gladiator.screens.GameOverScreen;
import com.gladiator.screens.GameScreen;
import com.gladiator.screens.MenuScreen;
import com.gladiator.screens.VictoryScreen;

/**
 * Main game class initializes managers and screens.
 */
public class GladiatorGame extends Game {
    
    private GameStateManager gsm;

    @Override
    public void create() {
        gsm = new GameStateManager(this);

        gsm.registerScreen(GameStateManager.State.MENU, new MenuScreen(gsm));
        gsm.registerScreen(GameStateManager.State.GAME, new GameScreen(gsm));
        gsm.registerScreen(GameStateManager.State.GAME_OVER, new GameOverScreen(gsm));
        gsm.registerScreen(GameStateManager.State.VICTORY, new VictoryScreen(gsm));

        AssetManager.getInstance().loadAll();
        AudioManager.getInstance().attachToEvents();
        gsm.set(GameStateManager.State.MENU);
    }

    @Override
    public void dispose() {
        if (screen != null) {
            screen.dispose();
        }
        AssetManager.getInstance().dispose();
        AudioManager.getInstance().dispose();
    }
}
