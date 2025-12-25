// src/main/java/com/lingdouluo/entity/Entity.java
package com.lingdouluo.entity;

import com.lingdouluo.physics.CollisionResult;
import javafx.scene.canvas.GraphicsContext;

public abstract class Entity {
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected double velocityX;
    protected double velocityY;
    protected boolean isActive;
    protected boolean isOnGround;

    public Entity(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocityX = 0;
        this.velocityY = 0;
        this.isActive = true;
        this.isOnGround = false;
    }

    public abstract void update(double deltaTime);
    public abstract void render(GraphicsContext gc);
    public abstract void handleCollision(CollisionResult collision);

    // 位置和大小相关的方法
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // 速度相关的方法
    public double getVelocityX() { return velocityX; }
    public double getVelocityY() { return velocityY; }
    public void setVelocityX(double velocityX) { this.velocityX = velocityX; }
    public void setVelocityY(double velocityY) { this.velocityY = velocityY; }

    // 状态相关的方法
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public boolean isOnGround() { return isOnGround; }
    public void setOnGround(boolean onGround) { isOnGround = onGround; }

    // 碰撞检测辅助方法
    public boolean intersects(Entity other) {
        return x < other.x + other.width &&
                x + width > other.x &&
                y < other.y + other.height &&
                y + height > other.y;
    }

    public double getCenterX() {
        return x + width / 2;
    }

    public double getCenterY() {
        return y + height / 2;
    }
}