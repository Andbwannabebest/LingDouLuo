// src/main/java/com/lingdouluo/entity/Platform.java
package com.lingdouluo.entity;

import com.lingdouluo.physics.CollisionResult;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Platform extends Entity {

    private Color color;
    private boolean isSolid;

    public Platform(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.color = Color.rgb(120, 80, 60); // 棕色平台
        this.isSolid = true;
        this.velocityX = 0;
        this.velocityY = 0;
    }

    @Override
    public void update(double deltaTime) {
        // 平台通常是静态的，不需要更新
        // 但可以用于移动平台
        if (velocityX != 0 || velocityY != 0) {
            x += velocityX * deltaTime;
            y += velocityY * deltaTime;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        // 绘制平台主体
        gc.setFill(color);
        gc.fillRect(x, y, width, height);

        // 绘制平台边框
        gc.setStroke(Color.rgb(80, 50, 40));
        gc.setLineWidth(2);
        gc.strokeRect(x, y, width, height);

        // 绘制平台纹理
        gc.setStroke(Color.rgb(140, 100, 80));
        gc.setLineWidth(1);
        for (int i = 0; i < width; i += 20) {
            gc.strokeLine(x + i, y, x + i, y + height);
        }
        for (int i = 0; i < height; i += 20) {
            gc.strokeLine(x, y + i, x + width, y + i);
        }
    }

    @Override
    public void handleCollision(CollisionResult collision) {
        // 平台处理碰撞（通常不需要做任何事情）
    }

    // Getter和Setter方法
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }

    public boolean isSolid() { return isSolid; }
    public void setSolid(boolean solid) { isSolid = solid; }

    public void setVelocityX(double velocityX) { this.velocityX = velocityX; }
    public void setVelocityY(double velocityY) { this.velocityY = velocityY; }
}