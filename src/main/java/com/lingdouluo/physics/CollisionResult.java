// src/main/java/com/lingdouluo/physics/CollisionResult.java
package com.lingdouluo.physics;

import com.lingdouluo.entity.Entity;

public class CollisionResult {

    public enum CollisionType {
        PLATFORM, ENEMY, PLAYER, BULLET, POWER_UP, NONE
    }

    private boolean collided;
    private Entity entity1;
    private Entity entity2;
    private double normalX;
    private double normalY;
    private CollisionType type;
    private int damage;

    public CollisionResult() {
        this.collided = false;
        this.normalX = 0;
        this.normalY = 0;
        this.type = CollisionType.NONE;
        this.damage = 0;
    }

    // Getter和Setter方法
    public boolean isCollided() { return collided; }
    public void setCollided(boolean collided) { this.collided = collided; }

    public Entity getEntity1() { return entity1; }
    public void setEntity1(Entity entity1) { this.entity1 = entity1; }

    public Entity getEntity2() { return entity2; }
    public void setEntity2(Entity entity2) { this.entity2 = entity2; }

    public double getNormalX() { return normalX; }
    public void setNormalX(double normalX) { this.normalX = normalX; }

    public double getNormalY() { return normalY; }
    public void setNormalY(double normalY) { this.normalY = normalY; }

    public CollisionType getType() { return type; }
    public void setType(CollisionType type) { this.type = type; }

    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }

    // 辅助方法
    public double getEntityX() {
        return entity2 != null ? entity2.getX() : 0;
    }

    public double getEntityY() {
        return entity2 != null ? entity2.getY() : 0;
    }

    public double getEntityWidth() {
        return entity2 != null ? entity2.getWidth() : 0;
    }

    public double getEntityHeight() {
        return entity2 != null ? entity2.getHeight() : 0;
    }

    public Entity getSource() {
        return entity1;
    }

    public Entity getTarget() {
        return entity2;
    }
}