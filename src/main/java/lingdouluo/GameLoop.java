package lingdouluo;

import lingdouluo.config.Config;
import lingdouluo.entity.Bullet;
import lingdouluo.entity.Enemy;
import lingdouluo.entity.Player;
import lingdouluo.entity.Platform;
import lingdouluo.level.Level;
import lingdouluo.save.SaveData;
import lingdouluo.save.SaveSystem;
import lingdouluo.physics.CollisionSystem;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.scene.paint.Color;

public class GameLoop {
    // 游戏核心组件
    private Player player1;
    private Player player2;
    private Level currentLevel;
    private List<Platform> platforms= new ArrayList<>(); // 假设这里有平台列表
    private CollisionSystem collisionSystem;
    private List<Enemy> enemies;
    private List<Bullet> bullets;
    // 游戏状态
    private long score;
    private long playTime;
    private boolean isGameOver;
    private boolean isPaused;
    private double deltaTime;

    public GameLoop() {
        // 初始化玩家
        this.player1 = new Player("player1","凌云", "warrior");
        this.player2 = new Player("player2","星尘", "tech");
        // 初始化关卡
        this.currentLevel = new Level(Config.DEFAULT_LEVEL);
        // 初始化实体
        this.enemies = new ArrayList<>();
        this.bullets = new ArrayList<>();
        initEnemies();
        // 初始化物理系统
        this.collisionSystem = new CollisionSystem();
        // 初始化游戏状态
        this.score = 0;
        this.playTime = 0;
        this.isGameOver = false;
        this.isPaused = false;
        this.deltaTime = Config.DELTA_TIME;

        // 初始化平台
        initPlatforms();

        // 将平台设置给玩家
        player1.setPlatforms(platforms);
        player2.setPlatforms(platforms);

        // 设置玩家初始位置
        player1.setX(Config.PLAYER_INIT_X);
        player1.setY(Config.PLAYER_INIT_Y);
        player2.setX(Config.PLAYER_INIT_X + 100);
        player2.setY(Config.PLAYER_INIT_Y);
    }

    // 初始化平台
    private void initPlatforms() {
        // 主平台（地面）
        platforms.add(new Platform(0, 550, 800, 50,true));  // 底部主平台

        // 可选：添加更多平台
        platforms.add(new Platform(100, 450, 200, 20,true));  // 中间平台
        platforms.add(new Platform(400, 400, 150, 20,true));  // 另一个平台
        platforms.add(new Platform(600, 350, 200, 20,true));  // 高处平台
    }

    // 初始化敌人
    private void initEnemies() {
        enemies.add(new Enemy(500, 650, 100));  // 降低敌人Y坐标，使其在平台上
        enemies.add(new Enemy(800, 450, 120));
        enemies.add(new Enemy(1100, 250, 100));
        enemies.add(new Enemy(1400, 550, 150));
    }

    // 游戏主更新逻辑
    public void update() {
        if (isGameOver || isPaused) return;

        // 更新游戏时长
        playTime += (long) (deltaTime * 1000);

        // 更新玩家
        player1.update(deltaTime);
        player2.update(deltaTime);

        // 更新敌人
        Iterator<Enemy> enemyIt = enemies.iterator();
        while (enemyIt.hasNext()) {
            Enemy enemy = enemyIt.next();
            enemy.update(deltaTime, player1, this);
            if (enemy.isDestroyed()) {
                enemyIt.remove();
                score += 100;
            }
        }

        // 更新子弹
        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet bullet = bulletIt.next();
            bullet.update(deltaTime);
            if (bullet.isDestroyed() || isBulletOutOfScreen(bullet)) {
                bulletIt.remove();
            }
        }

        // 更新平台
        if (currentLevel != null) {
            currentLevel.updatePlatforms();
        }

        // 碰撞检测
        handleCollisions();

        // 平台碰撞检测
        handlePlatformCollisions();

