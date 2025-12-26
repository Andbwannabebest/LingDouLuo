package lingdouluo.entity;

import lingdouluo.config.Config;
import lingdouluo.physics.CollisionLayer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bullet implements Entity {
    // 成员变量
    private double x;
    private double y;
    private double width;
    private double height;
    private double direction;
    private int damage;
    private boolean isDestroyed;
    private double travelDistance;

    // 构造器
    public Bullet(double x, double y, double direction) {
        this.x = x;
        this.y = y;
        this.width = Config.BULLET_WIDTH;
        this.height = Config.BULLET_HEIGHT;
        this.direction = direction;
        this.damage = Config.BULLET_DAMAGE;
        this.isDestroyed = false;
        this.travelDistance = 0;
    }

    // 更新子弹状态
    @Override
    public void update(double deltaTime) {
        if (isDestroyed) return;

        // 计算子弹移动距离
        double moveStep = direction * Config.BULLET_SPEED * deltaTime;
        x += moveStep;
        travelDistance += Math.abs(moveStep);

        // 超出屏幕范围则销毁
        boolean isOutOfScreen = x < -width || x > Config.SCREEN_WIDTH + width
                || y < -height || y > Config.SCREEN_HEIGHT + height;
        boolean isExceedRange = travelDistance > Config.BULLET_MAX_RANGE;

        if (isOutOfScreen || isExceedRange) {
            setDestroyed(true);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isDestroyed) return;

        // 渲染子弹为黄色矩形
        gc.setFill(Color.YELLOW);
        gc.fillRect(x, y, width, height);

        // 添加子弹轨迹效果
        gc.setFill(Color.ORANGE);
        for (int i = 0; i < 3; i++) {
            double trailX = x - (i + 1) * direction * 5;
            if (trailX >= 0 && trailX <= Config.SCREEN_WIDTH) {
                gc.fillRect(trailX, y, 2, height);
            }
        }
    }

    // 碰撞检测（改进版）
    @Override
    public boolean isCollidingWith(Entity other) {
        if (this.isDestroyed() || other.isDestroyed()) return false;

        // 使用更精确的碰撞检测，增加碰撞框大小
        double expand = 2.0; // 扩大碰撞框

        return this.getX() - expand < other.getX() + other.getWidth() &&
                this.getX() + this.getWidth() + expand > other.getX() &&
                this.getY() - expand < other.getY() + other.getHeight() &&
                this.getY() + this.getHeight() + expand > other.getY();
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
    public CollisionLayer getCollisionLayer() { return CollisionLayer.BULLET; }
    @Override
    public boolean isDestroyed() { return isDestroyed; }
    @Override
    public void setDestroyed(boolean destroyed) { isDestroyed = destroyed; }

    // ==================== Getter方法 ====================
    public double getTravelDistance() { return travelDistance; }
    public int getDamage() { return damage; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setDirection(double direction) { this.direction = direction; }
}