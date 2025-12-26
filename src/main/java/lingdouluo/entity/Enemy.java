package lingdouluo.entity;

import lingdouluo.config.Config;
import lingdouluo.physics.CollisionLayer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lingdouluo.GameLoop;

public class Enemy implements Entity {
    // 成员变量
    private double x;
    private double y;
    private double width;
    private double height;
    private int health;
    private double moveSpeed;
    private int direction;
    private boolean isDestroyed;
    private Player targetPlayer;

    // 构造器
    public Enemy(double x, double y, int health) {
        this.x = x;
        this.y = y;
        this.width = Config.ENEMY_WIDTH;
        this.height = Config.ENEMY_HEIGHT;
        this.health = health;
        this.moveSpeed = Config.ENEMY_MOVE_SPEED;
        this.direction = 1;
        this.isDestroyed = false;
        this.targetPlayer = null;
    }

    // 基础更新
    @Override
    public void update(double deltaTime) {
        if (isDestroyed) return;

        // 如果没有目标，随机移动
        if (targetPlayer == null || targetPlayer.isDestroyed()) {
            // 左右往返移动
            double moveDistance = direction * moveSpeed * deltaTime;
            x += moveDistance;

            // 边界检测
            if (x < 0) {
                x = 0;
                direction = 1;
            }
            if (x + width > Config.SCREEN_WIDTH) {
                x = Config.SCREEN_WIDTH - width;
                direction = -1;
            }
        }
    }

    // 重载update方法（改进AI追击逻辑）
    public void update(double deltaTime, Player player, GameLoop gameLoop) {
        this.targetPlayer = player;

        if (isDestroyed) return;

        // 如果有目标玩家且目标存活，追踪玩家
        if (targetPlayer != null && !targetPlayer.isDestroyed()) {
            double playerCenterX = targetPlayer.getX() + targetPlayer.getWidth() / 2;
            double enemyCenterX = x + width / 2;

            // 计算方向
            if (playerCenterX > enemyCenterX + 10) { // 增加阈值避免抖动
                direction = 1;
            } else if (playerCenterX < enemyCenterX - 10) {
                direction = -1;
            }

            // 移动
            double moveDistance = direction * moveSpeed * deltaTime;
            x += moveDistance;

            // 边界检测
            if (x < 0) {
                x = 0;
            }
            if (x + width > Config.SCREEN_WIDTH) {
                x = Config.SCREEN_WIDTH - width;
            }
        } else {
            // 没有目标时使用基础移动逻辑
            update(deltaTime);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isDestroyed) return;

        // 渲染敌人
        gc.setFill(Color.RED);
        gc.fillRect(x, y, width, height);

        // 渲染血量
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(12));
        gc.fillText("HP: " + health, x, y - 10);

        // 渲染方向指示器
        if (direction > 0) {
            gc.setFill(Color.ORANGE);
            gc.fillRect(x + width - 5, y + height/2 - 2, 5, 4);
        } else {
            gc.setFill(Color.ORANGE);
            gc.fillRect(x, y + height/2 - 2, 5, 4);
        }
    }

    // 受伤处理
    public void takeDamage(int damage) {
        this.health -= damage;
        if (health <= 0) {
            setDestroyed(true);
        }
    }

    // 碰撞检测
    @Override
    public boolean isCollidingWith(Entity other) {
        return this.getX() < other.getX() + other.getWidth() &&
                this.getX() + this.getWidth() > other.getX() &&
                this.getY() < other.getY() + other.getHeight() &&
                this.getY() + this.getHeight() > other.getY();
    }

    // ==================== Entity接口实现 ====================
    @Override
    public double getX() { return x; }
    @Override
    public double getY() { return y; }
    @Override
    public double getWidth() { return width; }
    @Override
    public double getHeight() { return height; }
    @Override
    public CollisionLayer getCollisionLayer() { return CollisionLayer.ENEMY; }
    @Override
    public boolean isDestroyed() { return isDestroyed; }
    @Override
    public void setDestroyed(boolean destroyed) { isDestroyed = destroyed; }

    // ==================== 自定义方法 ====================
    public int getHealth() { return health; }
    public void setHealth(int health) {
        this.health = Math.max(health, 0);
        if (this.health <= 0) {
            setDestroyed(true);
        }
    }
    public int getDamage() { return Config.ENEMY_ATTACK_DAMAGE; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setTargetPlayer(Player player) { this.targetPlayer = player; }
}