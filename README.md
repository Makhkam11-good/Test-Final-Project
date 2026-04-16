# ⚔ Gladiator Arena ⚔

## Описание

**Gladiator Arena** — это 2D Top-Down Arena Survival игра на платформе libGDX (Java 25).  
Пережи 10 волн врагов и победи финального Демона-Короля на арене. Черпай силу из апгрейдов и выбирай оптимальные комбинации характеристик для победы.

Игра реализована с использованием **7 паттернов проектирования** (GoF) и демонстрирует архитектурные лучшие практики разработки игр.

---

## Управление

| Вход | Действие |
|------|----------|
| **W / A / S / D** | Движение рыцаря (8 направлений) |
| **Движение** | Автоматическая атака (каждые 1-2 сек) |
| **1 / 2 / 3** | Выбор сложности в меню (Easy / Medium / Hard) |
| **1 / 2 / 3** | Выбор апгрейда (на экране выбора) |

---

## Как запустить

### Требования
- Java 25+
- libGDX 1.14.0
- Gradle

### Шаги
1. Клонируй репозиторий:
   ```bash
   git clone <repo-url>
   cd "Test Final"
   ```

2. Загрузи спрайты с itch.io в папки `assets/`:
   ```
   assets/
   ├── sprites/player/      (idle.png, run.png, attack.png, dead.png)
   ├── sprites/enemies/     (slime.png, goblin.png, boss.png)
   ├── backgrounds/         (arena.png)
   ├── ui/                  (hp_bar_bg.png, hp_bar_fill.png, card_bg.png)
   ├── fonts/               (game_font.fnt, game_font.png)
   └── audio/               (music/game_bgm.mp3, sounds/*.wav)
   ```

3. Откройи проект в **IntelliJ IDEA** (Gradle sync автоматически)

4. Запусти:
   ```bash
   ./gradlew lwjgl3:run
   ```
   или `lwjgl3/src/main/java/com/gladiator/lwjgl3/Lwjgl3Launcher.java`

---

## Игровой процесс

### Волны (1–9)
- Враги появляются волнами с нарастающей сложностью
- После каждой волны → **UpgradeScreen** с выбором 3 случайных апгрейдов
- HP игрока не восстанавливается между волнами

### Волна 10 — Босс
- **Финальный враг: Демон-Король** с 3 состояниями (Idle → Chase → Dash)
- Повышенный урон и сложная тактика
- При победе → **VictoryScreen**

### Апгрейды (Decorator паттерн)
Стакаются и комбинируются свободно:
- **Fire Weapon** — +15 урона
- **Poison** — +10 урона + эффект яда
- **Shield** — +30 HP
- **Armor** — -20% входящего урона
- **Speed Boots** — +25% скорость
- **Attack Speed** — -20% кулдаун атаки

### Сложности (Strategy паттерн)
| Параметр | Easy | Medium | Hard |
|----------|------|--------|------|
| Скорость врагов | ×0.8 | ×1.0 | ×1.3 |
| Урон врагов | ×0.7 | ×1.0 | ×1.5 |
| HP Босса | 300 | 500 | 1000 |

---

## 7 Паттернов GoF

### 1. **Singleton** 🏭
Единственные экземпляры для глобального состояния.

**Классы:** `AssetManager`, `GameManager`, `EventBus`

```java
AssetManager.getInstance().loadAll();
GameManager.getInstance().getDifficulty();
EventBus.getInstance().post(new GameEvent(Type.ENEMY_DIED));
```

---

### 2. **State** 🔄
Переключение поведения без if-else.

**Классы:** 
- `GameStateManager` (LOADING, MENU, GAME, UPGRADE, GAME_OVER, VICTORY)
- `PlayerState` → IdleState, RunState, AttackState, DeadState
- `BossState` → IdleBossState, ChaseBossState, DashBossState

```java
// Игрок меняет состояние
if (velocityX != 0) changeState(new RunState(this));
else changeState(new IdleState(this));

// Босс делегирует поведение State
currentState.update(this, delta, playerX, playerY);
```

---

### 3. **Factory Method** 🏗️
Создание врагов без изменения основного кода.

**Классы:** `EnemyFactory` → SlimeFactory, GoblinFactory, BossFactory

```java
Enemy slime = new SlimeFactory().create(x, y);
Enemy goblin = new GoblinFactory().create(x, y);
Boss boss = (Boss) new BossFactory().create(x, y);
```

---

### 4. **Observer** 👁️
Слабо связанное взаимодействие через события.

**Классы:** `EventBus`, `GameEvent`, `EventListener`

