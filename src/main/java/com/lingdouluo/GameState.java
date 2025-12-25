package com.lingdouluo;

import com.lingdouluo.config.Config;
import com.lingdouluo.entity.Bullet;
import com.lingdouluo.entity.Enemy;
import com.lingdouluo.entity.Entity;
import com.lingdouluo.entity.Platform;
import com.lingdouluo.entity.Player;
import com.lingdouluo.save.SaveData;
import com.lingdouluo.save.SaveSystem;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import com.lingdouluo.physics.CollisionSystem;
import com.lingdouluo.physics.CollisionResult;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * 游戏状态管理器，统一管理所有实体和游戏状态
 * 优化点：1. 修复碰撞调用匹配问题 2. 补充玩家控制方法 3. 优化存档保存反馈 4. 修复关卡加载逻辑 5. 增加子弹射程限制
 */
public class GameState {
    // 日志工具
    private static final Logger logger = Logger.getLogger(GameState.class.getName());

    // 玩家
    private Player player1;
    private Player player2;
    // 实体列表
    private List<Enemy> enemies;
    private List<Bullet> bullets;
    private List<Platform> platforms;
    // 游戏状态
    private String currentLevel;
    private long score;
    private long playTime;
    private boolean isGameOver;
    private boolean isPaused;
    // 物理系统
    private CollisionSystem physicsCollisionSystem;

    public GameState() {
        // 初始化默认玩家
        this.player1 = new Player("凌云", "warrior");
        this.player2 = new Player("星尘", "tech");
        // 初始化实体列表
        this.enemies = new ArrayList<>();
        this.bullets = new ArrayList<>();
        this.platforms = new ArrayList<>();
        // 初始化游戏状态
        this.currentLevel = Config.DEFAULT_LEVEL;
        this.score = 0;
        this.playTime = 0;
        this.isGameOver = false;
        this.isPaused = false;
        // 初始化物理碰撞系统
        this.physicsCollisionSystem = new CollisionSystem();
        // 加载默认关卡
        loadLevel(currentLevel);
    }

    /**
     * 从前端更新玩家和关卡配置
     */
    public void updateFromUI(String p1Name, String p2Name, String p1Char, String p2Char, String level) {
        this.player1.setName(p1Name);
        this.player1.setCharacterType(p1Char);
        this.player2.setName(p2Name);
        this.player2.setCharacterType(p2Char);
        this.currentLevel = level;
        // 重新加载关卡（重置玩家状态）
        resetPlayerState();
        loadLevel(level);
    }

    /**
     * 重置玩家状态（关卡切换/游戏重启时使用）
     */
    private void resetPlayerState() {
        // 重置玩家位置、血量、销毁状态
        player1.setX(Config.PLAYER_INIT_X);
        player1.setY(Config.PLAYER_INIT_Y);
        player1.setHealth(Config.PLAYER_INIT_HEALTH);
        player1.setDestroyed(false);

        player2.setX(Config.PLAYER_INIT_X + 100); // 与玩家1错开位置
        player2.setY(Config.PLAYER_INIT_Y);
        player2.setHealth(Config.PLAYER_INIT_HEALTH);
        player2.setDestroyed(false);

        // 清空子弹（切换关卡时子弹失效）
        bullets.clear();
    }

    /**
     * 加载关卡
     */
    private void loadLevel(String levelName) {
        // 清空原有实体
        enemies.clear();
        platforms.clear();

        // 统一大小写判断，避免"Factory"和"factory"加载失败
        String lowerLevelName = levelName.toLowerCase();

        // 工厂关卡（示例）
        if ("factory".equals(lowerLevelName)) {
            // 平台配置（5参数匹配Platform构造器）
            platforms.add(new Platform(300, 800, 200, 20, false));
            platforms.add(new Platform(600, 600, 150, 20, true));
            platforms.add(new Platform(900, 400, 200, 20, false));
            platforms.add(new Platform(1200, 700, 150, 20, true));

            // 敌人配置
            enemies.add(new Enemy(500, 750, 100));
            enemies.add(new Enemy(800, 550, 150));
            enemies.add(new Enemy(1100, 350, 100));
            enemies.add(new Enemy(1400, 650, 150));
            logger.info("工厂关卡加载完成，包含" + platforms.size() + "个平台，" + enemies.size() + "个敌人");
        }
        // 默认关卡
        else if (Config.DEFAULT_LEVEL.toLowerCase().equals(lowerLevelName)) {
            // 平台配置
            platforms.add(new Platform(200, 700, 250, 20, false));
            platforms.add(new Platform(500, 500, 200, 20, false));
            platforms.add(new Platform(800, 300, 250, 20, false));
            // 敌人配置
            enemies.add(new Enemy(400, 650, 80));
            enemies.add(new Enemy(700, 450, 120));
            logger.info("默认关卡加载完成，包含" + platforms.size() + "个平台，" + enemies.size() + "个敌人");
        } else {
            logger.warning("未知关卡：" + levelName + "，已加载默认关卡");
            loadLevel(Config.DEFAULT_LEVEL); // 加载默认关卡兜底
        }
    }

