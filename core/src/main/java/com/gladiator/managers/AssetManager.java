package com.gladiator.managers;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * AssetManager (Singleton) - единственный экземпляр, управляет загрузкой и кэшированием всех ресурсов.
 * Использует асинхронный libGDX AssetManager для неблокирующей загрузки.
 */
public class AssetManager {
    private static AssetManager instance;

    // libGDX AssetManager для асинхронной загрузки
    private com.badlogic.gdx.assets.AssetManager manager;
    private Map<String, TextureRegion> regions;
    private Map<String, Animation<TextureRegion>> animations;
    private Map<String, AnimationSize> animationSizes;  // Информация о размерах каждой анимации
    private Music bgm;
    private Map<String, Sound> sounds;
    private boolean loaded;

    // Вспомогательный класс для хранения размеров анимации
    public static class AnimationSize {
        public float width;
        public float height;
        public AnimationSize(float width, float height) {
            this.width = width;
            this.height = height;
        }
    }

    // Приватный конструктор - запрещаем создание экземпляров извне
    private AssetManager() {
        manager = new com.badlogic.gdx.assets.AssetManager();
        regions = new HashMap<>();
        animations = new HashMap<>();
        animationSizes = new HashMap<>();
        sounds = new HashMap<>();
        loaded = false;
    }

