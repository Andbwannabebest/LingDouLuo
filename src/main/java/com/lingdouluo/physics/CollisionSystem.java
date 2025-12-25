package com.lingdouluo.physics;

import com.lingdouluo.entity.Entity;
import com.lingdouluo.entity.Player;
import com.lingdouluo.entity.Enemy;
import com.lingdouluo.level.Level;
import java.util.List;
import java.util.ArrayList;

public class CollisionSystem {

    /**
     * AABB碰撞检测（所有实体）
     */
    public List<CollisionResult> detectCollisions(List<Entity> allEntities) {
        List<CollisionResult> collisionResults = new ArrayList<>();

        for (int i = 0; i < allEntities.size(); i++) {
            Entity entityA = allEntities.get(i);
            if (entityA.isDestroyed()) continue;

            for (int j = i + 1; j < allEntities.size(); j++) {
                Entity entityB = allEntities.get(j);
                if (entityB.isDestroyed()) continue;

                boolean isCollided = isAABBCollision(entityA, entityB);
                CollisionLayer layerA = entityA.getCollisionLayer();
                CollisionLayer layerB = entityB.getCollisionLayer();

                collisionResults.add(new CollisionResult(entityA, entityB, layerA, layerB, isCollided));
            }
        }
        return collisionResults;
    }

    /**
     * 批量处理碰撞结果
     */
    public void processCollisions(List<CollisionResult> results) {
        for (CollisionResult result : results) {
            if (result.isCollided()) {
                handleCollision(result.getEntityA(), result.getEntityB());
            }
        }
    }

    /**
     * 玩家-平台碰撞检测（单独提取，供外部调用）
     */
    public void checkPlayerPlatformCollision(Player player, List<Level.Platform> platforms) {
        if (player.isDestroyed() || platforms == null || platforms.isEmpty()) return;

        boolean onPlatform = false;
        for (Level.Platform platform : platforms) {
            // AABB碰撞判断（仅检测玩家下落时的碰撞）
            if (player.getYVelocity() > 0 &&
                    player.getX() < platform.getX() + platform.getWidth() &&
                    player.getX() + player.getWidth() > platform.getX() &&
                    player.getY() + player.getHeight() <= platform.getY() + 10 &&
                    player.getY() + player.getHeight() >= platform.getY()) {

                player.setOnPlatform(true);
                player.setYVelocity(0);
                player.setY(platform.getY() - player.getHeight());
                onPlatform = true;
                break;
            }
        }

        if (!onPlatform) {
            player.setOnPlatform(false);
        }
    }

    // 核心碰撞处理
    private void handleCollision(Entity a, Entity b) {
        // 子弹-敌人碰撞
        if ((a instanceof com.lingdouluo.entity.Bullet && b instanceof Enemy) ||
                (a instanceof Enemy && b instanceof com.lingdouluo.entity.Bullet)) {
            com.lingdouluo.entity.Bullet bullet = (a instanceof com.lingdouluo.entity.Bullet) ? (com.lingdouluo.entity.Bullet) a : (com.lingdouluo.entity.Bullet) b;
            Enemy enemy = (a instanceof Enemy) ? (Enemy) a : (Enemy) b;
            enemy.takeDamage(bullet.getDamage());
            bullet.setDestroyed(true);
        }

        // 玩家-敌人碰撞
        if ((a instanceof Player && b instanceof Enemy) ||
                (a instanceof Enemy && b instanceof Player)) {
            Player player = (a instanceof Player) ? (Player) a : (Player) b;
            Enemy enemy = (a instanceof Enemy) ? (Enemy) a : (Enemy) b;
            player.takeDamage(enemy.getDamage());
        }
    }

    // AABB碰撞判断
    private boolean isAABBCollision(Entity a, Entity b) {
        return a.getX() < b.getX() + b.getWidth() &&
                a.getX() + a.getWidth() > b.getX() &&
                a.getY() < b.getY() + b.getHeight() &&
                a.getY() + a.getHeight() > b.getY();
    }
}