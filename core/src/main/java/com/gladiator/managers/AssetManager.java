package com.gladiator.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

/**
 * AssetManager (Singleton) loads core textures and creates deterministic
 * procedural animation frames for the arena.
 */
public class AssetManager implements Disposable {
    public static final String TEX_BACKGROUND = "background";
    public static final String TEX_PLAYER = "player";
    public static final String TEX_SLIME = "slime";
    public static final String TEX_GOBLIN = "goblin";
    public static final String TEX_BOSS = "boss";

    public static final String ANIM_PLAYER_IDLE = "anim.player.idle";
    public static final String ANIM_PLAYER_RUN = "anim.player.run";
    public static final String ANIM_PLAYER_ATTACK = "anim.player.attack";
    public static final String ANIM_PLAYER_DEAD = "anim.player.dead";
    public static final String ANIM_SLIME = "anim.enemy.slime";
    public static final String ANIM_GOBLIN = "anim.enemy.goblin";
    public static final String ANIM_ARCHER = "anim.enemy.archer";
    public static final String ANIM_BRUTE = "anim.enemy.brute";
    public static final String ANIM_BOSS = "anim.enemy.boss";

    private static AssetManager instance;

    private final Map<String, Texture> textures = new HashMap<>();
    private final Map<String, TextureRegion[]> animations = new HashMap<>();
    private final List<Texture> generatedTextures = new ArrayList<>();
    private final Texture pixel;
    private boolean loaded;

    private AssetManager() {
        pixel = createPixel();
        loaded = false;
    }

