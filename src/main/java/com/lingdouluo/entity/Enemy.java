package com.lingdouluo.entity;

import com.lingdouluo.config.Config;
import com.lingdouluo.physics.CollisionLayer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.lingdouluo.GameLoop; // 添加GameLoop导入

public class Enemy implements Entity {
    // 成员变量
    private double x;
    private double y;
    private double width;
    private double height;
    private int health;
    private double moveSpeed;
    private int direction; // 1=右，-1=左
    private boolean isDestroyed;

    // 构造器
    public Enemy(double x, double y, int health) {
        this.x = x;
        this.y = y;
        this.width = Config.ENEMY_WIDTH;
        this.height = Config.ENEMY_HEIGHT;
        this.health = health;
        this.moveSpeed = Config.ENEMY_MOVE_SPEED;
        this.direction = 1;
        this.isDestroyed = false;
    }

    // ==================== Entity接口实现 ====================
    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public CollisionLayer getCollisionLayer() {
        return CollisionLayer.ENEMY;
    }

    @Override
    public void update(double deltaTime) {
        if (isDestroyed) return;

        // 左右往返移动
        double moveDistance = direction * moveSpeed * deltaTime;
        x += moveDistance;

        // 边界检测
        if (x < 0) {
            x = 0;
            direction = 1;
        }
        if (x + width > Config.SCREEN_WIDTH) {
            x = Config.SCREEN_WIDTH - width;
            direction = -1;
        }
    }

    // 重载update方法（兼容AI追击逻辑）
    public void update(double deltaTime, Player player, GameLoop gameLoop) {
        this.update(deltaTime);
        // 简单AI：追击玩家
        if (player != null && !player.isDestroyed()) {
            if (player.getX() > x + width/2 && direction != 1) {
                direction = 1;
            } else if (player.getX() < x + width/2 && direction != -1) {
                direction = -1;
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isDestroyed) return;

        // 渲染敌人
        gc.setFill(Color.RED);
        gc.fillRect(x, y, width, height);

        // 渲染血量
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(12));
        gc.fillText("HP: " + health, x, y - 10);
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

    @Override
    public void setDestroyed(boolean destroyed) {
        isDestroyed = destroyed;
    }

    // ==================== 自定义方法 ====================
    public void takeDamage(int damage) {
        this.health -= damage;
        if (health <= 0) {
            setDestroyed(true);
        }
    }

    // ==================== Getter & Setter ====================
    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = Math.max(health, 0);
        if (this.health <= 0) {
            setDestroyed(true);
        }
    }

    public int getDamage() {
        return Config.ENEMY_ATTACK_DAMAGE;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}