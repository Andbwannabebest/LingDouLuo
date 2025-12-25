package com.lingdouluo;

import com.lingdouluo.config.Config;
import com.lingdouluo.entity.Bullet;
import com.lingdouluo.entity.Enemy;
import com.lingdouluo.entity.Player;
import com.lingdouluo.entity.Entity;
import com.lingdouluo.level.Level;
import com.lingdouluo.save.SaveData;
import com.lingdouluo.save.SaveSystem;
import com.lingdouluo.physics.CollisionSystem;
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
        this.player1 = new Player("凌云", "warrior");
        this.player2 = new Player("星尘", "tech");
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
    }

    // 初始化敌人
    private void initEnemies() {
        enemies.add(new Enemy(500, 750, 100));
        enemies.add(new Enemy(800, 550, 150));
        enemies.add(new Enemy(1100, 350, 100));
        enemies.add(new Enemy(1400, 650, 150));
    }

    // 游戏主更新逻辑
    public void update() {
        if (isGameOver || isPaused) return;

        // 更新游戏时长
        playTime += (long) (deltaTime * 1000);

        // 更新玩家
        player1.update(deltaTime, this);
        player2.update(deltaTime, this);

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
        currentLevel.updatePlatforms(); // 调用public方法

        // 碰撞检测
        List<Entity> allEntities = new ArrayList<>();
        allEntities.add(player1);
        allEntities.add(player2);
        allEntities.addAll(enemies);
        allEntities.addAll(bullets);

        collisionSystem.processCollisions(collisionSystem.detectCollisions(allEntities));
        // 玩家-平台碰撞检测
        collisionSystem.checkPlayerPlatformCollision(player1, currentLevel.getPlatforms());
        collisionSystem.checkPlayerPlatformCollision(player2, currentLevel.getPlatforms());

        // 游戏结束检测
        if (player1.isDestroyed() && player2.isDestroyed()) {
            isGameOver = true;
        }
    }

    // 渲染游戏画面
    public void render(GraphicsContext gc) {
        // 背景
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, Config.SCREEN_WIDTH, Config.SCREEN_HEIGHT);

        // 渲染平台
        for (Level.Platform platform : currentLevel.getPlatforms()) {
            gc.setFill(platform.isSolid() ? Color.GRAY : Color.GRAY.deriveColor(0,1,1,0.5));
            gc.fillRect(platform.getX(), platform.getY(), platform.getWidth(), platform.getHeight());
            gc.setStroke(Color.WHITE);
            gc.strokeRect(platform.getX(), platform.getY(), platform.getWidth(), platform.getHeight());
        }

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
        } else if (isPaused) {
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(48));
            gc.fillText("PAUSED", Config.SCREEN_WIDTH/2 - 100, Config.SCREEN_HEIGHT/2);
        }
    }

    // 渲染游戏信息
    private void renderGameInfo(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(16));
        gc.fillText("得分: " + score, 20, 30);
        gc.fillText("时长: " + String.format("%02d:%02d", (playTime/1000)/60, (playTime/1000)%60), 20, 60);
        gc.fillText("关卡: " + currentLevel.getLevelName(), 20, 90);
        gc.fillText(player1.getName() + " HP: " + player1.getHealth(), 20, 120);
        gc.fillText(player2.getName() + " HP: " + player2.getHealth(), 20, 150);
    }

    // 子弹超出屏幕判断
    private boolean isBulletOutOfScreen(Bullet bullet) {
        return bullet.getX() < 0 || bullet.getX() > Config.SCREEN_WIDTH
                || bullet.getY() < 0 || bullet.getY() > Config.SCREEN_HEIGHT;
    }

    // 按键处理分发
    public void handleKeyPress(KeyEvent event) {
        player1.handleKeyPress(event);
        player2.handleKeyPress(event);
    }

    public void handleKeyRelease(KeyEvent event) {
        player1.handleKeyRelease(event);
        player2.handleKeyRelease(event);
    }

    // 存档/读档
    public boolean saveGame() {
        if (isGameOver) return false;

        SaveData saveData = new SaveData(
                player1.getX(), player1.getY(), player1.getHealth(), player1.getName(), player1.getCharacterType(),
                player2.getX(), player2.getY(), player2.getHealth(), player2.getName(), player2.getCharacterType(),
                currentLevel.getLevelName(), score, playTime
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
        return true;
    }

    // ==================== Getter & Setter ====================
    public boolean isGameOver() { return isGameOver; }
    public void setGameOver(boolean gameOver) { isGameOver = gameOver; }
    public boolean isPaused() { return isPaused; }
    public void setPaused(boolean paused) { isPaused = paused; }
    public void addBullet(Bullet bullet) { bullets.add(bullet); }
}