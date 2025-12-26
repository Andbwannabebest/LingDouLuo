// src/main/java/com/lingdouluo/entity/PatrolEnemy.java
package com.lingdouluo.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PatrolEnemy extends Enemy {

    private double patrolRange;
    private double patrolSpeed;
    private double patrolStartX;
    private double patrolDirection;

    public PatrolEnemy(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.patrolRange = 100;
        this.patrolSpeed = 1.5;
        this.patrolStartX = x;
        this.patrolDirection = 1; // 1表示向右，-1表示向左
        this.health = 30;
        this.maxHealth = 30;
        this.damage = 10;
        this.scoreValue = 100;
    }

    @Override
    public void update(double deltaTime) {
        if (!isActive) return;

        // 巡逻逻辑
        patrol(deltaTime);
        applyPhysics(deltaTime);
    }

    protected void patrol(double deltaTime) {
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

        // 更新位置 - 使用deltaTime确保不同帧率下移动一致
        double frameAdjust = 60.0 * deltaTime;
        x += velocityX * frameAdjust;
        y += velocityY * frameAdjust;

        // 边界检查 - 敌人不会掉出地图
        if (y > 600 - height) {
            y = 600 - height;
            velocityY = 0;
            isOnGround = true;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isActive) return;

        // 绘制敌人身体
        gc.setFill(Color.rgb(200, 100, 50)); // 橙色巡逻敌人
        gc.fillRect(x, y, width, height);

        // 绘制敌人眼睛
        gc.setFill(Color.WHITE);
        gc.fillOval(x + 10, y + 10, 12, 12);
        gc.fillOval(x + width - 22, y + 10, 12, 12);

        gc.setFill(Color.BLACK);
        gc.fillOval(x + 13, y + 13, 6, 6);
        gc.fillOval(x + width - 19, y + 13, 6, 6);

        // 绘制巡逻指示器
        if (patrolDirection > 0) {
            gc.setFill(Color.YELLOW);
            gc.fillRect(x + width, y + height/2 - 5, 8, 10);
        } else {
            gc.setFill(Color.YELLOW);
            gc.fillRect(x - 8, y + height/2 - 5, 8, 10);
        }

        // 绘制生命条
        drawHealthBar(gc);
    }

    // Getter和Setter
    public double getPatrolRange() { return patrolRange; }
    public void setPatrolRange(double patrolRange) { this.patrolRange = patrolRange; }

    public double getPatrolSpeed() { return patrolSpeed; }
    public void setPatrolSpeed(double patrolSpeed) { this.patrolSpeed = patrolSpeed; }
}