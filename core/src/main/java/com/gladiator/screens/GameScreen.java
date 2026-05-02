package com.gladiator.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gladiator.commands.AttackCommand;
import com.gladiator.commands.CommandHistory;
import com.gladiator.commands.KeyboardInputHandler;
import com.gladiator.commands.PlayerInputHandler;
import com.gladiator.entities.Boss;
import com.gladiator.entities.Enemy;
import com.gladiator.entities.Player;
import com.gladiator.entities.Projectile;
import com.gladiator.events.EventBus;
import com.gladiator.events.EventListener;
import com.gladiator.events.GameEvent;
import com.gladiator.factories.ArcherFactory;
import com.gladiator.factories.BossFactory;
import com.gladiator.factories.BruteFactory;
import com.gladiator.factories.EnemyFactory;
import com.gladiator.factories.GoblinFactory;
import com.gladiator.factories.SlimeFactory;
import com.gladiator.managers.AssetManager;
import com.gladiator.managers.AudioManager;
import com.gladiator.managers.GameManager;
import com.gladiator.managers.GameStateManager;
import com.gladiator.managers.LevelManager;

public class GameScreen implements Screen {
    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 480f;
    private static final WavePlan[] WAVES = {
        new WavePlan("Gate Trial", 5, 0, 0, 0, 0.88f, false, false),
        new WavePlan("Slime Surge", 8, 1, 0, 0, 0.82f, false, false),
        new WavePlan("Goblin Rush", 5, 4, 0, 0, 0.78f, false, false),
        new WavePlan("Arrow Balcony", 4, 4, 2, 0, 0.72f, false, false),
        new WavePlan("Burning Sigils", 6, 5, 2, 0, 0.68f, true, false),
        new WavePlan("Iron Initiates", 4, 5, 2, 1, 0.64f, true, false),
        new WavePlan("Twin Ambush", 7, 7, 3, 1, 0.60f, true, false),
        new WavePlan("Moonlit Volley", 5, 7, 5, 1, 0.56f, true, false),
        new WavePlan("Shield Breakers", 4, 8, 4, 3, 0.52f, true, false),
        new WavePlan("Champion's Gauntlet", 8, 9, 4, 3, 0.48f, true, false),
        new WavePlan("Last Blood Moon", 9, 10, 5, 4, 0.44f, true, false),
        new WavePlan("Demon King's Court", 0, 0, 0, 0, 1.0f, true, true)
    };

    private final GameStateManager gsm;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final OrthographicCamera camera;
    private final StringBuilder hudBuilder = new StringBuilder(160);
    private final StringBuilder statsBuilder = new StringBuilder(160);

    private final EnemyFactory slimeFactory = new SlimeFactory();
    private final EnemyFactory goblinFactory = new GoblinFactory();
    private final EnemyFactory archerFactory = new ArcherFactory();
    private final EnemyFactory bruteFactory = new BruteFactory();
    private final EnemyFactory bossFactory = new BossFactory();

    private final CommandHistory commandHistory = new CommandHistory();
    private PlayerInputHandler inputHandler;
    private AttackCommand attackCommand;

    private Player player;
    private LevelManager levelManager;
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Projectile> projectiles = new ArrayList<>();
    private final ArrayList<Particle> particles = new ArrayList<>();
    private final ArrayList<FloatingText> floatingTexts = new ArrayList<>();
    private final ArrayList<HazardZone> hazards = new ArrayList<>();
    private Boss boss;

    private int pendingSlimes;
    private int pendingGoblins;
    private int pendingArchers;
    private int pendingBrutes;
    private float spawnTimer;
    private float spawnInterval;
    private float hazardTimer;
    private float waveIntroTimer;
    private float attackArcTimer;
    private float shakeTimer;
    private float shakePower;
    private boolean bossWave;
    private boolean needsWaveStart;

    private final EventListener waveClearedListener;
    private final EventListener playerDiedListener;
    private final EventListener bossDiedListener;
    private final EventListener enemyDiedListener;

