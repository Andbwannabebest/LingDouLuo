package lingdouluo.physics;

import lingdouluo.entity.Entity;
import lingdouluo.entity.Player;
import lingdouluo.entity.Enemy;
import lingdouluo.entity.Bullet;
import lingdouluo.level.Level;
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
     * 玩家-平台碰撞检测（改进版）
     */
    public void checkPlayerPlatformCollision(Player player, List<Level.Platform> platforms) {
        if (player.isDestroyed() || platforms == null || platforms.isEmpty()) return;

        boolean onPlatform = false;

        for (Level.Platform platform : platforms) {
            // 更精确的平台碰撞检测
            double playerBottom = player.getY() + player.getHeight();
            double platformTop = platform.getY();
            double playerTop = player.getY();
            double platformBottom = platform.getY() + platform.getHeight();

            // 检查玩家是否在平台正上方且正在下落
            if (player.getYVelocity() >= 0 && // 只有下落或静止时才能站在平台上
                    playerBottom <= platformTop + 10 && // 允许一定的穿透阈值
                    playerBottom >= platformTop - 5 &&
                    player.getX() + player.getWidth() > platform.getX() &&
                    player.getX() < platform.getX() + platform.getWidth()) {

                // 将玩家放置在平台顶部
                player.setY(platformTop - player.getHeight());
                player.setYVelocity(0);
                player.setOnPlatform(true);
                onPlatform = true;
                break;
            }
        }

        if (!onPlatform && player.getYVelocity() >= 0) {
            player.setOnPlatform(false);
        }
    }

    // 核心碰撞处理（修复子弹碰撞）
    private void handleCollision(Entity a, Entity b) {
        // 子弹-敌人碰撞
        if ((a instanceof Bullet && b instanceof Enemy) ||
                (a instanceof Enemy && b instanceof Bullet)) {

            Bullet bullet = (a instanceof Bullet) ? (Bullet) a : (Bullet) b;
            Enemy enemy = (a instanceof Enemy) ? (Enemy) a : (Enemy) b;

            if (!bullet.isDestroyed() && !enemy.isDestroyed()) {
                enemy.takeDamage(bullet.getDamage());
                bullet.setDestroyed(true);
            }
        }

        // 玩家-敌人碰撞
        if ((a instanceof Player && b instanceof Enemy) ||
                (a instanceof Enemy && b instanceof Player)) {

            Player player = (a instanceof Player) ? (Player) a : (Player) b;
            Enemy enemy = (a instanceof Enemy) ? (Enemy) a : (Enemy) b;

            if (!player.isDestroyed() && !enemy.isDestroyed()) {
                player.takeDamage(enemy.getDamage());

                // 添加击退效果
                if (player.getX() < enemy.getX()) {
                    player.setX(player.getX() - 20); // 向左击退
                } else {
                    player.setX(player.getX() + 20); // 向右击退
                }
            }
        }

        // 子弹-玩家碰撞（友军火力关闭）
        // if ((a instanceof Bullet && b instanceof Player) ||
        //     (a instanceof Player && b instanceof Bullet)) {
        //     // 可以在这里实现友军火力逻辑
        // }
    }

    // AABB碰撞判断（改进版，增加容错）
    private boolean isAABBCollision(Entity a, Entity b) {
        if (a.isDestroyed() || b.isDestroyed()) return false;

        return a.getX() < b.getX() + b.getWidth() &&
                a.getX() + a.getWidth() > b.getX() &&
                a.getY() < b.getY() + b.getHeight() &&
                a.getY() + a.getHeight() > b.getY();
    }

    /**
     * 精确的子弹碰撞检测（专门用于子弹）
     */
    public boolean checkBulletCollision(Bullet bullet, Enemy enemy) {
        if (bullet.isDestroyed() || enemy.isDestroyed()) return false;

        // 使用更精确的碰撞检测
        return bullet.getX() < enemy.getX() + enemy.getWidth() &&
                bullet.getX() + bullet.getWidth() > enemy.getX() &&
                bullet.getY() < enemy.getY() + enemy.getHeight() &&
                bullet.getY() + bullet.getHeight() > enemy.getY();
    }
}