    /**
     * 玩家移动控制
     * @param player 目标玩家
     * @param direction 移动方向（-1=左，1=右，0=停止）
     */
    public void movePlayer(Player player, int direction, double deltaTime) {
        if (player.isDestroyed() || isGameOver || isPaused) {
            return;
        }
        double moveDistance = direction * Config.PLAYER_MOVE_SPEED * deltaTime;
        player.setX(player.getX() + moveDistance);
    }

    /**
     * 玩家跳跃控制
     * @param player 目标玩家
     */
    public void jumpPlayer(Player player) {
        if (player.isDestroyed() || isGameOver || isPaused || !player.isOnPlatform()) {
            return; // 仅在平台上可跳跃
        }
        player.setYVelocity(Config.PLAYER_JUMP_FORCE);
    }

    /**
     * 发射子弹
     */
    public void fireBullet(Player player, double direction) {
        if (player.isDestroyed() || isGameOver || isPaused) {
            return;
        }
        double bulletX = player.getX() + (direction > 0 ? player.getWidth() : -Config.BULLET_WIDTH);
        double bulletY = player.getY() + player.getHeight() / 2 - Config.BULLET_HEIGHT / 2;
        bullets.add(new Bullet(bulletX, bulletY, direction));
    }

    /**
     * 保存游戏
     * @return 是否保存成功
     */
    public boolean saveGame() {
        if (isGameOver) {
            logger.warning("游戏已结束，无法保存存档");
            return false;
        }
        SaveData saveData = new SaveData();
        // 保存玩家1状态
        saveData.setPlayer1X(player1.getX());
        saveData.setPlayer1Y(player1.getY());
        saveData.setPlayer1Health(player1.getHealth());
        // 保存玩家2状态
        saveData.setPlayer2X(player2.getX());
        saveData.setPlayer2Y(player2.getY());
        saveData.setPlayer2Health(player2.getHealth());
        // 保存关卡和分数
        saveData.setCurrentLevel(currentLevel);
        saveData.setScore(score);
        saveData.setPlayTime(playTime);

        // 处理保存结果
        boolean saveSuccess = SaveSystem.saveGame(saveData);
        if (saveSuccess) {
            logger.info("游戏存档保存成功");
        } else {
            logger.severe("游戏存档保存失败");
        }
        return saveSuccess;
    }

    /**
     * 加载游戏
     * @return 是否加载成功
     */
    public boolean loadGame() {
        SaveData saveData = SaveSystem.loadGame();
        if (saveData == null) {
            logger.warning("存档加载失败！未找到有效存档或存档已损坏");
            return false;
        }
        // 恢复玩家1状态
        player1.setX(saveData.getPlayer1X());
        player1.setY(saveData.getPlayer1Y());
        player1.setHealth(saveData.getPlayer1Health());
        // 恢复玩家2状态
        player2.setX(saveData.getPlayer2X());
        player2.setY(saveData.getPlayer2Y());
        player2.setHealth(saveData.getPlayer2Health());
        // 恢复关卡和分数
        this.currentLevel = saveData.getCurrentLevel();
        this.score = saveData.getScore();
        this.playTime = saveData.getPlayTime();
        // 重新加载关卡
        loadLevel(currentLevel);
        logger.info("游戏存档加载成功，当前关卡：" + currentLevel + "，得分：" + score);
        return true;
    }

    /**
     * 重启游戏
     */
    public void restartGame() {
        this.score = 0;
        this.playTime = 0;
        this.isGameOver = false;
        this.isPaused = false;
        // 重置玩家状态并加载默认关卡
        resetPlayerState();
        loadLevel(Config.DEFAULT_LEVEL);
        logger.info("游戏已重启");
    }