        // 游戏结束检测
        if (player1.isDestroyed() && player2.isDestroyed()) {
            isGameOver = true;
        }
    }

    // 处理平台碰撞
    private void handlePlatformCollisions() {
        if (currentLevel == null) return;

        // 玩家1平台碰撞
        collisionSystem.checkPlayerPlatformCollision(player1, currentLevel.getPlatforms());
        // 玩家2平台碰撞
        collisionSystem.checkPlayerPlatformCollision(player2, currentLevel.getPlatforms());

        // 敌人平台碰撞（简化版，防止敌人掉下平台）
        for (Enemy enemy : enemies) {
            checkEnemyPlatformCollision(enemy, currentLevel.getPlatforms());
        }
    }

    // 敌人平台碰撞检测
    private void checkEnemyPlatformCollision(Enemy enemy, List<Level.Platform> platforms) {
        if (enemy.isDestroyed() || platforms == null || platforms.isEmpty()) return;

        boolean onPlatform = false;
        for (Level.Platform platform : platforms) {
            // 检测敌人是否在平台上
            if (enemy.getY() + enemy.getHeight() <= platform.getY() + Config.COLLISION_THRESHOLD &&
                    enemy.getY() + enemy.getHeight() >= platform.getY() &&
                    enemy.getX() < platform.getX() + platform.getWidth() &&
                    enemy.getX() + enemy.getWidth() > platform.getX()) {

                enemy.setY(platform.getY() - enemy.getHeight());
                onPlatform = true;
                break;
            }
        }
    }

    // 处理碰撞
    private void handleCollisions() {
        // 玩家与敌人碰撞
        for (Enemy enemy : enemies) {
            if (player1.isCollidingWith(enemy) && !player1.isDestroyed()) {
                player1.takeDamage(enemy.getDamage());
            }
            if (player2.isCollidingWith(enemy) && !player2.isDestroyed()) {
                player2.takeDamage(enemy.getDamage());
            }
        }

        // 子弹与敌人碰撞
        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet bullet = bulletIt.next();
            Iterator<Enemy> enemyIt = enemies.iterator();
            while (enemyIt.hasNext()) {
                Enemy enemy = enemyIt.next();
                if (bullet.isCollidingWith(enemy)) {
                    enemy.takeDamage(bullet.getDamage());
                    bullet.setDestroyed(true);
                    break;
                }
            }
        }
    }

    // 渲染游戏画面
    public void render(GraphicsContext gc) {
        // 背景
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, Config.SCREEN_WIDTH, Config.SCREEN_HEIGHT);

        // 渲染平台（使用我们自己的platforms列表）
        gc.setFill(Color.GRAY);
        for (Platform platform : platforms) {
            gc.fillRect(platform.getX(), platform.getY(),
                    platform.getWidth(), platform.getHeight());
        }
        /**
        // 渲染平台
        if (currentLevel != null) {
            for (Level.Platform platform : currentLevel.getPlatforms()) {
                gc.setFill(platform.isSolid() ? Color.GRAY : Color.GRAY.deriveColor(0,1,1,0.5));
                gc.fillRect(platform.getX(), platform.getY(), platform.getWidth(), platform.getHeight());
                gc.setStroke(Color.WHITE);
                gc.strokeRect(platform.getX(), platform.getY(), platform.getWidth(), platform.getHeight());
            }
        }
        **/
        // 渲染敌人
        for (Enemy enemy : enemies) {
            enemy.render(gc);
        }

        // 渲染子弹
        for (Bullet bullet : bullets) {
            bullet.render(gc);
        }

        // 渲染玩家
        player1.render(gc);
        player2.render(gc);

        // 渲染游戏信息
        renderGameInfo(gc);

        // 游戏结束/暂停提示
        if (isGameOver) {
            gc.setFill(Color.RED);
            gc.setFont(javafx.scene.text.Font.font(48));
            gc.fillText("GAME OVER! 得分: " + score, Config.SCREEN_WIDTH/2 - 200, Config.SCREEN_HEIGHT/2);
            gc.setFont(javafx.scene.text.Font.font(24));
            gc.fillText("按ESC退出游戏", Config.SCREEN_WIDTH/2 - 100, Config.SCREEN_HEIGHT/2 + 50);
        } else if (isPaused) {
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(48));
            gc.fillText("PAUSED", Config.SCREEN_WIDTH/2 - 100, Config.SCREEN_HEIGHT/2);
            gc.setFont(javafx.scene.text.Font.font(24));
            gc.fillText("按ESC退出游戏", Config.SCREEN_WIDTH/2 - 100, Config.SCREEN_HEIGHT/2 + 50);
        }
    }

    // 渲染游戏信息
    private void renderGameInfo(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(16));
        gc.fillText("得分: " + score, 20, 30);
        gc.fillText("时长: " + String.format("%02d:%02d", (playTime/1000)/60, (playTime/1000)%60), 20, 60);
        gc.fillText("关卡: " + (currentLevel != null ? currentLevel.getLevelName() : "未知"), 20, 90);
        gc.fillText(player1.getName() + " HP: " + player1.getHealth(), 20, 120);
        gc.fillText(player2.getName() + " HP: " + player2.getHealth(), 20, 150);
        gc.fillText("操作: WASD/方向键移动 | 空格/上键跳跃 | J/1射击 | P暂停 | ESC退出", 20, Config.SCREEN_HEIGHT - 30);
    }

    // 子弹超出屏幕判断
    private boolean isBulletOutOfScreen(Bullet bullet) {
        return bullet.getX() < 0 || bullet.getX() > Config.SCREEN_WIDTH
                || bullet.getY() < 0 || bullet.getY() > Config.SCREEN_HEIGHT;
    }

    // 按键处理分发
    // GameLoop.java - 修改handleKeyPress方法
    public void handleKeyPress(KeyEvent event) {



        System.out.println("按键按下: " + event.getCode());

        switch (event.getCode()) {
            case A:
                player1.setMovingLeft(true);
                System.out.println("玩家1左移");
                break;
            case D:
                player1.setMovingRight(true);
                System.out.println("玩家1右移");
                break;
            case W:
            case SPACE:
                System.out.println("玩家1跳跃，isOnPlatform=" + player1.isOnPlatform());
                player1.jump();
                break;
            case J:
                fireBullet(player1);
                System.out.println("玩家1射击");
                break;

            case LEFT:
                player2.setMovingLeft(true);
                System.out.println("玩家2左移");
                break;
            case RIGHT:
                player2.setMovingRight(true);
                System.out.println("玩家2右移");
                break;
            case UP:
                System.out.println("玩家2跳跃，isOnPlatform=" + player2.isOnPlatform());
                player2.jump();
                break;
            case NUMPAD1:
            case DIGIT1:
                fireBullet(player2);
                System.out.println("玩家2射击");
                break;

            case P:
                setPaused(!isPaused);
                System.out.println("游戏暂停: " + isPaused);
                break;
            case S:
                saveGame();
                break;
            case L:
                loadGame();
                break;
            case ESCAPE:
                System.out.println("ESC按下");
                break;
        }
    }

    public void handleKeyRelease(KeyEvent event) {
        switch (event.getCode()) {
            case A:
                player1.setMovingLeft(false);
                break;
            case D:
                player1.setMovingRight(false);
                break;
            case LEFT:
                player2.setMovingLeft(false);
                break;
            case RIGHT:
                player2.setMovingRight(false);
                break;
        }
    }

    // 发射子弹
    private void fireBullet(Player player) {
        if (player.isDestroyed() || isGameOver || isPaused) return;

        double direction = player.isFacingRight() ? 1 : -1;
        double bulletX = player.getX() + (direction > 0 ? player.getWidth() : -Config.BULLET_WIDTH);
        double bulletY = player.getY() + player.getHeight() / 2 - Config.BULLET_HEIGHT / 2;

        Bullet bullet = new Bullet(bulletX, bulletY, direction);
        bullets.add(bullet);
    }

    // 存档/读档
    public boolean saveGame() {
        if (isGameOver) return false;

        SaveData saveData = new SaveData(
                player1.getX(), player1.getY(), player1.getHealth(), player1.getName(), player1.getCharacterType(),
                player2.getX(), player2.getY(), player2.getHealth(), player2.getName(), player2.getCharacterType(),
                currentLevel != null ? currentLevel.getLevelName() : Config.DEFAULT_LEVEL, score, playTime
        );
        return SaveSystem.saveGame(saveData);
    }

    public boolean loadGame() {
        SaveData saveData = SaveSystem.loadGame();
        if (saveData == null) return false;

        // 恢复玩家1状态
        player1.setX(saveData.getPlayer1X());
        player1.setY(saveData.getPlayer1Y());
        player1.setHealth(saveData.getPlayer1Health());
        player1.setName(saveData.getPlayer1Name());
        player1.setCharacterType(saveData.getPlayer1Char());

        // 恢复玩家2状态
        player2.setX(saveData.getPlayer2X());
        player2.setY(saveData.getPlayer2Y());
        player2.setHealth(saveData.getPlayer2Health());
        player2.setName(saveData.getPlayer2Name());
        player2.setCharacterType(saveData.getPlayer2Char());

        // 恢复游戏状态
        this.currentLevel = new Level(saveData.getCurrentLevel());
        this.score = saveData.getScore();
        this.playTime = saveData.getPlayTime();
        this.isGameOver = false;
        this.isPaused = false;

        // 重新初始化敌人
        enemies.clear();
        initEnemies();
        bullets.clear();

        return true;
    }



    // ==================== Getter & Setter ====================
    public boolean isGameOver() { return isGameOver; }
    public void setGameOver(boolean gameOver) { isGameOver = gameOver; }
    public boolean isPaused() { return isPaused; }
    public void setPaused(boolean paused) { isPaused = paused; }
    public void addBullet(Bullet bullet) { bullets.add(bullet); }
    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
}