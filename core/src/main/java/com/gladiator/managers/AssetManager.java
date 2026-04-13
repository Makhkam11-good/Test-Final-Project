package com.gladiator.managers;

/**
 * AssetManager (Singleton) - единственный экземпляр, управляет загрузкой и кэшированием всех ресурсов.
 */
public class AssetManager {
    private static AssetManager instance;

    // Приватный конструктор - запрещаем создание экземпляров извне
    private AssetManager() {
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
     * Загрузить все игровые ресурсы (спрайты, звуки, фонты).
     */
    public void loadAll() {
        // TODO: Реализовать загрузку текстур, звуков, фонтов в Фазе 9
    }

    /**
     * Освободить все загруженные ресурсы при выходе.
     */
    public void dispose() {
        // TODO: Вызвать dispose() для всех Texture, Sound, Music объектов в Фазе 9
    }
}
