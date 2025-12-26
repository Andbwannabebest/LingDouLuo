// src/main/java/com/lingdouluo/LingDouLuoGame.java
package com.lingdouluo;

import com.lingdouluo.config.Config;
import com.lingdouluo.entity.*;
import com.lingdouluo.input.InputManager;
import com.lingdouluo.level.Level;
import com.lingdouluo.save.SaveSystem;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class LingDouLuoGame {

    private GraphicsContext gc;
    private Player player1;
    private Player player2;
    private Level currentLevel;
    private InputManager inputManager;
    private List<Level> levels;
    private int currentLevelIndex;

    private double gameTime;
    private boolean isPaused;
    private boolean showPauseMenu;

    // 敌人列表 - 修改为Enemy类型
    private List<Enemy> enemies;

    // 菜单状态
    private MenuState menuState;
    private enum MenuState {
        MAIN_MENU,
        PAUSE_MENU,
        GAME_OVER,
        LEVEL_COMPLETE,
        PLAYING
    }

    public LingDouLuoGame(GraphicsContext gc) {
        this.gc = gc;
        this.inputManager = new InputManager();
        this.levels = new ArrayList<>();
        this.enemies = new ArrayList<>(); // 改为List<Enemy>
        this.gameTime = 0;
        this.isPaused = false;
        this.showPauseMenu = false;
        this.menuState = MenuState.MAIN_MENU;

        initializeGame();
    }

    private void initializeGame() {
        // 初始化关卡
        createLevels();

        // 初始化玩家1和玩家2
        player1 = new Player(100, 500, 1);
        player2 = new Player(200, 500, 2);

        player1.setInputManager(inputManager);
        player2.setInputManager(inputManager);

        // 给玩家添加更多武器
        player1.addWeapon(new RifleWeapon(player1));
        player1.addWeapon(new GrenadeWeapon(player1));
        player1.addWeapon(new ShotgunWeapon(player1));

        player2.addWeapon(new RifleWeapon(player2));
        player2.addWeapon(new GrenadeWeapon(player2));
        player2.addWeapon(new ShotgunWeapon(player2));

        // 加载第一关
        loadLevel(0);

        // 设置初始菜单状态
        Config.GAME_STATE = GameState.MENU;
    }

    private void createLevels() {
        // 创建工厂区关卡
        Level factoryLevel = new Level("工厂区");
        factoryLevel.setBackgroundColor(Color.rgb(60, 60, 70));
        factoryLevel.setPlayerSpawn(100, 500);

        // 添加平台
        factoryLevel.addPlatform(0, 600, 1280, 120);  // 地面
        factoryLevel.addPlatform(200, 500, 100, 20);  // 平台1
        factoryLevel.addPlatform(400, 450, 100, 20);  // 平台2
        factoryLevel.addPlatform(600, 400, 100, 20);  // 平台3
        factoryLevel.addPlatform(800, 350, 100, 20);  // 平台4
        factoryLevel.addPlatform(1000, 300, 100, 20); // 平台5
        factoryLevel.addPlatform(1100, 250, 100, 20); // 平台6

        levels.add(factoryLevel);

        // TODO: 添加更多关卡
    }

    public void loadLevel(int index) {
        if (index >= 0 && index < levels.size()) {
            currentLevelIndex = index;
            currentLevel = levels.get(index);
            player1.setPosition(currentLevel.getPlayerSpawnX(), currentLevel.getPlayerSpawnY());
            player2.setPosition(currentLevel.getPlayerSpawnX() + 100, currentLevel.getPlayerSpawnY());
            currentLevel.setPlayer(player1);

            // 创建敌人
            createEnemies();

            Config.GAME_STATE = GameState.PLAYING;
            menuState = MenuState.PLAYING;
        }
    }

    private void createEnemies() {
        enemies.clear();

        // 创建巡逻敌人
        PatrolEnemy enemy1 = new PatrolEnemy(400, 550, 64, 64);
        enemy1.setPatrolRange(100);
        enemy1.setPatrolSpeed(1.5);
        enemies.add(enemy1);

        PatrolEnemy enemy2 = new PatrolEnemy(800, 300, 64, 64);
        enemy2.setPatrolRange(80);
        enemy2.setPatrolSpeed(2.0);
        enemies.add(enemy2);

        // 创建射击敌人
        ShootingEnemy enemy3 = new ShootingEnemy(600, 350, 64, 64);
        enemies.add(enemy3);

        ShootingEnemy enemy4 = new ShootingEnemy(1000, 200, 64, 64);
        enemies.add(enemy4);

        // 创建跳跃敌人
        JumpingEnemy enemy5 = new JumpingEnemy(300, 550, 64, 64);
        enemies.add(enemy5);

        JumpingEnemy enemy6 = new JumpingEnemy(900, 200, 64, 64);
        enemies.add(enemy6);
    }

    public void update(double deltaTime) {
        // 更新输入
        inputManager.update();

        // 处理菜单状态
        handleMenuInput();

        if (menuState != MenuState.PLAYING || isPaused) {
            return;
        }

        gameTime += deltaTime;

        // 更新玩家
        player1.update(deltaTime);
        player2.update(deltaTime);

        // 更新敌人
        updateEnemies(deltaTime);

        // 检测碰撞
        checkCollisions();

        // 检查游戏状态
        checkGameState();
    }

    private void handleMenuInput() {
        // 处理暂停/继续
        if (inputManager.isAnyPausePressed() && inputManager.isKeyJustPressed(
                inputManager.isP1Pause() ? Config.KEY_P1_PAUSE :
                        inputManager.isP2Pause() ? Config.KEY_P2_PAUSE : Config.KEY_ESCAPE)) {

            if (menuState == MenuState.PLAYING) {
                togglePause();
            } else if (menuState == MenuState.PAUSE_MENU) {
                resumeGame();
            } else if (menuState == MenuState.MAIN_MENU) {
                startGame();
            }
        }

        // 处理返回主菜单
        if (inputManager.isBackPressed() && inputManager.isKeyJustPressed(Config.KEY_BACK)) {
            if (menuState == MenuState.PAUSE_MENU || menuState == MenuState.GAME_OVER ||
                    menuState == MenuState.LEVEL_COMPLETE) {
                returnToMainMenu();
            }
        }

        // 处理游戏内按键
        if (menuState == MenuState.PLAYING && !isPaused) {
            // R键重新开始
            if (inputManager.isKeyPressed(82)) { // R键
                restartGame();
            }
            // N键下一关
            if (inputManager.isKeyPressed(78)) { // N键
                nextLevel();
            }
        }
    }

    private void updateEnemies(double deltaTime) {
        List<Enemy> enemiesToRemove = new ArrayList<>();

        for (Enemy enemy : enemies) {
            enemy.update(deltaTime);

            // 更新敌人AI（朝向最近的玩家）
            Player targetPlayer = findNearestPlayer(enemy);
            if (targetPlayer != null && enemy instanceof ShootingEnemy) {
                ((ShootingEnemy) enemy).setTarget(targetPlayer);
            }

            // 检查敌人是否死亡
            if (!enemy.isActive()) {
                enemiesToRemove.add(enemy);
                // 给两个玩家都加分数
                player1.addScore(enemy.getScoreValue());
                player2.addScore(enemy.getScoreValue());
            }
        }

        enemies.removeAll(enemiesToRemove);
    }

    private Player findNearestPlayer(Enemy enemy) {
        double distToP1 = Math.sqrt(
                Math.pow(enemy.getX() - player1.getX(), 2) +
                        Math.pow(enemy.getY() - player1.getY(), 2)
        );

        double distToP2 = Math.sqrt(
                Math.pow(enemy.getX() - player2.getX(), 2) +
                        Math.pow(enemy.getY() - player2.getY(), 2)
        );

        return distToP1 < distToP2 ? player1 : player2;
    }

    private void checkCollisions() {
        // 检查玩家与敌人的碰撞
        for (Enemy enemy : enemies) {
            if (player1.intersects(enemy) && player1.isActive()) {
                player1.takeDamage(enemy.getDamage());
            }
            if (player2.intersects(enemy) && player2.isActive()) {
                player2.takeDamage(enemy.getDamage());
            }
        }

        // 检查敌人子弹与玩家的碰撞
        for (Enemy enemy : enemies) {
            if (enemy instanceof ShootingEnemy) {
                ShootingEnemy shootingEnemy = (ShootingEnemy) enemy;
                for (Bullet bullet : shootingEnemy.getBullets()) {
                    if (player1.intersects(bullet) && bullet.isActive()) {
                        player1.takeDamage(bullet.getDamage());
                        bullet.setActive(false);
                    }
                    if (player2.intersects(bullet) && bullet.isActive()) {
                        player2.takeDamage(bullet.getDamage());
                        bullet.setActive(false);
                    }
                }
            }
        }

        // 检查玩家子弹与敌人的碰撞
        checkBulletCollisions(player1);
        checkBulletCollisions(player2);

        // 检查玩家之间的碰撞（防止重叠）
        if (player1.intersects(player2)) {
            // 简单推开逻辑
            double dx = player1.getX() - player2.getX();
            if (dx != 0) {
                player1.setX(player1.getX() + dx * 0.1);
                player2.setX(player2.getX() - dx * 0.1);
            }
        }
    }

    private void checkBulletCollisions(Player player) {
        if (player.getCurrentWeapon() == null) return;

        List<Bullet> bulletsToRemove = new ArrayList<>();

        for (Bullet bullet : player.getCurrentWeapon().getBullets()) {
            for (Enemy enemy : enemies) {
                if (bullet.intersects(enemy) && bullet.isActive()) {
                    enemy.takeDamage(bullet.getDamage());

                    // 如果是爆炸性子弹，创建爆炸效果
                    if (bullet.isExplosive()) {
                        createExplosion(bullet.getX(), bullet.getY(), bullet.getExplosionRadius(), bullet.getDamage() / 2);
                    }

                    bullet.setActive(false);
                    bulletsToRemove.add(bullet);
                    break;
                }
            }
        }

        player.getCurrentWeapon().getBullets().removeAll(bulletsToRemove);
    }

    private void createExplosion(double x, double y, double radius, int damage) {
        // 对范围内的敌人造成伤害
        for (Enemy enemy : enemies) {
            double distance = Math.sqrt(
                    Math.pow(x - enemy.getX(), 2) +
                            Math.pow(y - enemy.getY(), 2)
            );

            if (distance <= radius) {
                // 距离越近伤害越高
                int actualDamage = (int)(damage * (1 - distance / radius));
                enemy.takeDamage(actualDamage);
            }
        }

        // TODO: 添加爆炸视觉效果
    }

    private void checkGameState() {
        // 检查玩家生命值
        if (player1.getHealth() <= 0 && player2.getHealth() <= 0) {
            menuState = MenuState.GAME_OVER;
            Config.GAME_STATE = GameState.GAME_OVER;
        }

        // 检查关卡完成（击败所有敌人）
        if (enemies.isEmpty()) {
            menuState = MenuState.LEVEL_COMPLETE;
            Config.GAME_STATE = GameState.LEVEL_COMPLETE;
        }
    }

    public void render() {
        // 清空画布
        gc.clearRect(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        if (menuState == MenuState.MAIN_MENU) {
            renderMainMenu();
            return;
        }

        // 渲染关卡背景
        if (currentLevel != null) {
            currentLevel.render(gc);
        }

        // 渲染敌人
        for (Enemy enemy : enemies) {
            enemy.render(gc);
        }

        // 渲染玩家
        player1.render(gc);
        player2.render(gc);

        // 渲染敌人子弹
        for (Enemy enemy : enemies) {
            if (enemy instanceof ShootingEnemy) {
                ShootingEnemy shootingEnemy = (ShootingEnemy) enemy;
                for (Bullet bullet : shootingEnemy.getBullets()) {
                    bullet.render(gc);
                }
            }
        }

        // 渲染游戏信息
        renderGameInfo();

        // 根据状态渲染相应菜单
        switch (menuState) {
            case PAUSE_MENU:
                renderPauseMenu();
                break;
            case GAME_OVER:
                renderGameOver();
                break;
            case LEVEL_COMPLETE:
                renderLevelComplete();
                break;
        }
    }

    private void renderMainMenu() {
        // 背景
        gc.setFill(Color.rgb(30, 30, 50));
        gc.fillRect(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        // 标题
        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Arial", 72));
        gc.fillText("灵斗罗", Config.WINDOW_WIDTH / 2 - 120, 150);

        // 副标题
        gc.setFill(Color.CYAN);
        gc.setFont(Font.font("Arial", 36));
        gc.fillText("双人合作射击游戏", Config.WINDOW_WIDTH / 2 - 180, 220);

        // 操作说明
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 24));
        gc.fillText("玩家1: WASD移动, K跳跃, J射击, U切换武器", Config.WINDOW_WIDTH / 2 - 250, 300);
        gc.fillText("玩家2: 方向键移动, 3跳跃, 2射击, 5切换武器", Config.WINDOW_WIDTH / 2 - 250, 340);
        gc.fillText("暂停: H(玩家1) 或 9(玩家2) 或 ESC", Config.WINDOW_WIDTH / 2 - 200, 380);
        gc.fillText("返回主菜单: I", Config.WINDOW_WIDTH / 2 - 100, 420);

        // 开始游戏提示
        gc.setFill(Color.LIME);
        gc.setFont(Font.font("Arial", 32));
        gc.fillText("按 H 或 9 或 ESC 开始游戏", Config.WINDOW_WIDTH / 2 - 200, 500);

        // 作者信息
        gc.setFill(Color.GRAY);
        gc.setFont(Font.font("Arial", 16));
        gc.fillText("© 2023 灵斗罗开发团队", Config.WINDOW_WIDTH / 2 - 100, 680);
    }

    private void renderGameInfo() {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 14));
        gc.fillText("时间: " + (int)gameTime + "秒", 10, 20);
        gc.fillText("关卡: " + (currentLevelIndex + 1), 10, 40);
        gc.fillText("剩余敌人: " + enemies.size(), 10, 60);
        gc.fillText("玩家1生命: " + player1.getHealth() + "/" + player1.getMaxHealth(), 10, 80);
        gc.fillText("玩家2生命: " + player2.getHealth() + "/" + player2.getMaxHealth(), 10, 100);
    }

    private void renderPauseMenu() {
        // 半透明背景
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        // 标题
        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Arial", 48));
        gc.fillText("游戏暂停", Config.WINDOW_WIDTH / 2 - 100, Config.WINDOW_HEIGHT / 2 - 100);

        // 选项
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 24));
        gc.fillText("继续游戏: 按 H 或 9 或 ESC", Config.WINDOW_WIDTH / 2 - 150, Config.WINDOW_HEIGHT / 2);
        gc.fillText("返回主菜单: 按 I", Config.WINDOW_WIDTH / 2 - 100, Config.WINDOW_HEIGHT / 2 + 40);
        gc.fillText("重新开始: 按 R", Config.WINDOW_WIDTH / 2 - 80, Config.WINDOW_HEIGHT / 2 + 80);
        gc.fillText("保存游戏: 按 S", Config.WINDOW_WIDTH / 2 - 80, Config.WINDOW_HEIGHT / 2 + 120);
        gc.fillText("加载游戏: 按 L", Config.WINDOW_WIDTH / 2 - 80, Config.WINDOW_HEIGHT / 2 + 160);
    }

    private void renderGameOver() {
        // 背景
        gc.setFill(Color.rgb(0, 0, 0, 0.8));
        gc.fillRect(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        // 标题
        gc.setFill(Color.RED);
        gc.setFont(Font.font("Arial", 48));
        gc.fillText("游戏结束", Config.WINDOW_WIDTH / 2 - 100, Config.WINDOW_HEIGHT / 2 - 100);

        // 分数
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 24));
        gc.fillText("玩家1分数: " + player1.getScore(), Config.WINDOW_WIDTH / 2 - 80, Config.WINDOW_HEIGHT / 2);
        gc.fillText("玩家2分数: " + player2.getScore(), Config.WINDOW_WIDTH / 2 - 80, Config.WINDOW_HEIGHT / 2 + 40);

        // 选项
        gc.setFill(Color.YELLOW);
        gc.fillText("重新开始: 按 R", Config.WINDOW_WIDTH / 2 - 80, Config.WINDOW_HEIGHT / 2 + 100);
        gc.fillText("返回主菜单: 按 I", Config.WINDOW_WIDTH / 2 - 100, Config.WINDOW_HEIGHT / 2 + 140);
    }

    private void renderLevelComplete() {
        // 背景
        gc.setFill(Color.rgb(0, 0, 0, 0.8));
        gc.fillRect(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        // 标题
        gc.setFill(Color.GREEN);
        gc.setFont(Font.font("Arial", 48));
        gc.fillText("关卡完成!", Config.WINDOW_WIDTH / 2 - 100, Config.WINDOW_HEIGHT / 2 - 100);

        // 统计信息
        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Arial", 24));
        gc.fillText("用时: " + (int)gameTime + "秒", Config.WINDOW_WIDTH / 2 - 60, Config.WINDOW_HEIGHT / 2);
        gc.fillText("玩家1分数: " + player1.getScore(), Config.WINDOW_WIDTH / 2 - 80, Config.WINDOW_HEIGHT / 2 + 40);
        gc.fillText("玩家2分数: " + player2.getScore(), Config.WINDOW_HEIGHT / 2 - 80, Config.WINDOW_HEIGHT / 2 + 80);

        // 选项
        gc.setFill(Color.CYAN);
        gc.fillText("下一关: 按 N", Config.WINDOW_HEIGHT / 2 - 60, Config.WINDOW_HEIGHT / 2 + 140);
        gc.fillText("返回主菜单: 按 I", Config.WINDOW_HEIGHT / 2 - 100, Config.WINDOW_HEIGHT / 2 + 180);
    }

    public void togglePause() {
        if (menuState == MenuState.PLAYING) {
            menuState = MenuState.PAUSE_MENU;
            isPaused = true;
            Config.GAME_STATE = GameState.PAUSED;
        }
    }

    public void resumeGame() {
        if (menuState == MenuState.PAUSE_MENU) {
            menuState = MenuState.PLAYING;
            isPaused = false;
            Config.GAME_STATE = GameState.PLAYING;
        }
    }

    public void startGame() {
        if (menuState == MenuState.MAIN_MENU) {
            menuState = MenuState.PLAYING;
            isPaused = false;
            Config.GAME_STATE = GameState.PLAYING;
            loadLevel(0);
        }
    }

    public void returnToMainMenu() {
        menuState = MenuState.MAIN_MENU;
        isPaused = false;
        Config.GAME_STATE = GameState.MENU;
    }

    public void restartGame() {
        initializeGame();
        menuState = MenuState.PLAYING;
        isPaused = false;
        Config.GAME_STATE = GameState.PLAYING;
    }

    public void nextLevel() {
        if (currentLevelIndex + 1 < levels.size()) {
            loadLevel(currentLevelIndex + 1);
        } else {
            // 没有更多关卡，返回主菜单
            returnToMainMenu();
        }
    }

    public void saveGame() {
        SaveSystem.saveGame(player1, player2, currentLevelIndex, gameTime);
    }

    public void loadGame() {
        SaveSystem.loadGame();
        // TODO: 从存档数据恢复游戏状态
    }

    // Getter方法
    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public InputManager getInputManager() {
        return inputManager;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public MenuState getMenuState() {
        return menuState;
    }
}