    /**
     * Получить единственный экземпляр AssetManager.
     */
    public static AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }

    /**
     * Загрузить все игровые ресурсы в очередь (асинхронно).
     * Вызывается один раз в GladiatorGame.create().
     */
    public void loadAll() {
        // Поставь в очередь загрузку всех текстур (если они существуют)
        tryLoadTexture("sprites/player/idle.png");
        tryLoadTexture("sprites/player/run.png");
        tryLoadTexture("sprites/player/attack.png");
        tryLoadTexture("sprites/player/dead.png");
        tryLoadTexture("sprites/enemies/slime.png");
        tryLoadTexture("sprites/enemies/goblin.png");
        tryLoadTexture("sprites/enemies/boss.png");
        tryLoadTexture("backgrounds/arena.png");
        tryLoadTexture("ui/hp_bar_bg.png");
        tryLoadTexture("ui/hp_bar_fill.png");
        tryLoadTexture("ui/card_bg.png");

        // Загружаем звуки сразу (не асинхронно)
        loadSounds();
        
        Gdx.app.log("AssetManager", "loadAll() - все ресурсы поставлены в очередь");
    }

    /**
     * Попытаться загрузить текстуру, если она существует.
     */
    private void tryLoadTexture(String path) {
        try {
            if (Gdx.files.internal(path).exists()) {
                manager.load(path, Texture.class);
                Gdx.app.log("AssetManager", "Queued texture: " + path);
            } else {
                Gdx.app.log("AssetManager", "Texture not found: " + path + " (will use fallback)");
            }
        } catch (Exception e) {
            Gdx.app.log("AssetManager", "Could not queue texture " + path + ": " + e.getMessage());
        }
    }

    /**
     * Обновить статус загрузки. Вызывать в GladiatorGame.render().
     * @return true если загрузка завершена, false если идёт
     */
    public boolean update() {
        try {
            if (manager.update()) {
                if (!loaded) {
                    loaded = true;
                    finishLoading();
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            // Если ошибка при загрузке - логируем и помечаем как загружено (используем fallbacks)
            Gdx.app.error("AssetManager", "Error during asset loading: " + e.getMessage());
            if (!loaded) {
                loaded = true;
                finishLoading();
            }
            return true;
        }
    }

    /**
     * Завершить загрузку - нарезать анимации, загрузить звуки.
     */
    private void finishLoading() {
        // Нарезка спрайт-листов на кадры анимации (автоматическая подгонка размеров)
        buildAnimationAuto("player_idle", "sprites/player/idle.png", 4, 0.15f);
        buildAnimationAuto("player_run", "sprites/player/run.png", 6, 0.10f);
        buildAnimationAuto("player_attack", "sprites/player/attack.png", 6, 0.08f);
        buildAnimationAuto("player_dead", "sprites/player/dead.png", 4, 0.12f);
        buildAnimationAuto("slime_walk", "sprites/enemies/slime.png", 4, 0.15f);
        buildAnimationAuto("goblin_walk", "sprites/enemies/goblin.png", 6, 0.10f);
        buildAnimationAuto("boss_walk", "sprites/enemies/boss.png", 6, 0.12f);

        // Загрузи звуки
        loadSounds();

        Gdx.app.log("AssetManager", "finishLoading() - все ресурсы загружены!");
    }

    /**
     * Автоматически определить размеры и построить анимацию из спрайт-листа.
     * Пробует все возможные раскладки и выбирает наиболее подходящую.
     * @param key ключ для хранения анимации
     * @param path путь к файлу спрайт-листа
     * @param frameCount количество кадров в анимации
     * @param duration длительность одного кадра (в секундах)
     */
    private void buildAnimationAuto(String key, String path, int frameCount, float duration) {
        try {
            // Проверяем есть ли текстура в менеджере
            if (!manager.contains(path, Texture.class)) {
                Gdx.app.log("AssetManager", "Texture not loaded: " + path + " (animation skipped)");
                return;
            }
            
            Texture sheet = manager.get(path, Texture.class);
            if (sheet == null) {
                Gdx.app.error("AssetManager", "Texture is null: " + path);
                return;
            }

            int sheetWidth = sheet.getWidth();
            int sheetHeight = sheet.getHeight();
            
            // Определяем размер одного кадра автоматически
            // Пробуем все делители frameCount и выбираем наиболее подходящую раскладку
            int frameW = 32, frameH = 32; // default
            float bestScore = Float.MAX_VALUE;
            
            // Пробуем все делители frameCount
            for (int cols = 1; cols <= frameCount; cols++) {
                if (frameCount % cols == 0) {
                    int rows = frameCount / cols;
                    
                    // Пробуем эту раскладку
                    int possibleW = sheetWidth / cols;
                    int possibleH = sheetHeight / rows;
                    
                    // Проверяем что размеры разумные (минимум 16x16)
                    if (possibleW >= 16 && possibleH >= 16) {
                        // Оцениваем как хорошо эта раскладка подходит
                        // Предпочитаем кадры которые примерно квадратные или чуть выше чем в ширину
                        float aspectRatio = Math.abs((float)possibleW / possibleH - 0.8f);
                        
                        if (aspectRatio < bestScore) {
                            bestScore = aspectRatio;
                            frameW = possibleW;
                            frameH = possibleH;
                        }
                    }
                }
            }
            
            // Логируем вычисленную раскладку
            int cols = sheetWidth / frameW;
            int rows = sheetHeight / frameH;
            Gdx.app.log("AssetManager", "Layout: " + key + " -> " + cols + "x" + rows + " grid, frame size: " + frameW + "x" + frameH);

            // Разбиваем спрайт-лист
            TextureRegion[][] tmp = TextureRegion.split(sheet, frameW, frameH);
            if (tmp == null || tmp.length == 0) {
                Gdx.app.error("AssetManager", "Failed to split texture: " + path);
                return;
            }
            
            // Собираем все кадры из всех рядов
            TextureRegion[] anim = new TextureRegion[frameCount];
            int frameIndex = 0;
            for (int row = 0; row < tmp.length && frameIndex < frameCount; row++) {
                for (int col = 0; col < tmp[row].length && frameIndex < frameCount; col++) {
                    anim[frameIndex] = tmp[row][col];
                    frameIndex++;
                }
            }
            
            Animation<TextureRegion> animation = new Animation<>(duration, anim);
            animation.setPlayMode(Animation.PlayMode.LOOP);
            animations.put(key, animation);
            
            // Сохраняем размеры этой анимации
            animationSizes.put(key, new AnimationSize(frameW, frameH));

            Gdx.app.log("AssetManager", "Built animation: " + key + " (" + frameCount + " frames, " + frameW + "x" + frameH + ")");
        } catch (Exception e) {
            Gdx.app.error("AssetManager", "Error building animation " + key + ": " + e.getMessage());
        }
    }

    /**
     * Загрузить все звуки.
     */
    private void loadSounds() {
        try {
            // Попытаемся загрузить BGM если существует
            if (Gdx.files.internal("audio/music/game_bgm.mp3").exists()) {
                bgm = Gdx.audio.newMusic(Gdx.files.internal("audio/music/game_bgm.mp3"));
                bgm.setLooping(true);
                bgm.setVolume(0.5f);
                Gdx.app.log("AssetManager", "Loaded BGM");
            } else {
                Gdx.app.log("AssetManager", "BGM not found, will use null (silence)");
                bgm = null;
            }
        } catch (Exception e) {
            Gdx.app.log("AssetManager", "Error loading BGM: " + e.getMessage());
            bgm = null;
        }

        // Загружаем звуки эффектов
        tryLoadSound("hit", "audio/sounds/hit.wav");
        tryLoadSound("enemy_death", "audio/sounds/enemy_death.wav");
        tryLoadSound("upgrade", "audio/sounds/upgrade.wav");
        tryLoadSound("boss_roar", "audio/sounds/boss_roar.wav");

        Gdx.app.log("AssetManager", "Все доступные звуки загружены!");
    }

    /**
     * Попытаться загрузить звук, если он существует.
     */
    private void tryLoadSound(String key, String path) {
        try {
            if (Gdx.files.internal(path).exists()) {
                Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
                sounds.put(key, sound);
                Gdx.app.log("AssetManager", "Loaded sound: " + key);
            } else {
                Gdx.app.log("AssetManager", "Sound not found: " + path);
            }
        } catch (Exception e) {
            Gdx.app.log("AssetManager", "Error loading sound " + key + ": " + e.getMessage());
        }
    }

    /**
     * Получить анимацию по ключу.
     * @return анимация или null если не найдена
     */
    public Animation<TextureRegion> getAnimation(String key) {
        return animations.getOrDefault(key, null);
    }

    /**
     * Получить размеры кадра для анимации.
     * @return объект с шириной и высотой, или null если анимация не найдена
     */
    public AnimationSize getAnimationSize(String key) {
        return animationSizes.getOrDefault(key, null);
    }

    /**
     * Получить текстуру по пути.
     * @return текстура или null если не загружена
     */
    public Texture getTexture(String path) {
        try {
            return manager.get(path, Texture.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Получить звук по ключу.
     * @return звук или null если не найден
     */
    public Sound getSound(String key) {
        return sounds.getOrDefault(key, null);
    }

    /**
     * Получить фоновую музыку.
     */
    public Music getBgm() {
        return bgm;
    }

    /**
     * Получить главный libGDX AssetManager.
     */
    public com.badlogic.gdx.assets.AssetManager getManager() {
        return manager;
    }

    /**
     * Проверить завершена ли загрузка всех ресурсов.
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Освободить все загруженные ресурсы при выходе.
     */
    public void dispose() {
        if (bgm != null) {
            bgm.dispose();
        }
        for (Sound sound : sounds.values()) {
            if (sound != null) {
                sound.dispose();
            }
        }
        manager.dispose();
        Gdx.app.log("AssetManager", "dispose() - все ресурсы освобождены");
    }
}