```java
// Подписка на события
EventBus.getInstance().subscribe(GameEvent.Type.WAVE_CLEARED, event -> {
    gsm.push(GameStateManager.State.UPGRADE);
});

EventBus.getInstance().subscribe(GameEvent.Type.ENEMY_DIED, event -> {
    AssetManager.getInstance().getSound("enemy_death").play();
});

// Публикация событий
EventBus.getInstance().post(new GameEvent(GameEvent.Type.WAVE_CLEARED));
```

**События:** ENEMY_DIED, WAVE_CLEARED, PLAYER_HURT, PLAYER_DIED, BOSS_DIED

---

### 5. **Strategy** 📊
Сложность меняется без изменения логики.

**Классы:** `DifficultyStrategy` → EasyDifficulty, MediumDifficulty, HardDifficulty

```java
// Выбор сложности
DifficultyStrategy strategy = new HardDifficulty();
GameManager.getInstance().setDifficulty(strategy);

// Враги применяют множители
float speed = baseSpeed * difficulty.getEnemySpeedMult();
```

---

### 6. **Decorator** 🎁
Апгрейды стакаются и комбинируются.

**Классы:** `PlayerStats` → BasePlayerStats → 6 декораторов

```java
// Цепочка оборачивания
stats = new BasePlayerStats();
stats = new FireWeaponDecorator(stats);
stats = new ShieldDecorator(stats);
stats = new SpeedBootsDecorator(stats);

// Рекурсивное вычисление характеристик
int damage = stats.getDamage();  // Суммирует бонусы всех декораторов
```

---

### 7. **Command** ⚡
Действия как объекты с историей.

**Классы:** `Command` → MoveCommand, AttackCommand, `CommandHistory`

```java
Command moveCommand = new MoveCommand(player, Direction.RIGHT);
moveCommand.execute();

CommandHistory history = new CommandHistory();
history.add(moveCommand);
history.undo();  // Отмена действия
```

---

## Структура проекта

```
core/src/main/java/com/gladiator/
├── GladiatorGame.java           # Точка входа
├── screens/                     # Все экраны
│   ├── LoadingScreen.java
│   ├── MenuScreen.java
│   ├── GameScreen.java
│   ├── UpgradeScreen.java
│   ├── GameOverScreen.java
│   ├── VictoryScreen.java
│   └── HUD.java
├── managers/                    # Синглтоны
│   ├── AssetManager.java
│   ├── GameManager.java
│   ├── GameStateManager.java
│   └── LevelManager.java
├── entities/                    # Игровые объекты
│   ├── Player.java
│   ├── Enemy.java
│   ├── Boss.java
│   ├── states/                  # PlayerState реализации
│   └── boss/                    # BossState реализации
├── factories/                   # Factory Method
│   ├── EnemyFactory.java
│   ├── SlimeFactory.java
│   ├── GoblinFactory.java
│   └── BossFactory.java
├── decorator/                   # Decorator паттерн
│   ├── PlayerStats.java
│   ├── BasePlayerStats.java
│   ├── PlayerDecorator.java
│   └── 6 декораторов
├── strategy/                    # Strategy паттерн
│   ├── DifficultyStrategy.java
│   ├── EasyDifficulty.java
│   ├── MediumDifficulty.java
│   └── HardDifficulty.java
├── commands/                    # Command паттерн
│   ├── Command.java
│   ├── MoveCommand.java
│   ├── AttackCommand.java
│   └── CommandHistory.java
└── events/                      # Observer паттерн
    ├── EventBus.java
    ├── GameEvent.java
    └── EventListener.java
```

---

## Особенности Фазы 9

✅ Спрайты и анимации (Player, Enemy, Boss)  
✅ LoadingScreen с асинхронной загрузкой и прогресс-баром  
✅ Фон арены вместо чёрного экрана  
✅ Цветной HUD (HP бар: зелёный→оранжевый→красный)  
✅ Звуки: удары, смерти, апгрейды, появление босса  
✅ Fallback механика (если спрайт не найден — цветной прямоугольник)  
✅ Нет утечек памяти (все ресурсы освобождаются)  

---

## Контрол-лист

- [x] Игра запускается с LoadingScreen
- [x] MenuScreen с выбором сложности
- [x] GameScreen: враги, игрок, апгрейды
- [x] Все 7 паттернов реализованы и работают
- [x] Boss на волне 10 с State паттерном
- [x] Спрайты и анимации
- [x] Звуки через Observer
- [x] HUD с цветным HP баром
- [x] Нет memory leak
- [x] README с описанием всех паттернов

---

## Лицензия

MIT

---

**Удачи в Gladiator Arena!** ⚔️🎮
