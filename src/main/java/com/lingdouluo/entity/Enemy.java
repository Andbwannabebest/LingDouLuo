// src/main/java/com/lingdouluo/entity/Enemy.java
package com.lingdouluo.entity;

import com.lingdouluo.physics.CollisionResult;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Enemy extends Entity {

    protected int health;
    protected int maxHealth;
    protected int damage;
    protected int scoreValue;
    protected boolean isAggressive;
    protected double patrolRange;
    protected double patrolSpeed;
    protected double detectionRange;

    protected double patrolStartX;
    protected double patrolDirection;

    public Enemy(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.maxHealth = 20;
        this.health = maxHealth;
        this.damage = 10;
        this.scoreValue = 100;
        this.isAggressive = true;
        this.patrolRange = 100;
        this.patrolSpeed = 1.0;
        this.detectionRange = 200;

        this.patrolStartX = x;
        this.patrolDirection = 1; // 1表示向右，-1表示向左
    }

    public Enemy() {
        super();
    }

    @Override
    public void update(double deltaTime) {
        if (!isActive) return;

        patrol(deltaTime);
        applyPhysics(deltaTime);
        updateAI(deltaTime);
    }

    protected void patrol(double deltaTime) {
        // 简单的巡逻逻辑
        if (patrolRange > 0) {
            velocityX = patrolDirection * patrolSpeed;

            // 检查是否到达巡逻边界
            if (x > patrolStartX + patrolRange) {
                patrolDirection = -1;
                velocityX = 0;
            } else if (x < patrolStartX - patrolRange) {
                patrolDirection = 1;
                velocityX = 0;
            }
        }
    }

    protected void applyPhysics(double deltaTime) {
        // 应用重力
        if (!isOnGround) {
            velocityY += 0.5;
        }

        // 更新位置
        x += velocityX * deltaTime;
        y += velocityY * deltaTime;
    }

    protected void updateAI(double deltaTime) {
        // 子类可以重写这个方法来实现特定的AI行为
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isActive) return;

        // 绘制敌人身体
        gc.setFill(Color.rgb(200, 50, 50)); // 红色敌人
        gc.fillRect(x, y, width, height);

        // 绘制敌人眼睛
        gc.setFill(Color.WHITE);
        gc.fillOval(x + 5, y + 5, 8, 8);
        gc.fillOval(x + width - 13, y + 5, 8, 8);

        gc.setFill(Color.BLACK);
        gc.fillOval(x + 7, y + 7, 4, 4);
        gc.fillOval(x + width - 11, y + 7, 4, 4);

        // 绘制生命条
        drawHealthBar(gc);
    }

    protected void drawHealthBar(GraphicsContext gc) {
        double healthPercent = (double) health / maxHealth;
        double barWidth = width;
        double barHeight = 4;
        double barX = x;
        double barY = y - 8;

        // 背景
        gc.setFill(Color.rgb(50, 50, 50));
        gc.fillRect(barX, barY, barWidth, barHeight);

        // 生命值
        if (healthPercent > 0.6) {
            gc.setFill(Color.GREEN);
        } else if (healthPercent > 0.3) {
            gc.setFill(Color.YELLOW);
        } else {
            gc.setFill(Color.RED);
        }
        gc.fillRect(barX, barY, barWidth * healthPercent, barHeight);

        // 边框
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRect(barX, barY, barWidth, barHeight);
    }

    @Override
    public void handleCollision(CollisionResult collision) {
        // 处理碰撞
        switch (collision.getType()) {
            case PLATFORM:
                handlePlatformCollision(collision);
                break;
            case BULLET:
                takeDamage(collision.getDamage());
                break;
            case PLAYER:
                // 攻击玩家
                break;
        }
    }

    protected void handlePlatformCollision(CollisionResult collision) {
        // 平台碰撞处理
        if (collision.getNormalY() < 0) { // 从上方碰撞
            y = collision.getEntityY() - height;
            velocityY = 0;
            isOnGround = true;
        } else if (collision.getNormalY() > 0) { // 从下方碰撞
            y = collision.getEntityY() + collision.getEntityHeight();
            velocityY = 0;
        }

        if (collision.getNormalX() != 0) { // 水平碰撞
            // 改变巡逻方向
            patrolDirection *= -1;
        }
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            isActive = false;
            // TODO: 播放死亡动画和音效
        }
    }

    // Getter和Setter方法
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getDamage() { return damage; }
    public int getScoreValue() { return scoreValue; }

    public void setHealth(int health) { this.health = health; }
    public void setDamage(int damage) { this.damage = damage; }
    public void setScoreValue(int scoreValue) { this.scoreValue = scoreValue; }
}