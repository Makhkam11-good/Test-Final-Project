Вот готовый файл в формате Markdown. Я аккуратно перевел все данные в Markdown-таблицы и структурировал текст, чтобы нейросеть (Claude) в VS Code смогла идеально прочитать твой GDD-документ.

Просто скопируй весь код из блока ниже, создай в VS Code файл с названием `GDD.md` в корне твоего проекта и вставь туда этот текст.

```markdown
# ⚔ GLADIATOR ARENA ⚔ 
## Game Design Document + Пошаговый план разработки
**libGDX · Java · 7 паттернов GoF · Месяц разработки** 

### 1. Общая концепция

| Характеристика | Описание |
| :--- | :--- |
| Жанр | 2D Top-Down Arena Survival — выживание на арене с видом сверху (вдохновлено Brotato) 
| Суть | Рыцарь стоит в центре арены 800×480. Враги лезут со всех сторон. Рыцарь автоматически атакует. Цель — пережить 10 волн и убить Босса. 
| Камера | Статичная — вся игра умещается на одном экране. Арена не прокручивается. 
| Победа | Убить Финального Босса на 10-й волне → экран Victory. Если HP Рыцаря обнулился раньше → Game Over. 

Между каждой волной (1–9) появляется экран выбора апгрейда: 3 карточки, игрок выбирает одну.
Апгрейды накапливаются через паттерн Decorator, создавая уникальную комбинацию каждую игру. 

### 2. Игровые сущности

#### 2.1 Рыцарь (Player) 
Главный персонаж. Не умирает между волнами — HP сохраняется. Апгрейды также сохраняются. 

| Параметр | Значение |
| :--- | :--- |
| Управление | W A S D — движение по 8 направлениям |
| HP базовое | 100 единиц. Убывает при контакте с врагами.  |
| Скорость | 150 px/сек — модифицируется декораторами  |
| Атака | Автоматически — удар в радиусе 80px раз в 1 сек  |
| Урон базовый | 10 единиц — модифицируется декораторами  |
| Кулдаун атаки | 1.0 сек — модифицируется DecoratorAttackSpeed  |

#### 2.2 Враги 
Слизь (Slime) — появляется с волны 1 

| Параметр | Значение |
| :--- | :--- |
| HP | 20 единиц  |
| Урон | 5 ед/сек при контакте  |
| Скорость | 60 px/сек  |
| Награда | 10 очков  |
| AI | PatrolAI → при виде игрока AggressiveAI  |

Гоблин (Goblin) — появляется с волны 3 

| Параметр | Значение |
| :--- | :--- |
| HP | 40 единиц  |
| Урон | 12 ед/сек при контакте  |
| Скорость | 100 px/сек  |
| Награда | 25 очков  |
| AI | AggressiveAI — сразу преследует игрока  |

Финальный Босс (Demon King) — только волна 10 

| Параметр | Значение |
| :--- | :--- |
| HP | 500 (Easy: 300 / Hard: 1000)  |
| Урон контакт | 20 ед/сек  |
| Урон рывок (Dash) | 40 единиц за касание  |
| AI | State: IdleState → ChaseState → DashState → Idle  |
| HP бар | Большая полоса внизу экрана  |

#### 2.3 Состояния Босса — паттерн State 

| Состояние | Поведение и длительность |
| :--- | :--- |
| IdleState | Стоит, вращается к игроку. Длится 1.5 сек → переход в Chase.  |
| ChaseState | Медленно идёт к игроку (80 px/сек). Длится 3.0 сек → переход в Dash. |
| DashState | Рывок 400 px/сек в направлении игрока. Длится 0.6 сек → переход в Idle. |

### 3. Апгрейды — паттерн Decorator 
После каждой из волн 1–9 игрок видит экран с 3 случайными карточками апгрейда. 
Выбор одной из них оборачивает текущие характеристики Рыцаря новым декоратором. Эффекты накапливаются.

Пример цепочки: BasePlayer → FireWeaponDecorator → ShieldDecorator → SpeedDecorator 

| Декоратор | Категория | Эффект | Стек (если взять дважды) |
| :--- | :--- | :--- | :--- |
| FireWeaponDecorator | Атака | +15 урона | +30 урона — огненный меч  |
| PoisonDecorator | Атака | +10 урон + яд | Яд стакается — 2 стака/сек  |
| ShieldDecorator | Защита | +30 HP | +60 HP (с малым штрафом скорости)  |
| ArmorDecorator | Защита | -20% вход. урон | -36% суммарно (мультипликативно) |
| SpeedBootsDecorator | Движение | +25% скорость | +56% скорость суммарно  |
| AttackSpeedDecorator | Движение | -20% кулдаун | -36% кулдаун — почти двойная атака  |

### 4. 10 волн — прогрессия игры 
Волна считается завершённой когда все враги уничтожены. LevelManager (Observer) считает убийства и публикует WAVE_CLEARED. 

| Волна | Состав врагов | После волны | Сложность |
| :--- | :--- | :--- | :--- |
| 1 | 4 Слизи | Экран апгрейда (Decorator) | Очень легко  |
| 2 | 6 Слизей | Экран апгрейда | Легко  |
| 3 | 4 Слизи + 2 Гоблина | Экран апгрейда | Легко+  |
| 4 | 5 Гоблинов + 3 Слизи | Экран апгрейда | Средне  |
| 5 | 8 Гоблинов | Экран апгрейда | Средне  |
| 6 | 6 Гоблинов + 4 Слизи | Экран апгрейда | Средне+ |
| 7 | 10 Гоблинов | Экран апгрейда | Сложно  |
| 8 | 8 Гоблинов + 5 Слизей (быстрее) | Экран апгрейда | Сложно |
| 9 | 12 Гоблинов (макс. скорость) | Экран апгрейда | Очень сложно  |
| 10 | ФИНАЛЬНЫЙ БОСС | Victory / Game Over | Босс  |

### 5. Сложность — паттерн Strategy 
Выбирается в главном меню. Создаётся объект стратегии, передаётся в GameManager, влияет на все расчёты. 

| Параметр | Easy | Medium | Hard |
| :--- | :--- | :--- | :--- |
| Скорость врагов | ×0.8 (замедлены) | ×1.0 (базовая) | ×1.3 (быстрые) |
| Урон врагов | ×0.7 | ×1.0 | ×1.5  |
| HP Босса | 300 | 500 | 1000  |
| Интервал спавна | 2.0 сек (редко) | 1.5 сек | 1.0 сек (часто)  |

### 6. Все паттерны GoF 

| Паттерн | Где применяется | Что даёт коду |
| :--- | :--- | :--- |
| Singleton | AssetManager, GameManager | Единственный экземпляр, доступен отовсюду  |
| State | GameStateManager, PlayerState (4 состояния), BossState (3 состояния) | Поведение меняется без длинных if-else  |
| Factory Method | EnemyFactory → SlimeFactory, GoblinFactory, BossFactory | Новый враг = новая фабрика, GameScreen не меняется  |
| Observer | EventBus: ENEMY_DIED, WAVE_CLEARED, PLAYER_HURT, PLAYER_DIED | Компоненты не зависят друг от друга  |
| Strategy | DifficultyStrategy: Easy / Medium / Hard | Сложность меняется без изменения логики игры  |
| Decorator | PlayerDecorator: Fire, Poison, Shield, Armor, Speed, AttackSpeed | Апгрейды стакаются и комбинируются свободно  |
| Command | MoveCommand, AttackCommand, CommandHistory | Действия — объекты, история команд  |

### 7. Экраны игры 

**MenuScreen** 
* Заголовок игры, три кнопки Easy / Medium / Hard 
* При нажатии → создаётся DifficultyStrategy, передаётся в GameManager → переход в GameScreen 

**GameScreen** 
* Основной цикл: update() + render() 
* HUD: полоса HP игрока, текущая волна, счёт 
* Спавн врагов через EnemyFactory по таймеру 
* При завершении волны → UpgradeScreen 
* При смерти → GameOverScreen, при победе → VictoryScreen 

**UpgradeScreen** 
* Показывает 3 случайных карточки декораторов 
* Клик → player.setStats(new Decorator(player.getStats())) → возврат в GameScreen 

**GameOverScreen / VictoryScreen** 
* GameOver: счёт, достигнутая волна, кнопки Ещё раз / Меню 
* Victory: золотой фон, финальный счёт, кнопка Ещё раз 

### 8. Структура пакетов 

| Пакет | Содержит |
| :--- | :--- |
| com.gladiator | GladiatorGame.java — точка входа  |
| .screens | MenuScreen, GameScreen, UpgradeScreen, GameOverScreen, VictoryScreen  |
| .managers | AssetManager (Singleton), GameStateManager (State), GameManager  |
| .events | EventBus (Observer), GameEvent, EventListener  |
| .entities | Player, Enemy, Boss — базовые классы сущностей  |
| .entities.states | PlayerState interface, IdleState, RunState, AttackState, DeadState  |
| .entities.boss | BossState interface, IdleBossState, ChaseBossState, DashBossState  |
| .factories | EnemyFactory (abstract), SlimeFactory, GoblinFactory, BossFactory  |
| .decorator | PlayerStats (interface), BasePlayer, PlayerDecorator (abstract), 6 декораторов  |
| .strategy | DifficultyStrategy (interface), EasyDifficulty, MediumDifficulty, HardDifficulty  |
| .commands | Command, MoveCommand, AttackCommand, CommandHistory  |
| .ai | EnemyAI (interface), PatrolAI, AggressiveAI, CowardlyAI, ArcherAI  |
| assets/ | sprites/, backgrounds/, ui/, audio/ — все ресурсы игры  |

### 9. Пошаговый план разработки 
Каждая фаза строится на предыдущей. Не переходи к следующей пока текущая не работает без ошибок. 

| ФАЗА 1: НАСТРОЙКА ПРОЕКТА (День 1–2) |
| :--- |
| 1. Скачай libGDX Project Generator на gdx-liftoff.com. Выбери: Desktop модуль, Java, Gradle.  |
| 2. Импортируй проект в IntelliJ IDEA. Убедись что Gradle sync прошёл без ошибок.  |
| 3. Запусти DesktopLauncher.java — должен открыться пустой чёрный экран 800×480.  |
| 4. Создай структуру всех пакетов согласно разделу 8 — пустые классы-заглушки (просто public class X {}).  |
| 5. Создай AssetManager.java с паттерном Singleton: private static AssetManager instance; public static AssetManager getInstance().  |
| 6. Создай GladiatorGame.java extends Game — вызывает AssetManager.getInstance().loadAll() в методе create().  |
| 7. Цель фазы: проект компилируется, пустой экран открывается. Нет красных ошибок в IDE. ✓  |

| ФАЗА 2: ЭКРАНЫ И STATE (День 3–4) |
| :--- |
| 1. Создай GameStateManager.java с enum State {MENU, GAME, UPGRADE, GAME_OVER, VICTORY} и методами push(), pop(), set().  |
| 2. Создай 5 пустых классов экранов implements Screen — каждый просто очищает экран в render() своим цветом.  |
| 3. В MenuScreen: нарисуй через BitmapFont текст 'GLADIATOR' и надписи Easy / Medium / Hard.  |
| 4. Добавь обработку клавиш: 1 → Easy, 2 → Medium, 3 → Hard → переход в GameScreen через gsm.set(GAME).  |
| 5. В GameScreen: пока только рисует тёмно-зелёный прямоугольник (арена-заглушка).  |
| 6. Добавь ESC в GameScreen → GameOverScreen для тестирования всех переходов.  |
| 7. Цель фазы: все 5 экранов открываются, переходы между ними работают. ✓  |

| ФАЗА 3: РЫЦАРЬ (PLAYER) (День 5–7) |
| :--- |
| 1. Создай Player.java с полями: float x, y, velocityX, velocityY, hp, maxHp, attackTimer.  |
| 2. В update(delta): читай WASD через Gdx.input.isKeyPressed(), обновляй velocityX/Y, перемещай x += velocityX * delta.  |
| 3. Ограничь позицию: x = MathUtils.clamp(x, 0, 800-W), y = MathUtils.clamp(y, 0, 480-H).  |
| 4. В render(batch): пока рисуй белый прямоугольник 48×64. Убедись что он двигается.  |
| 5. Создай интерфейс PlayerState и 4 класса: IdleState, RunState, AttackState, DeadState.  |
| 6. Подключи State: при velocityX != 0 → changeState(new RunState()), иначе → IdleState.  |
| 7. Добавь автоатаку: attackTimer -= delta; if (attackTimer <= 0) { attackTimer = 1.0f; performAttack(); }  |
| 8. Добавь Rectangle bounds = new Rectangle(), обновляй в каждом update().  |
| 9. Цель фазы: белый прямоугольник-игрок двигается по экрану, не выходит за края. ✓  |

| ФАЗА 4: EVENTBUS + LEVELMAN (День 8–9) |
| :--- |
| 1. Создай EventBus.java (Singleton): Map<Type, List<EventListener>> listeners. Методы subscribe(), post().  |
| 2. Создай GameEvent.java с enum Type {ENEMY_DIED, WAVE_CLEARED, PLAYER_HURT, PLAYER_DIED, BOSS_DIED}.  |
| 3. Создай EventListener.java как @FunctionalInterface с методом void onEvent(GameEvent e).  |
| 4. Создай LevelManager.java: подписывается на ENEMY_DIED, хранит счётчик enemiesAlive.  |
| 5. В LevelManager: при enemiesAlive == 0 → EventBus.post(new GameEvent(WAVE_CLEARED)).  |
| 6. В GameScreen.show(): создай LevelManager, задай enemiesAlive = количеству врагов волны.  |
| 7. В GameScreen: подпишись на WAVE_CLEARED → gsm.push(UPGRADE). В hide() → EventBus.clear().  |
| 8. Протестируй: добавь временную кнопку Q в GameScreen → вручную публикует ENEMY_DIED. Проверь что WAVE_CLEARED приходит.  |
| 9. Цель фазы: EventBus работает, события доходят до всех подписчиков без ошибок. ✓  |

| ФАЗА 5: ФАБРИКА + ВРАГИ (День 10–13) |
| :--- |
| 1. Создай Enemy.java: поля x, y, hp, damage, speed, Rectangle bounds. Методы update(), render(), takeDamage(int).  |
| 2. В Enemy.update(): вычисли вектор к игроку: dx = playerX-x, dy = playerY-y, нормализуй, умножь на speed*delta.  |
| 3. При bounds.overlaps(player.bounds): player.takeDamage(damage * delta) каждый кадр.  |
| 4. Создай абстрактный EnemyFactory с методом abstract Enemy create(float x, float y).  |
| 5. Создай SlimeFactory extends EnemyFactory — создаёт Enemy с HP=20, damage=5, speed=60.  |
| 6. Создай GoblinFactory extends EnemyFactory — создаёт Enemy с HP=40, damage=12, speed=100.  |
| 7. В GameScreen: добавь List<Enemy> enemies, спавни у краёв экрана (x=0, x=800, y=0, y=480) рандомно.  |
| 8. При enemy.hp <= 0: EventBus.post(ENEMY_DIED, enemy) → удаляй из списка через Iterator.remove().  |
| 9. При player.isAttacking() проверяй overlaps с каждым врагом в радиусе 80px → enemy.takeDamage(player.damage).  |
| 10. Цель фазы: враги появляются со всех сторон, идут к игроку, умирают от атаки. ✓  |

| ФАЗА 6: STRATEGY СЛОЖНОСТИ (День 14) |
| :--- |
| 1. Создай интерфейс DifficultyStrategy: float getEnemySpeedMult(), float getEnemyDamageMult(), int getBossHp(), float getSpawnInterval().  |
| 2. Создай EasyDifficulty, MediumDifficulty, HardDifficulty — реализуют DifficultyStrategy с конкретными числами.  |
| 3. Создай GameManager.java (Singleton): хранит DifficultyStrategy difficulty, int score, int currentWave.  |
| 4. В MenuScreen при выборе кнопки: GameManager.getInstance().setDifficulty(new EasyDifficulty()).  |
| 5. В EnemyFactory при create(): умножь speed *= GameManager.getInstance().getDifficulty().getEnemySpeedMult().  |
| 6. Добавь в GameScreen таймер спавна: spawnTimer -= delta; if < 0 → спавн нового врага → сбрось на getSpawnInterval().  |
| 7. Цель фазы: на Hard враги заметно быстрее и появляются чаще чем на Easy. ✓  |

| ФАЗА 7: DECORATOR АПГРЕЙДЫ (День 15–17) |
| :--- |
| 1. Создай интерфейс PlayerStats: int getMaxHp(), int getDamage(), float getSpeed(), float getAttackCooldown().  |
| 2. Создай BasePlayerStats implements PlayerStats — возвращает базовые значения (100, 10, 150f, 1.0f).  |
| 3. Создай abstract PlayerDecorator implements PlayerStats с полем protected PlayerStats wrapped.  |
| 4. Создай 6 конкретных декораторов: каждый переопределяет один метод, добавляя бонус к wrapped.getX().  |
| 5. В Player.java: замени прямые поля на PlayerStats stats = new BasePlayerStats(). Читай hp через stats.getMaxHp().  |
| 6. Метод Player.applyUpgrade(PlayerDecorator d): d.setWrapped(this.stats); this.stats = d.  |
| 7. Создай UpgradeScreen: выбирай 3 случайных класса из 6 декораторов, покажи карточки с текстом.  |
| 8. При клике на карточку: player.applyUpgrade(new FireWeaponDecorator()) → вернись в GameScreen.  |
| 9. Добавь лог в консоль: System.out.println('Damage: ' + player.stats.getDamage()) — проверь что растёт.  |
| 10. Цель фазы: апгрейды применяются, характеристики реально меняются, видно в логе. ✓  |

| ФАЗА 8: БОСС (День 18–20) |
| :--- |
| 1. Создай интерфейс BossState: void enter(), void update(float delta, float px, float py), void exit().  |
| 2. Создай IdleBossState (стоит 1.5 сек), ChaseBossState (идёт 80 px/сек, 3 сек), DashBossState (рывок 400 px/сек, 0.6 сек).  |
| 3. Создай Boss.java extends Enemy: добавь поле BossState currentState, метод changeState(BossState).  |
| 4. В Boss.update(): делегируй currentState.update(delta, playerX, playerY) — State управляет поведением.  |
| 5. Создай BossFactory extends EnemyFactory: создаёт Boss с HP = difficulty.getBossHp().  |
| 6. В GameScreen: на волне 10 — останови обычный спавн, создай 1 Босса через BossFactory в центре края экрана.  |
| 7. Добавь HP бар Босса: ShapeRenderer рисует полосу 400×16 снизу экрана, заполненность = boss.hp / boss.maxHp.  |
| 8. При boss.hp <= 0: EventBus.post(BOSS_DIED) → GameScreen переключает на VictoryScreen.  |
| 9. Цель фазы: Босс появляется, видно смену состояний (разная скорость), умирает, Victory показывается. ✓  |

| ФАЗА 9: СПРАЙТЫ И ПОЛИРОВКА (День 21–28) |
| :--- |
| 1. Найди бесплатные спрайты: itch.io → поиск 'knight pixel art free', 'slime sprite sheet', 'goblin pixel'.  |
| 2. Загрузи все текстуры в AssetManager.loadAll(). Замени белые прямоугольники на реальные спрайты.  |
| 3. Добавь анимации: TextureRegion sheet = TextureRegion.split(texture, frameW, frameH). Используй Animation<TextureRegion>.  |
| 4. Добавь фон арены: текстура земли/камня рисуется первой в batch.begin().  |
| 5. Добавь HUD финальный: BitmapFont, рисуй 'HP: X / Y \| Wave: N \| Score: S' в левом верхнем углу.  |
| 6. Добавь звуки (опционально): Gdx.audio.newSound() для удара, смерти врага, получения урона.  |
| 7. Протестируй все 3 сложности, все 10 волн, все 6 декораторов в разных комбинациях.  |
| 8. Убедись: нет memory leak — dispose() вызывается для всех Texture, Sound, Music объектов.  |
| 9. Напиши README.md: название игры, как запустить, описание всех 7 паттернов с примерами классов из кода.  |
| 10. Цель фазы: игра выглядит как настоящая, работает стабильно, готова к сдаче. ✓  |

### 10. Финальный чеклист 

| Готово | Что проверить | Паттерн |
| :---: | :--- | :--- |
| ☐ | Игра запускается, открывается MenuScreen | State |
| ☐ | Выбор сложности реально меняет параметры врагов | Strategy |
| ☐ | Рыцарь двигается WASD, не выходит за края экрана | Command |
| ☐ | Рыцарь имеет 4 состояния (Idle/Run/Attack/Dead) | State |
| ☐ | Слизь и Гоблин создаются через разные фабрики | Factory Method |
| ☐ | Убийство врага публикует ENEMY_DIED через EventBus | Observer |
| ☐ | LevelManager считает убийства и публикует WAVE_CLEARED | Observer |
| ☐ | После волны показывается UpgradeScreen с 3 карточками | Decorator |
| ☐ | Апгрейды стакаются и реально меняют характеристики | Decorator |
| ☐ | Волна 10 спавнит Босса через BossFactory | Factory Method |
| ☐ | Босс имеет 3 состояния: Idle / Chase / Dash | State |
| ☐ | HP бар Босса отображается и убывает | Observer |
| ☐ | Победа над Боссом → VictoryScreen | State |
| ☐ | Смерть Рыцаря → GameOverScreen | State |
| ☐ | GameManager.getInstance() работает из любого класса | Singleton |
| ☐ | AssetManager.getInstance() загружает все ресурсы | Singleton |
| ☐ | README с описанием всех 7 паттернов и примерами кода | Документация  |

⚔ Удачи в разработке! Ты справишься ⚔ 
```