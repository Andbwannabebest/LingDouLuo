package com.lingdouluo.physics;

import com.lingdouluo.entity.Entity;

/**
 * 碰撞检测结果封装类
 */
public class CollisionResult {
    private Entity entityA;
    private Entity entityB;
    private boolean collided;
    private CollisionLayer layerA;
    private CollisionLayer layerB;

    public CollisionResult(Entity entityA, Entity entityB, CollisionLayer layerA, CollisionLayer layerB, boolean collided) {
        this.entityA = entityA;
        this.entityB = entityB;
        this.layerA = layerA;
        this.layerB = layerB;
        this.collided = collided;
    }

    // Getter方法
    public Entity getEntityA() {
        return entityA;
    }

    public Entity getEntityB() {
        return entityB;
    }

    public boolean isCollided() {
        return collided;
    }

    public CollisionLayer getLayerA() {
        return layerA;
    }

    public CollisionLayer getLayerB() {
        return layerB;
    }
}