    public static AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }

    public void loadAll() {
        loadTexture(TEX_BACKGROUND, "backgrounds/arena.png");
        createProceduralAnimations();
        loaded = true;
    }

    private void loadTexture(String key, String path) {
        if (Gdx.files.internal(path).exists()) {
            textures.put(key, new Texture(Gdx.files.internal(path)));
        } else {
            Gdx.app.log("AssetManager", "Missing texture: " + path);
        }
    }

    public Texture getTexture(String key) {
        return textures.get(key);
    }

    public TextureRegion[] getAnimation(String key) {
        return animations.get(key);
    }

    public TextureRegion getAnimationFrame(String key, float stateTime, float frameDuration) {
        TextureRegion[] frames = animations.get(key);
        if (frames == null || frames.length == 0) {
            return null;
        }
        int frame = (int) (stateTime / frameDuration) % frames.length;
        return frames[frame];
    }

    public Texture getPixel() {
        return pixel;
    }

    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void dispose() {
        for (Texture texture : textures.values()) {
            if (texture != null) {
                texture.dispose();
            }
        }
        textures.clear();
        for (Texture texture : generatedTextures) {
            if (texture != null) {
                texture.dispose();
            }
        }
        generatedTextures.clear();
        animations.clear();
        if (pixel != null) {
            pixel.dispose();
        }
    }

    private Texture createPixel() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1f, 1f, 1f, 1f);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void createProceduralAnimations() {
        animations.clear();
        for (Texture texture : generatedTextures) {
            texture.dispose();
        }
        generatedTextures.clear();

        createAnimation(ANIM_PLAYER_IDLE, 48, 64, 4, this::paintPlayerIdle);
        createAnimation(ANIM_PLAYER_RUN, 48, 64, 6, this::paintPlayerRun);
        createAnimation(ANIM_PLAYER_ATTACK, 64, 64, 4, this::paintPlayerAttack);
        createAnimation(ANIM_PLAYER_DEAD, 64, 40, 1, this::paintPlayerDead);
        createAnimation(ANIM_SLIME, 40, 34, 4, this::paintSlime);
        createAnimation(ANIM_GOBLIN, 42, 54, 4, this::paintGoblin);
        createAnimation(ANIM_ARCHER, 42, 54, 4, this::paintArcher);
        createAnimation(ANIM_BRUTE, 56, 62, 4, this::paintBrute);
        createAnimation(ANIM_BOSS, 96, 96, 6, this::paintBoss);
    }

    private void createAnimation(String key, int width, int height, int frameCount, FramePainter painter) {
        TextureRegion[] frames = new TextureRegion[frameCount];
        for (int frame = 0; frame < frameCount; frame++) {
            Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
            pixmap.setBlending(Pixmap.Blending.None);
            pixmap.setColor(0f, 0f, 0f, 0f);
            pixmap.fill();
            pixmap.setBlending(Pixmap.Blending.SourceOver);
            painter.paint(pixmap, frame, width, height);
            Texture texture = new Texture(pixmap);
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            pixmap.dispose();
            generatedTextures.add(texture);
            frames[frame] = new TextureRegion(texture);
        }
        animations.put(key, frames);
    }

    private interface FramePainter {
        void paint(Pixmap pixmap, int frame, int width, int height);
    }

    private void paintPlayerIdle(Pixmap p, int frame, int width, int height) {
        int bob = frame == 1 || frame == 2 ? 1 : 0;
        paintGladiator(p, 24, 35 - bob, frame, false, false);
    }

    private void paintPlayerRun(Pixmap p, int frame, int width, int height) {
        paintGladiator(p, 24, 35, frame, true, false);
    }

    private void paintPlayerAttack(Pixmap p, int frame, int width, int height) {
        paintGladiator(p, 25, 35, frame, false, true);
        p.setColor(1f, 0.78f, 0.26f, frame == 1 || frame == 2 ? 0.9f : 0.45f);
        p.drawLine(37, 15 + frame * 2, 58, 3 + frame * 7);
        p.drawLine(38, 16 + frame * 2, 59, 4 + frame * 7);
        p.setColor(1f, 0.25f, 0.12f, 0.4f);
        p.drawLine(39, 18 + frame * 2, 61, 6 + frame * 7);
    }

    private void paintPlayerDead(Pixmap p, int frame, int width, int height) {
        p.setColor(0.05f, 0.04f, 0.04f, 0.45f);
        p.fillCircle(32, 34, 20);
        p.setColor(0.42f, 0.35f, 0.25f, 1f);
        p.fillRectangle(16, 18, 34, 9);
        p.setColor(0.8f, 0.78f, 0.65f, 1f);
        p.fillRectangle(20, 14, 24, 9);
        p.setColor(0.84f, 0.62f, 0.31f, 1f);
        p.fillCircle(16, 22, 5);
        p.setColor(0.9f, 0.9f, 0.78f, 1f);
        p.drawLine(42, 12, 57, 6);
        p.drawLine(43, 13, 58, 7);
    }

    private void paintGladiator(Pixmap p, int cx, int cy, int frame, boolean running, boolean attacking) {
        int stride = running ? (frame % 3) - 1 : 0;
        p.setColor(0.05f, 0.04f, 0.04f, 0.38f);
        p.fillCircle(cx, 55, 14);

        p.setColor(0.23f, 0.13f, 0.09f, 1f);
        p.fillRectangle(cx - 9 - stride, cy + 14, 5, 18);
        p.fillRectangle(cx + 4 + stride, cy + 14, 5, 18);
        p.setColor(0.78f, 0.57f, 0.34f, 1f);
        p.fillRectangle(cx - 10 - stride, cy + 7, 7, 10);
        p.fillRectangle(cx + 3 + stride, cy + 7, 7, 10);

        p.setColor(0.35f, 0.12f, 0.1f, 1f);
        p.fillTriangle(cx - 13, cy - 8, cx + 13, cy - 8, cx, cy + 20);
        p.setColor(0.85f, 0.75f, 0.58f, 1f);
        p.fillRectangle(cx - 10, cy - 2, 20, 20);
        p.setColor(0.68f, 0.42f, 0.18f, 1f);
        p.drawRectangle(cx - 10, cy - 2, 20, 20);
        p.setColor(0.95f, 0.72f, 0.42f, 1f);
        p.fillCircle(cx, cy - 11, 9);
        p.setColor(0.58f, 0.56f, 0.55f, 1f);
        p.fillRectangle(cx - 11, cy - 19, 22, 9);
        p.setColor(0.85f, 0.8f, 0.72f, 1f);
        p.fillRectangle(cx - 8, cy - 22, 16, 4);
        p.setColor(0.85f, 0.08f, 0.05f, 1f);
        p.fillRectangle(cx - 2, cy - 31, 4, 10);
        p.setColor(0.1f, 0.08f, 0.07f, 1f);
        p.fillRectangle(cx - 4, cy - 11, 2, 2);
        p.fillRectangle(cx + 3, cy - 11, 2, 2);

        p.setColor(0.55f, 0.15f, 0.11f, 1f);
        p.fillCircle(cx - 15, cy + 5, 8);
        p.setColor(0.9f, 0.75f, 0.5f, 1f);
        p.drawCircle(cx - 15, cy + 5, 8);

        if (!attacking) {
            p.setColor(0.92f, 0.9f, 0.78f, 1f);
            p.fillRectangle(cx + 14, cy - 2, 4, 24);
            p.fillTriangle(cx + 12, cy - 3, cx + 20, cy - 3, cx + 16, cy - 11);
        }
    }

    private void paintSlime(Pixmap p, int frame, int width, int height) {
        int squash = frame == 1 || frame == 2 ? 2 : 0;
        p.setColor(0.02f, 0.03f, 0.02f, 0.35f);
        p.fillCircle(20, 29, 14);
        p.setColor(0.13f, 0.65f, 0.32f, 1f);
        p.fillCircle(20, 18 + squash, 15);
        p.fillRectangle(7, 18 + squash, 26, 10 - squash);
        p.setColor(0.42f, 0.95f, 0.54f, 0.8f);
        p.fillCircle(14, 12 + squash, 4);
        p.setColor(0.05f, 0.16f, 0.08f, 1f);
        p.fillCircle(14, 18 + squash, 2);
        p.fillCircle(25, 18 + squash, 2);
    }

    private void paintGoblin(Pixmap p, int frame, int width, int height) {
        int bob = frame % 2;
        p.setColor(0.04f, 0.04f, 0.03f, 0.4f);
        p.fillCircle(21, 48, 14);
        p.setColor(0.42f, 0.69f, 0.22f, 1f);
        p.fillRectangle(14, 25 - bob, 14, 18);
        p.fillCircle(21, 17 - bob, 10);
        p.setColor(0.31f, 0.48f, 0.14f, 1f);
        p.fillTriangle(11, 18 - bob, 0, 14 - bob, 12, 12 - bob);
        p.fillTriangle(31, 18 - bob, 42, 14 - bob, 30, 12 - bob);
        p.setColor(0.36f, 0.16f, 0.09f, 1f);
        p.fillRectangle(11, 35 - bob, 20, 9);
        p.setColor(0.1f, 0.08f, 0.05f, 1f);
        p.fillRectangle(13, 44, 6, 8);
        p.fillRectangle(23, 44, 6, 8);
        p.setColor(0.88f, 0.88f, 0.72f, 1f);
        p.drawLine(28, 28 - bob, 38, 15 - bob);
        p.drawLine(29, 29 - bob, 39, 16 - bob);
        p.setColor(0.95f, 0.82f, 0.52f, 1f);
        p.fillRectangle(17, 20 - bob, 9, 3);
    }

    private void paintArcher(Pixmap p, int frame, int width, int height) {
        int pull = frame % 3;
        p.setColor(0.03f, 0.03f, 0.03f, 0.4f);
        p.fillCircle(21, 48, 13);
        p.setColor(0.11f, 0.26f, 0.23f, 1f);
        p.fillTriangle(21, 5, 9, 28, 33, 28);
        p.fillRectangle(12, 25, 18, 20);
        p.setColor(0.74f, 0.62f, 0.42f, 1f);
        p.fillCircle(21, 18, 7);
        p.setColor(0.05f, 0.07f, 0.06f, 1f);
        p.fillRectangle(15, 15, 12, 7);
        p.setColor(0.67f, 0.45f, 0.22f, 1f);
        p.drawCircle(32, 25, 12);
        p.setColor(0.95f, 0.9f, 0.65f, 1f);
        p.drawLine(19, 27, 34 - pull, 25);
        p.drawLine(34 - pull, 25, 41, 25);
        p.setColor(0.07f, 0.09f, 0.08f, 1f);
        p.fillRectangle(13, 43, 5, 9);
        p.fillRectangle(24, 43, 5, 9);
    }

    private void paintBrute(Pixmap p, int frame, int width, int height) {
        int bob = frame % 2;
        p.setColor(0.04f, 0.03f, 0.03f, 0.45f);
        p.fillCircle(28, 55, 18);
        p.setColor(0.44f, 0.15f, 0.12f, 1f);
        p.fillRectangle(16, 24 - bob, 24, 25);
        p.setColor(0.7f, 0.35f, 0.2f, 1f);
        p.fillCircle(28, 18 - bob, 12);
        p.setColor(0.25f, 0.22f, 0.2f, 1f);
        p.fillRectangle(14, 25 - bob, 28, 9);
        p.fillRectangle(13, 10 - bob, 30, 7);
        p.setColor(0.75f, 0.72f, 0.62f, 1f);
        p.fillTriangle(16, 12 - bob, 5, 9 - bob, 15, 18 - bob);
        p.fillTriangle(40, 12 - bob, 51, 9 - bob, 41, 18 - bob);
        p.setColor(0.12f, 0.06f, 0.05f, 1f);
        p.fillRectangle(18, 49, 8, 12);
        p.fillRectangle(31, 49, 8, 12);
        p.setColor(0.85f, 0.7f, 0.38f, 1f);
        p.drawRectangle(16, 24 - bob, 24, 25);
    }

    private void paintBoss(Pixmap p, int frame, int width, int height) {
        int pulse = frame % 3;
        p.setColor(0.03f, 0.01f, 0.03f, 0.55f);
        p.fillCircle(48, 77, 31);
        p.setColor(0.24f, 0.07f, 0.27f, 1f);
        p.fillCircle(48, 42 + pulse, 28);
        p.setColor(0.42f, 0.16f, 0.52f, 1f);
        p.fillCircle(48, 30 + pulse, 20);
        p.setColor(0.8f, 0.68f, 0.92f, 1f);
        p.fillTriangle(30, 24 + pulse, 18, 9 + pulse, 38, 20 + pulse);
        p.fillTriangle(66, 24 + pulse, 78, 9 + pulse, 58, 20 + pulse);
        p.setColor(0.11f, 0.03f, 0.12f, 1f);
        p.fillCircle(39, 36 + pulse, 5);
        p.fillCircle(57, 36 + pulse, 5);
        p.setColor(0.95f, 0.18f, 0.14f, 1f);
        p.fillCircle(39, 36 + pulse, 2);
        p.fillCircle(57, 36 + pulse, 2);
        p.setColor(0.36f, 0.11f, 0.4f, 1f);
        for (int i = 0; i < 4; i++) {
            int y = 44 + i * 9;
            p.drawLine(24, y + pulse, 3, y + 5 - i);
            p.drawLine(72, y + pulse, 93, y + 5 - i);
            p.drawLine(25, y + 1 + pulse, 4, y + 6 - i);
            p.drawLine(71, y + 1 + pulse, 92, y + 6 - i);
        }
        p.setColor(0.88f, 0.42f, 0.2f, 0.8f);
        p.drawCircle(48, 42 + pulse, 31);
        p.drawCircle(48, 42 + pulse, 32);
    }
}
