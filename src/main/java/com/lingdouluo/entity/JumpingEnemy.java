// src/main/java/com/lingdouluo/entity/JumpingEnemy.java
package com.lingdouluo.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class JumpingEnemy extends Enemy {

    private long lastJumpTime;
    private static final long JUMP_INTERVAL = 2000; // 毫秒

    public JumpingEnemy(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.health = 25;
        this.maxHealth = 25;
        this.damage = 20;
        this.scoreValue = 200;
        this.lastJumpTime = 0;
    }

    @Override
    public void update(double deltaTime) {
        if (!isActive) return;

        applyPhysics(deltaTime);

        // 尝试跳跃
        tryJump();
    }

    private void tryJump() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastJumpTime > JUMP_INTERVAL && isOnGround) {
            // 随机跳跃方向和力度
            double jumpForce = -8 + Math.random() * 4;
            double jumpHorizontal = (Math.random() - 0.5) * 3;

            velocityY = jumpForce;
            velocityX = jumpHorizontal;
            isOnGround = false;

            lastJumpTime = currentTime;
        }
    }

    protected void applyPhysics(double deltaTime) {
        // 应用重力
        if (!isOnGround) {
            velocityY += 0.5;
        }

        // 限制水平速度
        if (velocityX > 2) velocityX = 2;
        if (velocityX < -2) velocityX = -2;

        // 更新位置
        x += velocityX * deltaTime * 60;
        y += velocityY * deltaTime * 60;

        // 边界反弹
        if (x < 0 || x > 1280 - width) {
            velocityX *= -0.8;
            if (x < 0) x = 0;
            if (x > 1280 - width) x = 1280 - width;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isActive) return;

        // 绘制敌人身体
        gc.setFill(Color.rgb(100, 200, 100)); // 绿色跳跃敌人
        gc.fillRect(x, y, width, height);

        // 绘制弹簧腿
        gc.setFill(Color.rgb(150, 150, 150));
        gc.fillRect(x + 15, y + height, 8, 10);
        gc.fillRect(x + width - 23, y + height, 8, 10);

        // 绘制眼睛
        gc.setFill(Color.WHITE);
        gc.fillOval(x + 15, y + 15, 10, 10);
        gc.fillOval(x + width - 25, y + 15, 10, 10);

        gc.setFill(Color.BLACK);
        gc.fillOval(x + 18, y + 18, 4, 4);
        gc.fillOval(x + width - 22, y + 18, 4, 4);

        // 如果正在跳跃，显示跳跃效果
        if (!isOnGround) {
            gc.setFill(Color.rgb(255, 255, 0, 0.5));
            gc.fillOval(x - 5, y - 5, width + 10, height + 10);
        }

        // 绘制生命条
        drawHealthBar(gc);
    }
}