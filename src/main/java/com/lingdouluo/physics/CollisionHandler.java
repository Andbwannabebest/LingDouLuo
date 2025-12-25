package com.lingdouluo.physics;

import com.lingdouluo.entity.Entity;

/**
 * 碰撞处理器抽象类，定义碰撞回调方法
 */
public abstract class CollisionHandler {
    private CollisionLayer layerA;
    private CollisionLayer layerB;

    public CollisionHandler(CollisionLayer layerA, CollisionLayer layerB) {
        this.layerA = layerA;
        this.layerB = layerB;
    }

    /**
     * 碰撞开始时调用
     */
    public abstract void onCollisionBegin(Entity entityA, Entity entityB);

    /**
     * 碰撞持续时调用（每帧）
     */
    public void onCollisionStay(Entity entityA, Entity entityB) {}

    /**
     * 碰撞结束时调用
     */
    public void onCollisionEnd(Entity entityA, Entity entityB) {}

    // Getter方法
    public CollisionLayer getLayerA() {
        return layerA;
    }

    public CollisionLayer getLayerB() {
        return layerB;
    }
}