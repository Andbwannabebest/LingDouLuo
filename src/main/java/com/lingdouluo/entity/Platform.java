package com.lingdouluo.entity;

import com.lingdouluo.physics.CollisionLayer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 平台类：实现Entity接口，修复拼写错误与接口方法缺失问题
 */
public class Platform implements Entity {
    // 修正：成员变量拼写错误（heisnt → height）
    private float x; // 平台x坐标
    private float y; // 平台y坐标
    private float width; // 平台宽度
    private float height; // 平台高度（修正拼写错误）
    private boolean isSolid; // 是否为实心平台（能否站立）

    // 构造器：初始化平台所有属性
    public Platform(float x, float y, float width, float height, boolean isSolid) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height; // 修正：对应成员变量名
        this.isSolid = isSolid;
    }

    // ==================== 实现Entity接口的所有抽象方法 ====================
    @Override
    public double getX() {
        return x; // 转换为double，匹配接口返回类型
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
        return height; // 修正：返回正确的成员变量
    }

    @Override
    public CollisionLayer getCollisionLayer() {
        return CollisionLayer.PLATFORM; // 平台对应的碰撞层级
    }

    @Override
    public void update(double deltaTime) {
        // 平台默认无动态更新逻辑，空实现即可（如需移动平台，可在此扩展）
    }

    @Override
    public void render(GraphicsContext gc) {
        // 渲染平台：实心平台用不透明灰色，非实心用半透明灰色
        Color platformColor = isSolid ? Color.GRAY : Color.GRAY.deriveColor(0, 1, 1, 0.5);
        gc.setFill(platformColor);
        gc.fillRect(x, y, width, height);
        // 绘制平台边框，增强辨识度
        gc.setStroke(Color.WHITE);
        gc.strokeRect(x, y, width, height);
    }

    @Override
    public boolean isDestroyed() {
        return false; // 平台默认不会被销毁
    }

    @Override
    public void setDestroyed(boolean destroyed) {
        // 平台无需销毁，空实现即可
    }

    // ==================== Getter & Setter ====================
    public float getXFloat() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getYFloat() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidthFloat() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeightFloat() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isSolid() {
        return isSolid;
    }

    public void setSolid(boolean solid) {
        isSolid = solid;
    }

    // 平台特有方法：判断坐标点是否在平台内
    public boolean containsPoint(float pointX, float pointY) {
        return pointX >= x && pointX <= x + width
                && pointY >= y && pointY <= y + height;
    }
}