// src/main/java/com/lingdouluo/LingDouLuoGame.java
package com.lingdouluo;

import com.lingdouluo.config.Config;
import com.lingdouluo.entity.Player;
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
    private Player player;
    private Level currentLevel;
    private InputManager inputManager;
    private List<Level> levels;
    private int currentLevelIndex;

    private double gameTime;
    private boolean isPaused;

    public LingDouLuoGame(GraphicsContext gc) {
        this.gc = gc;
        this.inputManager = new InputManager();
        this.levels = new ArrayList<>();
        this.gameTime = 0;
        this.isPaused = false;

        initializeGame();
    }

    private void initializeGame() {
        // 初始化关卡
        createLevels();

        // 初始化玩家
        player = new Player(100, 500);
        player.setInputManager(inputManager);

        // 加载第一关
        loadLevel(0);
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

        // TODO: 添加敌人和道具
        // factoryLevel.addEnemy(...);
        // factoryLevel.addPowerUp(...);

        levels.add(factoryLevel);

        // TODO: 添加更多关卡
    }

    public void loadLevel(int index) {
        if (index >= 0 && index < levels.size()) {
            currentLevelIndex = index;
            currentLevel = levels.get(index);
            player.setPosition(currentLevel.getPlayerSpawnX(), currentLevel.getPlayerSpawnY());
            currentLevel.setPlayer(player);
            Config.GAME_STATE = GameState.PLAYING;
        }
    }

    public void update(double deltaTime) {
        if (isPaused || Config.GAME_STATE != GameState.PLAYING) {
            return;
        }

        gameTime += deltaTime;

        // 更新输入
        inputManager.update();

        // 更新玩家
        player.update(deltaTime);

        // 更新关卡
        if (currentLevel != null) {
            currentLevel.update(deltaTime);
        }

        // 检查游戏状态
        checkGameState();
    }

    public void render() {
        // 清空画布
        gc.clearRect(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        // 渲染关卡背景
        if (currentLevel != null) {
            currentLevel.render(gc);
        }

        // 渲染玩家
        player.render(gc);

        // 渲染游戏信息
        renderGameInfo();

        // 如果暂停，显示暂停菜单
        if (isPaused) {
            renderPauseMenu();
        }
    }

    private void renderGameInfo() {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 14));
        gc.fillText("FPS: " + (int)(60), 10, 20);
        gc.fillText("时间: " + (int)gameTime + "秒", 10, 40);
        gc.fillText("关卡: " + (currentLevelIndex + 1), 10, 60);
    }

    private void renderPauseMenu() {
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 48));
        gc.fillText("游戏暂停", Config.WINDOW_WIDTH / 2 - 100, Config.WINDOW_HEIGHT / 2 - 50);

        gc.setFont(Font.font("Arial", 24));
        gc.fillText("按 H 继续游戏", Config.WINDOW_WIDTH / 2 - 80, Config.WINDOW_HEIGHT / 2 + 20);
        gc.fillText("按 ESC 退出游戏", Config.WINDOW_WIDTH / 2 - 90, Config.WINDOW_HEIGHT / 2 + 60);
    }

    private void checkGameState() {
        // 检查玩家生命值
        if (player.getHealth() <= 0) {
            Config.GAME_STATE = GameState.GAME_OVER;
            // TODO: 显示游戏结束画面
        }

        // 检查关卡完成
        if (currentLevel != null && currentLevel.isCompleted()) {
            Config.GAME_STATE = GameState.LEVEL_COMPLETE;
            // TODO: 加载下一关或显示胜利画面
        }
    }

    public void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            Config.GAME_STATE = GameState.PAUSED;
        } else {
            Config.GAME_STATE = GameState.PLAYING;
        }
    }

    public void saveGame() {
        SaveSystem.saveGame(player, currentLevelIndex, gameTime);
    }

    public void loadGame() {
        SaveSystem.loadGame();
        // TODO: 从存档数据恢复游戏状态
    }

    // Getter方法
    public Player getPlayer() {
        return player;
    }

    public InputManager getInputManager() {
        return inputManager;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }
}