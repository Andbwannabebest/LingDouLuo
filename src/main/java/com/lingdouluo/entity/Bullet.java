package com.lingdouluo.entity;

import com.lingdouluo.config.Config;
import com.lingdouluo.physics.CollisionLayer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bullet implements Entity {
    // 成员变量
    private double x;
    private double y;
    private double width;
    private double height;
    private double direction; // 1=右，-1=左
    private int damage;
    private boolean isDestroyed;
    private double travelDistance; // 子弹飞行距离（新增：解决找不到getTravelDistance()的错误）

    // 构造器
    public Bullet(double x, double y, double direction) {
        this.x = x;
        this.y = y;
        this.width = Config.BULLET_WIDTH;
        this.height = Config.BULLET_HEIGHT;
        this.direction = direction;
        this.damage = Config.BULLET_DAMAGE;
        this.isDestroyed = false;
        this.travelDistance = 0; // 初始化飞行距离为0
    }

    // ==================== 补充缺失的getter方法 ====================
    // 获取子弹飞行距离（解决找不到getTravelDistance()的错误）
    public double getTravelDistance() {
        return travelDistance;
    }

    // 获取子弹伤害值
    public int getDamage() {
        return damage;
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
        return CollisionLayer.BULLET;
    }

    @Override
    public void update(double deltaTime) {
        if (isDestroyed) return;

        // 计算子弹移动距离
        double moveStep = direction * Config.BULLET_SPEED * deltaTime;
        x += moveStep;
        // 累加飞行距离（取绝对值，无论左右都计算）
        travelDistance += Math.abs(moveStep);

        // 超出屏幕范围则销毁
        boolean isOutOfScreen = x < 0 || x > Config.SCREEN_WIDTH
                || y < 0 || y > Config.SCREEN_HEIGHT;
        if (isOutOfScreen) {
            setDestroyed(true);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isDestroyed) return;

        // 渲染子弹为黄色矩形
        gc.setFill(Color.YELLOW);
        gc.fillRect(x, y, width, height);
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

    @Override
    public void setDestroyed(boolean destroyed) {
        isDestroyed = destroyed;
    }

    // ==================== 其他setter方法（可选） ====================
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }
}