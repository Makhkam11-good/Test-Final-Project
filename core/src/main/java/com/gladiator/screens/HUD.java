package com.gladiator.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gladiator.entities.Boss;
import com.gladiator.entities.Player;

/**
 * HUD - управляет отрисовкой элементов интерфейса (HP бар, волна, счёт, апгрейды).
 * Фаза 9: улучшенный HUD с цветным HP баром и информацией об апгрейдах.
 */
public class HUD {
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont normalFont;
    private ShapeRenderer sr;
    private OrthographicCamera camera;

    public HUD(OrthographicCamera camera) {
        this.batch = new SpriteBatch();
        this.camera = camera;
        
        // Создаём два шрифта разного размера
        this.titleFont = new BitmapFont();
        titleFont.getRegion().getTexture().setFilter(
            com.badlogic.gdx.graphics.Texture.TextureFilter.Linear,
            com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
        );
        titleFont.setColor(Color.YELLOW);
        
        this.normalFont = new BitmapFont();
        normalFont.getRegion().getTexture().setFilter(
            com.badlogic.gdx.graphics.Texture.TextureFilter.Linear,
            com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
        );
        normalFont.setColor(Color.WHITE);
        
        this.sr = new ShapeRenderer();
    }

    /**
     * Рисует полный HUD при обычной игре.
     */
    public void render(Player player, int wave, int score, String difficulty, Boss boss) {
        batch.setProjectionMatrix(camera.combined);
        sr.setProjectionMatrix(camera.combined);
        
        // HP бар игрока (левый верхний угол)
        renderPlayerHPBar(player);
        
        // Текстовая информация
        batch.begin();
        normalFont.draw(batch,
            "HP: " + (int)player.hp + "/" + (int)player.getStats().getMaxHp(),
            10, 446);
        normalFont.draw(batch,
            "Wave: " + wave + "/10  Score: " + score +
            "  [" + difficulty + "]",
            10, 430);
        normalFont.draw(batch,
            "DMG:" + player.getStats().getDamage() +
            "  SPD:" + (int)player.getStats().getSpeed() +
            "  CD:" + String.format("%.1f", player.getStats().getAttackCooldown()),
            10, 414);
        batch.end();
        
        // HP бар Босса (если волна 10 и босс жив)
        if (boss != null && boss.alive && wave == 10) {
            renderBossHPBar(boss);
        }
    }

    /**
     * Рисует HP бар игрока внизу слева.
     */
    private void renderPlayerHPBar(Player player) {
        float hpRatio = player.hp / player.getStats().getMaxHp();
        
        // Фон HP бара
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.2f, 0.2f, 0.2f, 1f);
        sr.rect(10, 450, 200, 16);
        
        // Заливка HP: зелёный→оранжевый→красный
        if (hpRatio > 0.5f) {
            sr.setColor(Color.GREEN);
        } else if (hpRatio > 0.25f) {
            sr.setColor(Color.ORANGE);
        } else {
            sr.setColor(Color.RED);
        }
        sr.rect(10, 450, 200 * hpRatio, 16);
        sr.end();
        
        // Рамка HP бара
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.WHITE);
        sr.rect(10, 450, 200, 16);
        sr.end();
    }

    /**
     * Рисует HP бар Босса в центре верхней части экрана.
     */
    private void renderBossHPBar(Boss boss) {
        float hpRatio = boss.hp / boss.maxHp;
        
        // Фон HP бара
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.2f, 0.2f, 0.2f, 1f);
        sr.rect(200, 8, 400, 16);
        
        // Заливка HP: скарлет (красный)
        sr.setColor(Color.SCARLET);
        sr.rect(200, 8, 400 * hpRatio, 16);
        sr.end();
        
        // Рамка HP бара
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.WHITE);
        sr.rect(200, 8, 400, 16);
        sr.end();
        
        // Текст "DEMON KING"
        batch.begin();
        normalFont.setColor(Color.SCARLET);
        normalFont.draw(batch,
            "DEMON KING  " + (int)boss.hp + "/" + (int)boss.maxHp,
            320, 36);
        normalFont.setColor(Color.WHITE);
        batch.end();
    }

    /**
     * Очищает ресурсы.
     */
    public void dispose() {
        batch.dispose();
        titleFont.dispose();
        normalFont.dispose();
        sr.dispose();
    }
}