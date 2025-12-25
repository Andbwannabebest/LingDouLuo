// src/main/java/com/lingdouluo/level/Level.java
package com.lingdouluo.level;

import com.lingdouluo.entity.*;
import com.lingdouluo.physics.CollisionSystem;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class Level {

    private String name;
    private Color backgroundColor;
    private List<Platform> platforms;
    private List<PatrolEnemy> enemies;
    private List<Entity> entities;
    private Player player;

    private CollisionSystem collisionSystem;

    private double playerSpawnX;
    private double playerSpawnY;

    private boolean isCompleted;
    private boolean isBossLevel;

    public Level(String name) {
        this.name = name;
        this.backgroundColor = Color.rgb(30, 30, 40);
        this.platforms = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.entities = new ArrayList<>();
        this.collisionSystem = new CollisionSystem();

        this.playerSpawnX = 100;
        this.playerSpawnY = 500;
        this.isCompleted = false;
        this.isBossLevel = false;
    }

    public void update(double deltaTime) {
        if (player == null) return;

        // 更新玩家
        player.update(deltaTime);

        // 更新敌人
        List<PatrolEnemy> enemiesToRemove = new ArrayList<>();
        for (PatrolEnemy enemy : enemies) {
            enemy.update(deltaTime);

            // 检查敌人是否死亡
            if (!enemy.isActive()) {
                enemiesToRemove.add(enemy);
                // 如果玩家存在，增加分数
                if (player != null) {
                    // player.addScore(enemy.getScoreValue());
                }
            }
        }
        enemies.removeAll(enemiesToRemove);

        // 检测碰撞
        checkCollisions();

        // 检查关卡完成条件
        checkCompletion();
    }

    public void render(GraphicsContext gc) {
        // 绘制背景
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, 1280, 720);

        // 绘制平台
        for (Platform platform : platforms) {
            platform.render(gc);
        }

        // 绘制敌人
        for (PatrolEnemy enemy : enemies) {
            enemy.render(gc);
        }

        // 绘制其他实体
        for (Entity entity : entities) {
            entity.render(gc);
        }

        // 绘制关卡边界（调试用）
        gc.setStroke(Color.rgb(100, 100, 100, 0.3));
        gc.setLineWidth(2);
        gc.strokeRect(0, 0, 1280, 720);
    }

    private void checkCollisions() {
        if (player == null) return;

        // 检查玩家与平台的碰撞
        for (Platform platform : platforms) {
            if (player.intersects(platform)) {
                // 这里需要创建一个碰撞结果对象
                // 在实际游戏中，应该有更完善的碰撞系统
            }
        }

        // 检查玩家武器的子弹与敌人的碰撞
        if (player.getCurrentWeapon() != null) {
            List<Bullet> bulletsToRemove = new ArrayList<>();

            for (Bullet bullet : player.getCurrentWeapon().getBullets()) {
                // 检查子弹与每个敌人的碰撞
                for (PatrolEnemy enemy : enemies) {
                    if (bullet.intersects(enemy) && bullet.isActive()) {
                        // 敌人受到伤害
                        enemy.takeDamage(bullet.getDamage());
                        bullet.setActive(false);
                        bulletsToRemove.add(bullet);
                        break; // 子弹命中后不再检查其他敌人
                    }
                }
            }

            // 移除已命中的子弹
            player.getCurrentWeapon().getBullets().removeAll(bulletsToRemove);
        }
    }

    private void checkCompletion() {
        // 关卡完成条件：击败所有敌人
        if (enemies.isEmpty() && !isBossLevel) {
            isCompleted = true;
        }
    }

    // 添加游戏元素的方法
    public void addPlatform(double x, double y, double width, double height) {
        Platform platform = new Platform(x, y, width, height);
        platforms.add(platform);
        entities.add(platform);
    }

    public void addEnemy(PatrolEnemy enemy) {
        enemies.add(enemy);
        entities.add(enemy);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    // Getter和Setter方法
    public String getName() { return name; }
    public Color getBackgroundColor() { return backgroundColor; }
    public Player getPlayer() { return player; }
    public double getPlayerSpawnX() { return playerSpawnX; }
    public double getPlayerSpawnY() { return playerSpawnY; }
    public boolean isCompleted() { return isCompleted; }
    public boolean isBossLevel() { return isBossLevel; }

    public void setPlayer(Player player) {
        this.player = player;
        this.player.setPosition(playerSpawnX, playerSpawnY);
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setPlayerSpawn(double x, double y) {
        this.playerSpawnX = x;
        this.playerSpawnY = y;
    }

    public void setBossLevel(boolean bossLevel) {
        isBossLevel = bossLevel;
    }
}