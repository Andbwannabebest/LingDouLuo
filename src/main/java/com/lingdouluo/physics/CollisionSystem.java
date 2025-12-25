// src/main/java/com/lingdouluo/physics/CollisionSystem.java
package com.lingdouluo.physics;

import com.lingdouluo.entity.Entity;

public class CollisionSystem {

    public CollisionResult checkCollision(Entity entity1, Entity entity2) {
        if (entity1 == null || entity2 == null || !entity1.isActive() || !entity2.isActive()) {
            return null;
        }

        // 简单的AABB碰撞检测
        if (entity1.getX() < entity2.getX() + entity2.getWidth() &&
                entity1.getX() + entity1.getWidth() > entity2.getX() &&
                entity1.getY() < entity2.getY() + entity2.getHeight() &&
                entity1.getY() + entity1.getHeight() > entity2.getY()) {

            // 计算碰撞法线
            double dx = (entity1.getX() + entity1.getWidth() / 2) -
                    (entity2.getX() + entity2.getWidth() / 2);
            double dy = (entity1.getY() + entity1.getHeight() / 2) -
                    (entity2.getY() + entity2.getHeight() / 2);

            double width = (entity1.getWidth() + entity2.getWidth()) / 2;
            double height = (entity1.getHeight() + entity2.getHeight()) / 2;

            double crossWidth = width * dy;
            double crossHeight = height * dx;

            CollisionResult result = new CollisionResult();
            result.setCollided(true);
            result.setEntity1(entity1);
            result.setEntity2(entity2);

            // 确定碰撞方向
            if (Math.abs(dx) <= width && Math.abs(dy) <= height) {
                if (crossWidth > crossHeight) {
                    if (crossWidth > -crossHeight) {
                        // 底部碰撞
                        result.setNormalY(-1);
                    } else {
                        // 左侧碰撞
                        result.setNormalX(1);
                    }
                } else {
                    if (crossWidth > -crossHeight) {
                        // 右侧碰撞
                        result.setNormalX(-1);
                    } else {
                        // 顶部碰撞
                        result.setNormalY(1);
                    }
                }
            }

            return result;
        }

        return null;
    }
}