    public GameScreen(GameStateManager gsm) {
        this.gsm = gsm;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);

        waveClearedListener = event -> onWaveCleared();
        playerDiedListener = event -> this.gsm.set(GameStateManager.State.GAME_OVER);
        bossDiedListener = event -> this.gsm.set(GameStateManager.State.VICTORY);
        enemyDiedListener = this::onEnemyDied;
    }

    @Override
    public void show() {
        if (player == null || GameManager.getInstance().getCurrentWave() == 1) {
            player = new Player();
        }

        commandHistory.clear();
        inputHandler = new KeyboardInputHandler(player, commandHistory);
        attackCommand = new AttackCommand(player);

        levelManager = new LevelManager();
        enemies.clear();
        projectiles.clear();
        particles.clear();
        floatingTexts.clear();
        hazards.clear();
        boss = null;
        bossWave = false;
        needsWaveStart = true;
        spawnTimer = 0f;
        hazardTimer = 0f;
        waveIntroTimer = 0f;
        attackArcTimer = 0f;
        shakeTimer = 0f;
        shakePower = 0f;
        AudioManager.getInstance().playMusic(AudioManager.MusicMode.ARENA);

        EventBus.getInstance().subscribe(GameEvent.Type.WAVE_CLEARED, waveClearedListener);
        EventBus.getInstance().subscribe(GameEvent.Type.PLAYER_DIED, playerDiedListener);
        EventBus.getInstance().subscribe(GameEvent.Type.BOSS_DIED, bossDiedListener);
        EventBus.getInstance().subscribe(GameEvent.Type.ENEMY_DIED, enemyDiedListener);
    }

    private void startWave(int wave) {
        WavePlan plan = getWavePlan(wave);
        enemies.clear();
        projectiles.clear();
        hazards.clear();
        boss = null;
        bossWave = plan.bossWave;
        waveIntroTimer = bossWave ? 3.0f : 2.1f;
        attackArcTimer = 0f;

        EventBus.getInstance().post(new GameEvent(GameEvent.Type.WAVE_STARTED, plan.name));
        if (bossWave) {
            boss = (Boss) bossFactory.create(360f, 350f);
            levelManager.startWave(1);
            AudioManager.getInstance().playMusic(AudioManager.MusicMode.BOSS);
            AudioManager.getInstance().playBossRoar();
            shake(0.8f, 7f);
            return;
        }

        AudioManager.getInstance().playMusic(AudioManager.MusicMode.ARENA);
        pendingSlimes = plan.slimes;
        pendingGoblins = plan.goblins;
        pendingArchers = plan.archers;
        pendingBrutes = plan.brutes;
        levelManager.startWave(plan.totalEnemies());
        float difficultySpawnFactor = GameManager.getInstance().getDifficulty().getSpawnInterval() / 1.5f;
        spawnInterval = plan.spawnInterval * difficultySpawnFactor;
        spawnTimer = 0.15f;
        hazardTimer = plan.hazards ? 2.8f : 999f;
    }

    private WavePlan getWavePlan(int wave) {
        int index = MathUtils.clamp(wave, 1, WAVES.length) - 1;
        return WAVES[index];
    }

    private void spawnNextEnemy() {
        int totalPending = pendingSlimes + pendingGoblins + pendingArchers + pendingBrutes;
        if (totalPending <= 0) {
            return;
        }

        int pick = MathUtils.random(totalPending - 1);
        EnemyFactory factory;
        if (pick < pendingSlimes) {
            pendingSlimes--;
            factory = slimeFactory;
        } else if (pick < pendingSlimes + pendingGoblins) {
            pendingGoblins--;
            factory = goblinFactory;
        } else if (pick < pendingSlimes + pendingGoblins + pendingArchers) {
            pendingArchers--;
            factory = archerFactory;
        } else {
            pendingBrutes--;
            factory = bruteFactory;
        }

        Enemy enemy = spawnAtEdge(factory);
        int wave = GameManager.getInstance().getCurrentWave();
        if (wave >= 9) {
            enemy.setSpeed(enemy.getSpeed() * 1.12f);
        }
        enemies.add(enemy);
        spawnBurst(enemy.getCenterX(), enemy.getCenterY(), 8, 0.9f, 0.55f, 0.25f);
    }

    private Enemy spawnAtEdge(EnemyFactory factory) {
        float x;
        float y;
        int edge = MathUtils.random(3);
        switch (edge) {
            case 0:
                x = MathUtils.random(20f, WORLD_WIDTH - 60f);
                y = WORLD_HEIGHT - 54f;
                break;
            case 1:
                x = MathUtils.random(20f, WORLD_WIDTH - 60f);
                y = 8f;
                break;
            case 2:
                x = 10f;
                y = MathUtils.random(20f, WORLD_HEIGHT - 80f);
                break;
            default:
                x = WORLD_WIDTH - 62f;
                y = MathUtils.random(20f, WORLD_HEIGHT - 80f);
                break;
        }
        return factory.create(x, y);
    }

    private void onWaveCleared() {
        if (bossWave) {
            return;
        }

        int nextWave = GameManager.getInstance().getCurrentWave() + 1;
        GameManager.getInstance().setCurrentWave(nextWave);

        if (nextWave <= WAVES.length) {
            needsWaveStart = true;
            UpgradeScreen upgradeScreen = new UpgradeScreen(gsm, player);
            gsm.registerScreen(GameStateManager.State.UPGRADE, upgradeScreen);
            gsm.push(GameStateManager.State.UPGRADE);
        }
    }

    private void onEnemyDied(GameEvent event) {
        Enemy enemy = event.getPayload(Enemy.class);
        if (enemy == null) {
            return;
        }
        spawnBurst(enemy.getCenterX(), enemy.getCenterY(), enemy instanceof Boss ? 42 : 18, 1f, 0.25f, 0.08f);
        addFloatingText("+" + getDeathRewardText(enemy), enemy.getCenterX(), enemy.getCenterY() + 18f,
            1f, 0.82f, 0.22f);
        shake(enemy instanceof Boss ? 0.65f : 0.16f, enemy instanceof Boss ? 8f : 2.6f);
    }

    private String getDeathRewardText(Enemy enemy) {
        if (enemy instanceof Boss) {
            return "1000";
        }
        if ("Iron Brute".equals(enemy.getTypeName())) {
            return "75";
        }
        if ("Arena Archer".equals(enemy.getTypeName())) {
            return "40";
        }
        if ("Goblin".equals(enemy.getTypeName())) {
            return "25";
        }
        return "10";
    }

    private void update(float delta) {
        inputHandler.update(delta);
        player.update(delta);
        GameManager.getInstance().addTime(delta);

        if (needsWaveStart) {
            startWave(GameManager.getInstance().getCurrentWave());
            needsWaveStart = false;
        }

        waveIntroTimer = Math.max(0f, waveIntroTimer - delta);
        attackArcTimer = Math.max(0f, attackArcTimer - delta);
        shakeTimer = Math.max(0f, shakeTimer - delta);

        if (player.consumeAttackReady()) {
            commandHistory.execute(attackCommand);
        }
        boolean attackTriggered = player.consumeAttackTriggered();
        if (attackTriggered) {
            attackArcTimer = 0.18f;
            spawnSlash(player.getCenterX(), player.getCenterY());
        }

        if (bossWave) {
            updateBoss(delta, attackTriggered);
        } else {
            updateEnemyWave(delta, attackTriggered);
        }

        updateProjectiles(delta);
        updateHazards(delta);
        updateEffects(delta);
    }

    private void updateBoss(float delta, boolean attackTriggered) {
        WavePlan plan = getWavePlan(GameManager.getInstance().getCurrentWave());
        if (boss != null && boss.isAlive()) {
            boss.setTarget(player.getCenterX(), player.getCenterY());
            boss.update(delta);

            if (boss.getBounds().overlaps(player.getBounds())) {
                if (boss.isDashing) {
                    if (!boss.isDashHitApplied()) {
                        player.takeDamage(40f, "Demon King Dash");
                        boss.setDashHitApplied(true);
                        shake(0.3f, 6f);
                    }
                } else {
                    player.takeDamage(boss.getContactDamage() * delta, "Demon King");
                }
            }

            if (attackTriggered && isInsidePlayerAttack(boss.getCenterX(), boss.getCenterY())) {
                damageEnemy(boss, player.getStats().getDamage());
            }
        }

        if (plan.hazards) {
            hazardTimer -= delta;
            if (hazardTimer <= 0f) {
                spawnHazard(MathUtils.random(120f, 680f), MathUtils.random(90f, 390f), 54f, 24f);
                hazardTimer = MathUtils.random(1.6f, 2.4f);
            }
        }
    }

    private void updateEnemyWave(float delta, boolean attackTriggered) {
        spawnTimer -= delta;
        if (pendingSlimes + pendingGoblins + pendingArchers + pendingBrutes > 0 && spawnTimer <= 0f) {
            spawnNextEnemy();
            spawnTimer = spawnInterval;
        }

        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            if (!enemy.isAlive()) {
                enemies.remove(i);
                continue;
            }

            enemy.setTarget(player.getCenterX(), player.getCenterY());
            enemy.update(delta);

            if (enemy.getBounds().overlaps(player.getBounds())) {
                player.takeDamage(enemy.getDamage() * delta, enemy.getTypeName());
            }

            if (enemy.consumeRangedAttackReady(player.getCenterX(), player.getCenterY())) {
                projectiles.add(new Projectile(enemy.getCenterX(), enemy.getCenterY(),
                    player.getCenterX(), player.getCenterY(), enemy.getProjectileSpeed(),
                    enemy.getProjectileDamage(), enemy.getTypeName(), 0.95f, 0.72f, 0.25f));
                spawnBurst(enemy.getCenterX(), enemy.getCenterY(), 4, 0.95f, 0.72f, 0.25f);
            }

            if (attackTriggered && isInsidePlayerAttack(enemy.getCenterX(), enemy.getCenterY())) {
                damageEnemy(enemy, player.getStats().getDamage());
            }
        }

        WavePlan plan = getWavePlan(GameManager.getInstance().getCurrentWave());
        if (plan.hazards) {
            hazardTimer -= delta;
            if (hazardTimer <= 0f) {
                float offsetX = MathUtils.random(-130f, 130f);
                float offsetY = MathUtils.random(-90f, 90f);
                float x = MathUtils.clamp(player.getCenterX() + offsetX, 80f, 720f);
                float y = MathUtils.clamp(player.getCenterY() + offsetY, 70f, 410f);
                spawnHazard(x, y, 42f, 16f);
                hazardTimer = MathUtils.random(3.2f, 4.8f);
            }
        }
    }

    private boolean isInsidePlayerAttack(float x, float y) {
        float dx = player.getCenterX() - x;
        float dy = player.getCenterY() - y;
        return (dx * dx + dy * dy) <= (Player.ATTACK_RADIUS * Player.ATTACK_RADIUS);
    }

    private void damageEnemy(Enemy enemy, int damage) {
        if (!enemy.isAlive()) {
            return;
        }
        enemy.takeDamage(damage);
        addFloatingText("-" + damage, enemy.getCenterX(), enemy.getCenterY() + 20f,
            1f, 0.9f, 0.52f);
        spawnBurst(enemy.getCenterX(), enemy.getCenterY(), 7, 1f, 0.8f, 0.25f);
        shake(0.09f, 1.8f);
    }

    private void updateProjectiles(float delta) {
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            projectile.update(delta);
            if (projectile.isAlive() && projectile.getBounds().overlaps(player.getBounds())) {
                player.takeDamage(projectile.getDamage(), projectile.getSourceName());
                spawnBurst(projectile.getX(), projectile.getY(), 10, 0.95f, 0.32f, 0.18f);
                projectile.destroy();
                shake(0.12f, 2.8f);
            }
            if (!projectile.isAlive()) {
                projectiles.remove(i);
            }
        }
    }

    private void updateHazards(float delta) {
        for (int i = hazards.size() - 1; i >= 0; i--) {
            HazardZone hazard = hazards.get(i);
            hazard.update(delta);
            if (hazard.isActive()) {
                float dx = player.getCenterX() - hazard.x;
                float dy = player.getCenterY() - hazard.y;
                if ((dx * dx + dy * dy) <= hazard.radius * hazard.radius) {
                    float damage = hazard.damagePerSecond * GameManager.getInstance().getDifficulty().getEnemyDamageMult();
                    player.takeDamage(damage * delta, "Arena flame rune");
                }
            }
            if (hazard.isDone()) {
                hazards.remove(i);
            }
        }
    }

    private void updateEffects(float delta) {
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle particle = particles.get(i);
            particle.update(delta);
            if (particle.life <= 0f) {
                particles.remove(i);
            }
        }
        for (int i = floatingTexts.size() - 1; i >= 0; i--) {
            FloatingText text = floatingTexts.get(i);
            text.update(delta);
            if (text.life <= 0f) {
                floatingTexts.remove(i);
            }
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        ScreenUtils.clear(0.02f, 0.025f, 0.035f, 1f);
        applyWorldCamera();

        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        batch.begin();
        drawBackground();
        batch.end();

        drawWorldShapes();

        batch.begin();
        if (bossWave && boss != null && boss.isAlive()) {
            boss.render(batch);
        } else {
            for (int i = 0; i < enemies.size(); i++) {
                enemies.get(i).render(batch);
            }
        }
        for (int i = 0; i < projectiles.size(); i++) {
            projectiles.get(i).render(batch);
        }
        player.render(batch);
        batch.end();

        drawForegroundEffects();

        resetHudCamera();
        drawHudBars();

        batch.begin();
        drawHudText();
        drawFloatingTexts();
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gsm.set(GameStateManager.State.GAME_OVER);
        }
    }

    private void drawBackground() {
        Texture bg = AssetManager.getInstance().getTexture(AssetManager.TEX_BACKGROUND);
        Texture pixel = AssetManager.getInstance().getPixel();
        if (bg != null) {
            batch.draw(bg, 0f, 0f, WORLD_WIDTH, WORLD_HEIGHT);
        } else if (pixel != null) {
            batch.setColor(0.13f, 0.14f, 0.16f, 1f);
            batch.draw(pixel, 0f, 0f, WORLD_WIDTH, WORLD_HEIGHT);
            batch.setColor(1f, 1f, 1f, 1f);
        }

        if (pixel != null) {
            batch.setColor(0.01f, 0.01f, 0.015f, 0.38f);
            batch.draw(pixel, 0f, 0f, WORLD_WIDTH, 52f);
            batch.draw(pixel, 0f, WORLD_HEIGHT - 58f, WORLD_WIDTH, 58f);
            batch.draw(pixel, 0f, 0f, 44f, WORLD_HEIGHT);
            batch.draw(pixel, WORLD_WIDTH - 44f, 0f, 44f, WORLD_HEIGHT);
            if (bossWave) {
                batch.setColor(0.45f, 0.04f, 0.08f, 0.18f);
                batch.draw(pixel, 0f, 0f, WORLD_WIDTH, WORLD_HEIGHT);
            }
            batch.setColor(1f, 1f, 1f, 1f);
        }
    }

    private void drawWorldShapes() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < hazards.size(); i++) {
            HazardZone hazard = hazards.get(i);
            float alpha = hazard.isActive() ? 0.34f : 0.16f + MathUtils.sin(hazard.age * 18f) * 0.07f;
            shapeRenderer.setColor(1f, 0.24f, 0.08f, alpha);
            shapeRenderer.circle(hazard.x, hazard.y, hazard.radius);
            shapeRenderer.setColor(1f, 0.7f, 0.1f, alpha * 0.45f);
            shapeRenderer.circle(hazard.x, hazard.y, hazard.radius * 0.58f);
        }
        if (attackArcTimer > 0f) {
            float alpha = attackArcTimer / 0.18f;
            shapeRenderer.setColor(1f, 0.82f, 0.25f, alpha * 0.18f);
            shapeRenderer.circle(player.getCenterX(), player.getCenterY(), Player.ATTACK_RADIUS);
            shapeRenderer.setColor(1f, 0.35f, 0.12f, alpha * 0.12f);
            shapeRenderer.circle(player.getCenterX(), player.getCenterY(), Player.ATTACK_RADIUS * 0.72f);
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.95f, 0.65f, 0.25f, 0.16f);
        shapeRenderer.circle(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 82f);
        shapeRenderer.circle(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 168f);
        for (int i = 0; i < hazards.size(); i++) {
            HazardZone hazard = hazards.get(i);
            shapeRenderer.setColor(1f, 0.9f, 0.4f, hazard.isActive() ? 0.55f : 0.3f);
            shapeRenderer.circle(hazard.x, hazard.y, hazard.radius);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawForegroundEffects() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).render(shapeRenderer);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawHudBars() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        float hpRatio = MathUtils.clamp(player.getHp() / player.getStats().getMaxHp(), 0f, 1f);
        float cooldown = player.getAttackCooldownProgress();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.05f, 0.045f, 0.04f, 0.88f);
        shapeRenderer.rect(8f, 438f, 304f, 34f);
        shapeRenderer.setColor(0.16f, 0.12f, 0.09f, 1f);
        shapeRenderer.rect(18f, 454f, 218f, 12f);
        shapeRenderer.setColor(0.95f * (1f - hpRatio), 0.2f + hpRatio * 0.65f, 0.12f, 1f);
        shapeRenderer.rect(18f, 454f, 218f * hpRatio, 12f);
        shapeRenderer.setColor(0.18f, 0.15f, 0.1f, 1f);
        shapeRenderer.rect(18f, 442f, 218f, 6f);
        shapeRenderer.setColor(1f, 0.75f, 0.22f, 1f);
        shapeRenderer.rect(18f, 442f, 218f * cooldown, 6f);
        shapeRenderer.end();

        if (bossWave && boss != null && boss.isAlive()) {
            float bossRatio = MathUtils.clamp(boss.getHp() / boss.getMaxHp(), 0f, 1f);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0.04f, 0.02f, 0.035f, 0.92f);
            shapeRenderer.rect(150f, 18f, 500f, 22f);
            shapeRenderer.setColor(0.42f, 0.03f, 0.08f, 1f);
            shapeRenderer.rect(158f, 25f, 484f, 8f);
            shapeRenderer.setColor(0.9f, 0.12f, 0.1f, 1f);
            shapeRenderer.rect(158f, 25f, 484f * bossRatio, 8f);
            shapeRenderer.end();
        }
    }

    private void drawHudText() {
        font.getData().setScale(1.0f);
        font.setColor(Color.WHITE);
        hudBuilder.setLength(0);
        hudBuilder.append("HP ")
            .append((int) player.getHp())
            .append("/")
            .append(player.getStats().getMaxHp())
            .append("   WAVE ")
            .append(GameManager.getInstance().getCurrentWave())
            .append("/")
            .append(WAVES.length)
            .append("   SCORE ")
            .append(GameManager.getInstance().getScore());
        font.draw(batch, hudBuilder, 18f, 468f);

        statsBuilder.setLength(0);
        statsBuilder.append("DMG ")
            .append(player.getStats().getDamage())
            .append("   SPD ")
            .append((int) player.getStats().getSpeed())
            .append("   CD ")
            .append(formatCooldown(player.getStats().getAttackCooldown()))
            .append("   ")
            .append(GameManager.getInstance().getDifficulty().getName());
        font.draw(batch, statsBuilder, 330f, 468f);

        WavePlan plan = getWavePlan(GameManager.getInstance().getCurrentWave());
        if (!bossWave) {
            statsBuilder.setLength(0);
            statsBuilder.append(plan.name)
                .append("   Remaining ")
                .append(levelManager == null ? 0 : Math.max(0, levelManager.getEnemiesAlive()));
            font.draw(batch, statsBuilder, 18f, 428f);
        }

        font.getData().setScale(0.85f);
        font.setColor(0.78f, 0.78f, 0.72f, 1f);
        font.draw(batch, "WASD move  |  Auto-slash when gold meter fills  |  ESC concede", 452f, 24f);

        if (waveIntroTimer > 0f) {
            float alpha = MathUtils.clamp(waveIntroTimer, 0f, 1f);
            font.getData().setScale(bossWave ? 2.2f : 1.75f);
            font.setColor(1f, bossWave ? 0.28f : 0.82f, bossWave ? 0.18f : 0.32f, alpha);
            statsBuilder.setLength(0);
            statsBuilder.append("WAVE ")
                .append(GameManager.getInstance().getCurrentWave())
                .append(": ")
                .append(plan.name);
            font.draw(batch, statsBuilder, bossWave ? 165f : 210f, 286f);
        }

        if (bossWave && boss != null && boss.isAlive()) {
            font.getData().setScale(1.0f);
            font.setColor(1f, 0.62f, 0.56f, 1f);
            statsBuilder.setLength(0);
            statsBuilder.append("DEMON KING  ")
                .append((int) boss.getHp())
                .append("/")
                .append((int) boss.getMaxHp());
            font.draw(batch, statsBuilder, 316f, 38f);
        }
        font.setColor(Color.WHITE);
        font.getData().setScale(1f);
    }

    private String formatCooldown(float cooldown) {
        int cdInt = (int) (cooldown * 100f + 0.5f);
        int cdWhole = cdInt / 100;
        int cdFrac = cdInt % 100;
        if (cdFrac < 10) {
            return cdWhole + ".0" + cdFrac;
        }
        return cdWhole + "." + cdFrac;
    }

    private void drawFloatingTexts() {
        font.getData().setScale(0.9f);
        for (int i = 0; i < floatingTexts.size(); i++) {
            floatingTexts.get(i).render(batch, font);
        }
        font.setColor(Color.WHITE);
        font.getData().setScale(1f);
    }

    private void applyWorldCamera() {
        float shakeX = 0f;
        float shakeY = 0f;
        if (shakeTimer > 0f) {
            float falloff = shakeTimer;
            shakeX = MathUtils.random(-shakePower, shakePower) * falloff;
            shakeY = MathUtils.random(-shakePower, shakePower) * falloff;
        }
        camera.position.set(WORLD_WIDTH / 2f + shakeX, WORLD_HEIGHT / 2f + shakeY, 0f);
        camera.update();
    }

    private void resetHudCamera() {
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0f);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    private void spawnHazard(float x, float y, float radius, float damagePerSecond) {
        hazards.add(new HazardZone(x, y, radius, damagePerSecond));
        spawnBurst(x, y, 10, 1f, 0.38f, 0.08f);
    }

    private void spawnBurst(float x, float y, int count, float r, float g, float b) {
        for (int i = 0; i < count; i++) {
            float angle = MathUtils.random(0f, MathUtils.PI2);
            float speed = MathUtils.random(35f, 150f);
            float size = MathUtils.random(2.2f, 5.2f);
            particles.add(new Particle(x, y, MathUtils.cos(angle) * speed,
                MathUtils.sin(angle) * speed, MathUtils.random(0.28f, 0.72f), size, r, g, b));
        }
    }

    private void spawnSlash(float x, float y) {
        for (int i = 0; i < 18; i++) {
            float angle = MathUtils.random(0f, MathUtils.PI2);
            float speed = MathUtils.random(110f, 245f);
            particles.add(new Particle(x, y, MathUtils.cos(angle) * speed,
                MathUtils.sin(angle) * speed, 0.23f, MathUtils.random(1.5f, 3.5f), 1f, 0.78f, 0.24f));
        }
    }

    private void addFloatingText(String text, float x, float y, float r, float g, float b) {
        floatingTexts.add(new FloatingText(text, x, y, r, g, b));
    }

    private void shake(float duration, float power) {
        shakeTimer = Math.max(shakeTimer, duration);
        shakePower = Math.max(shakePower, power);
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
        EventBus.getInstance().unsubscribe(GameEvent.Type.WAVE_CLEARED, waveClearedListener);
        EventBus.getInstance().unsubscribe(GameEvent.Type.PLAYER_DIED, playerDiedListener);
        EventBus.getInstance().unsubscribe(GameEvent.Type.BOSS_DIED, bossDiedListener);
        EventBus.getInstance().unsubscribe(GameEvent.Type.ENEMY_DIED, enemyDiedListener);
        if (levelManager != null) {
            levelManager.dispose();
            levelManager = null;
        }
    }

    @Override
    public void dispose() {
        hide();
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }

    private static final class WavePlan {
        private final String name;
        private final int slimes;
        private final int goblins;
        private final int archers;
        private final int brutes;
        private final float spawnInterval;
        private final boolean hazards;
        private final boolean bossWave;

        private WavePlan(String name, int slimes, int goblins, int archers, int brutes,
                float spawnInterval, boolean hazards, boolean bossWave) {
            this.name = name;
            this.slimes = slimes;
            this.goblins = goblins;
            this.archers = archers;
            this.brutes = brutes;
            this.spawnInterval = spawnInterval;
            this.hazards = hazards;
            this.bossWave = bossWave;
        }

        private int totalEnemies() {
            return slimes + goblins + archers + brutes;
        }
    }

    private static final class Particle {
        private float x;
        private float y;
        private float vx;
        private float vy;
        private final float maxLife;
        private final float size;
        private final float r;
        private final float g;
        private final float b;
        private float life;

        private Particle(float x, float y, float vx, float vy, float life,
                float size, float r, float g, float b) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.life = life;
            this.maxLife = life;
            this.size = size;
            this.r = r;
            this.g = g;
            this.b = b;
        }

        private void update(float delta) {
            life -= delta;
            x += vx * delta;
            y += vy * delta;
            vx *= 0.94f;
            vy *= 0.94f;
        }

        private void render(ShapeRenderer renderer) {
            float alpha = MathUtils.clamp(life / maxLife, 0f, 1f);
            renderer.setColor(r, g, b, alpha);
            renderer.circle(x, y, size * alpha + 0.8f);
        }
    }

    private static final class FloatingText {
        private final String text;
        private final float r;
        private final float g;
        private final float b;
        private float x;
        private float y;
        private float life;

        private FloatingText(String text, float x, float y, float r, float g, float b) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.r = r;
            this.g = g;
            this.b = b;
            this.life = 0.85f;
        }

        private void update(float delta) {
            life -= delta;
            y += 28f * delta;
        }

        private void render(SpriteBatch batch, BitmapFont font) {
            float alpha = MathUtils.clamp(life / 0.85f, 0f, 1f);
            font.setColor(r, g, b, alpha);
            font.draw(batch, text, x - 12f, y);
        }
    }

    private static final class HazardZone {
        private static final float TELEGRAPH_SECONDS = 1.05f;
        private static final float ACTIVE_SECONDS = 1.55f;

        private final float x;
        private final float y;
        private final float radius;
        private final float damagePerSecond;
        private float age;

        private HazardZone(float x, float y, float radius, float damagePerSecond) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.damagePerSecond = damagePerSecond;
            this.age = 0f;
        }

        private void update(float delta) {
            age += delta;
        }

        private boolean isActive() {
            return age >= TELEGRAPH_SECONDS;
        }

        private boolean isDone() {
            return age >= TELEGRAPH_SECONDS + ACTIVE_SECONDS;
        }
    }
}
