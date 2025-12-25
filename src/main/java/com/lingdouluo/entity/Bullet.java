// src/main/java/com/lingdouluo/entity/Bullet.java
package com.lingdouluo.entity;

import com.lingdouluo.config.Config;
import com.lingdouluo.physics.CollisionResult;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bullet extends Entity {

    private int damage;
    private Color color;
    private boolean isExplosive;
    private double explosionRadius;
    private long creationTime;
    private static final long MAX_LIFETIME = 5000; // 5秒

    public Bullet(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.damage = 10;
        this.color = Color.YELLOW;
        this.isExplosive = false;
        this.explosionRadius = 0;
        this.creationTime = System.currentTimeMillis();
    }

    @Override
    public void update(double deltaTime) {
        if (!isActive) return;

        // 检查生命周期
        long currentTime = System.currentTimeMillis();
        if (currentTime - creationTime > MAX_LIFETIME) {
            isActive = false;
            return;
        }

        // 应用重力（如果是榴弹）
        if (isExplosive) {
            velocityY += Config.GRAVITY * 0.5;
        }

        // 更新位置
        x += velocityX;
        y += velocityY;

        // 边界检查
        if (x < -width || x > Config.WINDOW_WIDTH ||
                y < -height || y > Config.WINDOW_HEIGHT) {
            isActive = false;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isActive) return;

        gc.setFill(color);
        if (isExplosive) {
            // 绘制榴弹
            gc.fillOval(x, y, width, height);
            gc.setStroke(Color.DARKRED);
            gc.setLineWidth(1);
            gc.strokeOval(x, y, width, height);
        } else {
            // 绘制普通子弹
            gc.fillRect(x, y, width, height);
        }

        // 如果即将爆炸，绘制闪烁效果
        if (isExplosive && System.currentTimeMillis() % 200 < 100) {
            gc.setStroke(Color.YELLOW);
            gc.setLineWidth(2);
            gc.strokeOval(x - 2, y - 2, width + 4, height + 4);
        }
    }

    @Override
    public void handleCollision(CollisionResult collision) {
        // 子弹碰撞处理
        if (isExplosive) {
            explode();
        } else {
            isActive = false;
        }
    }

    private void explode() {
        // TODO: 实现爆炸效果
        // 1. 创建爆炸动画
        // 2. 对范围内的敌人造成伤害
        // 3. 播放爆炸音效

        isActive = false;
    }

    // Getter和Setter方法
    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }

    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }

    public boolean isExplosive() { return isExplosive; }
    public void setExplosive(boolean explosive) { isExplosive = explosive; }

    public double getExplosionRadius() { return explosionRadius; }
    public void setExplosionRadius(double explosionRadius) {
        this.explosionRadius = explosionRadius;
    }
}