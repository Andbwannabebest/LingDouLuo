package com.lingdouluo.entity;

import com.lingdouluo.physics.CollisionLayer;
import javafx.scene.canvas.GraphicsContext;

/**
 * 所有游戏实体的基类（接口），定义统一行为规范
 */
public interface Entity {
    // 获取实体X坐标
    double getX();

    // 获取实体Y坐标
    double getY();

    // 获取实体宽度
    double getWidth();

    // 获取实体高度
    double getHeight();

    // 获取碰撞层
    CollisionLayer getCollisionLayer();

    // 更新实体状态
    void update(double deltaTime);

    // 渲染实体
    void render(GraphicsContext gc);

    // 判断是否需要销毁
    boolean isDestroyed();

    // 设置销毁状态
    void setDestroyed(boolean destroyed);

    // 新增：判断当前实体是否与另一个实体碰撞（AABB碰撞算法）
    default boolean isCollidingWith(Entity other) {
        // 接口默认方法（Java 8+支持），无需每个实现类重复编写
        return this.getX() < other.getX() + other.getWidth() &&
                this.getX() + this.getWidth() > other.getX() &&
                this.getY() < other.getY() + other.getHeight() &&
                this.getY() + this.getHeight() > other.getY();
    }
}