    /**
     * 更新游戏状态
     */
    public void update(double deltaTime) {
        if (isGameOver || isPaused) {
            return;
        }

        // 更新游戏时长（毫秒）
        playTime += (long) (deltaTime * 1000);

        // 1. 更新所有实体
        player1.update(deltaTime);
        player2.update(deltaTime);

        // 更新敌人（迭代器避免并发修改异常）
        Iterator<Enemy> enemyIt = enemies.iterator();
        while (enemyIt.hasNext()) {
            Enemy enemy = enemyIt.next();
            enemy.update(deltaTime);
            if (enemy.isDestroyed()) {
                enemyIt.remove();
                score += 100; // 击杀敌人加分
            }
        }

        // 更新子弹（迭代器避免并发修改异常，增加射程限制）
        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet bullet = bulletIt.next();
            bullet.update(deltaTime);
            // 子弹超出屏幕或达到最大射程则销毁
            boolean isOutOfScreen = bullet.getX() < 0 || bullet.getX() > Config.SCREEN_WIDTH
                    || bullet.getY() < 0 || bullet.getY() > Config.SCREEN_HEIGHT;
            boolean isExceedRange = bullet.getTravelDistance() > Config.BULLET_MAX_RANGE;
            if (bullet.isDestroyed() || isOutOfScreen || isExceedRange) {
                bulletIt.remove();
            }
        }

        // 更新平台（兼容Platform的update方法，空实现不影响）
        for (Platform platform : platforms) {
            platform.update(deltaTime);
        }

        // 2. 碰撞检测与处理（简化调用，使用批量处理方法）
        List<Entity> allEntities = new ArrayList<>();
        allEntities.add(player1);
        allEntities.add(player2);
        allEntities.addAll(enemies);
        allEntities.addAll(bullets);
        allEntities.addAll(platforms);

        List<CollisionResult> collisionResults = physicsCollisionSystem.detectCollisions(allEntities);
        physicsCollisionSystem.processCollisions(collisionResults); // 批量处理，更简洁

        // 3. 游戏结束检测（两个玩家都死亡）
        if (player1.isDestroyed() && player2.isDestroyed()) {
            isGameOver = true;
            logger.info("游戏结束！最终得分：" + score);
        }
    }

    /**
     * 渲染游戏画面
     */
    public void render(GraphicsContext gc) {
        // 1. 渲染背景（黑色背景，铺满整个屏幕）
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, Config.SCREEN_WIDTH, Config.SCREEN_HEIGHT);

        // 2. 渲染平台
        for (Platform platform : platforms) {
            platform.render(gc);
        }

        // 3. 渲染敌人
        for (Enemy enemy : enemies) {
            enemy.render(gc);
        }

        // 4. 渲染子弹
        for (Bullet bullet : bullets) {
            bullet.render(gc);
        }

        // 5. 渲染玩家
        player1.render(gc);
        player2.render(gc);

        // 6. 渲染游戏信息
        renderGameInfo(gc);

        // 7. 渲染游戏结束/暂停提示
        if (isGameOver) {
            gc.setFill(Color.RED);
            gc.setFont(Font.font(48)); // 放大字体，更醒目
            String gameOverText = "GAME OVER!";
            String scoreText = "得分: " + score;
            // 居中渲染
            double gameOverX = Config.SCREEN_WIDTH / 2 - gc.getFont().getSize() * gameOverText.length() / 4;
            double scoreX = Config.SCREEN_WIDTH / 2 - gc.getFont().getSize() * scoreText.length() / 4;
            gc.fillText(gameOverText, gameOverX, Config.SCREEN_HEIGHT / 2 - 50);
            gc.fillText(scoreText, scoreX, Config.SCREEN_HEIGHT / 2 + 20);
            gc.setFont(Font.font(12)); // 恢复默认字体
        } else if (isPaused) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(48));
            String pauseText = "PAUSED";
            double pauseX = Config.SCREEN_WIDTH / 2 - gc.getFont().getSize() * pauseText.length() / 4;
            gc.fillText(pauseText, pauseX, Config.SCREEN_HEIGHT / 2);
            gc.setFont(Font.font(12)); // 恢复默认字体
        }
    }

    /**
     * 渲染游戏信息（分数、时长等）
     */
    private void renderGameInfo(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(16));
        // 分数
        gc.fillText("得分: " + score, 20, 30);
        // 游戏时长（格式化分:秒）
        long minutes = (playTime / 1000) / 60;
        long seconds = (playTime / 1000) % 60;
        gc.fillText("时长: " + String.format("%02d:%02d", minutes, seconds), 20, 60);
        // 关卡
        gc.fillText("关卡: " + currentLevel, 20, 90);
        // 玩家1血量
        gc.fillText(player1.getName() + " HP: " + player1.getHealth(), 20, 120);
        // 玩家2血量
        gc.fillText(player2.getName() + " HP: " + player2.getHealth(), 20, 150);
        gc.setFont(Font.font(12)); // 恢复默认字体
    }

    // Getter & Setter
    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public long getScore() {
        return score;
    }

    public String getCurrentLevel() {
        return currentLevel;
    }

    public long getPlayTime() {
        return playTime;
    }
}