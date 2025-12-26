package lingdouluo.entity;



import lingdouluo.config.Config;
import lingdouluo.physics.CollisionLayer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Player implements Entity {
    // 现有成员变量保持不变...
    private String name;
    private String characterType;
    private double x, y, width, height;
    private int health, maxHealth;
    private double yVelocity;
    private boolean isDestroyed, isOnPlatform;
    private boolean isMovingLeft = false, isMovingRight = false, facingRight = true;
    private Weapon currentWeapon;

    // 添加平台列表成员变量
    private List<Platform> platforms;
    private boolean wasOnPlatform = false;  // 上一帧是否在平台上
    // 新增：图片资源相关
    private Map<String, Image> playerImages;
    private String currentAction;
    private String playerId; // "player1" 或 "player2"
    private long lastAnimationUpdate;
    private int currentFrameIndex;

    // 修改构造器
    public Player(String playerId, String name, String characterType) {
        this.playerId = playerId;
        this.name = name;
        this.characterType = characterType;
        this.x = Config.PLAYER_INIT_X;
        this.y = Config.PLAYER_INIT_Y;
        this.width = Config.PLAYER_WIDTH;
        this.height = Config.PLAYER_HEIGHT;
        this.maxHealth = Config.PLAYER_INIT_HEALTH;
        this.health = this.maxHealth;
        this.yVelocity = 0;
        this.isDestroyed = false;
        this.isOnPlatform = false;

        this.currentWeapon = new RifleWeapon();

        // 初始化图片资源
        this.playerImages = new HashMap<>();
        this.currentAction = "initialize";
        this.lastAnimationUpdate = System.currentTimeMillis();
        this.currentFrameIndex = 0;
        loadPlayerImages();
    }

    // 或者添加setter方法
    public void setPlatforms(List<Platform> platforms) {
        this.platforms = platforms;
    }

    // 新增：加载玩家图片资源
    private void loadPlayerImages() {
        try {
            // 加载所有动作图片
            String[] actions = {"initialize", "run1", "run2", "run3", "jump1", "jump2", "jump3", "up", "down"};
            String[] directions = {"left", "right"};

            for (String action : actions) {
                for (String direction : directions) {
                    String imagePath = "/" + playerId + "/" + direction + "/" + action + ".png";
                    Image image = new Image(getClass().getResourceAsStream(imagePath));
                    String key = direction + "_" + action;
                    playerImages.put(key, image);
                }
            }
        } catch (Exception e) {
            System.err.println("加载玩家图片失败: " + e.getMessage());
            // 如果图片加载失败，游戏仍能正常运行（使用矩形渲染）
        }
    }

    // 修改update方法，添加动画状态更新
    @Override
    public void update(double deltaTime) {
        if (isDestroyed) return;

        // 水平移动
        double moveSpeed = 0;
        if (isMovingLeft) moveSpeed -= Config.PLAYER_MOVE_SPEED;
        if (isMovingRight) moveSpeed += Config.PLAYER_MOVE_SPEED;
        x += moveSpeed * deltaTime;

        // 更新朝向
        if (moveSpeed > 0) {
            facingRight = true;
        } else if (moveSpeed < 0) {
            facingRight = false;
        }

        // 重力和垂直移动
        yVelocity += Config.PLAYER_GRAVITY * deltaTime;
        y += yVelocity * deltaTime;

        // 重置平台状态
        isOnPlatform = false;

        // 平台碰撞检测
        if (platforms != null) {
            for (Platform platform : platforms) {
                if (checkPlatformCollision(platform, yVelocity)) {
                    isOnPlatform = true;
                    yVelocity = 0;
                    y = platform.getY() - height;
                    break; // 找到一个平台就停止检测
                }
            }
        }

        // 边界检测（作为备用地板）
        boolean onGround = y + height >= Config.SCREEN_HEIGHT;
        if (onGround) {
            y = Config.SCREEN_HEIGHT - height;
            yVelocity = 0;
            isOnPlatform = true;
        }

        // 边界限制
        if (x < 0) x = 0;
        if (x + width > Config.SCREEN_WIDTH) x = Config.SCREEN_WIDTH - width;
        if (y < 0) {
            y = 0;
            yVelocity = 0;
        }

        // 更新动画状态
        updateAnimationState();
    }

    private boolean checkPlatformCollision(Platform platform, double currentYVelocity) {
        // 简单的平台碰撞检测
        boolean horizontalOverlap = x < platform.getX() + platform.getWidth() &&
                x + width > platform.getX();

        // 检测玩家底部是否接近平台顶部
        double playerBottom = y + height;
        double platformTop = platform.getY();

        // 当玩家底部接近平台顶部，且正在下落时
        boolean verticallyClose = playerBottom >= platformTop - 5 &&
                playerBottom <= platformTop + 5;

        return horizontalOverlap && verticallyClose && currentYVelocity >= 0;
    }

    // 新增：更新动画状态
    // Player.java - 更新 updateAnimationState 方法
    private void updateAnimationState() {
        long currentTime = System.currentTimeMillis();

        // 如果在平台上
        if (isOnPlatform) {
            if (isMovingLeft || isMovingRight) {
                // 移动状态 - 跑步动画
                if (currentTime - lastAnimationUpdate > 100) { // 每100ms切换一帧
                    currentFrameIndex = (currentFrameIndex + 1) % 3;
                    lastAnimationUpdate = currentTime;
                }
                currentAction = "run" + (currentFrameIndex + 1);
            } else {
                // 站立状态
                currentAction = "initialize";
                currentFrameIndex = 0; // 重置帧索引
            }
        } else {
            // 跳跃/下落状态
            if (currentTime - lastAnimationUpdate > 150) { // 每150ms切换一帧
                currentFrameIndex = (currentFrameIndex + 1) % 3;
                lastAnimationUpdate = currentTime;
            }
            currentAction = "jump" + (currentFrameIndex + 1);
        }

        // 调试输出
        System.out.println("当前状态: " + currentAction +
                " | 在平台: " + isOnPlatform +
                " | 速度Y: " + yVelocity);
    }

    // 修改render方法，使用图片渲染
    @Override
    public void render(GraphicsContext gc) {
        if (isDestroyed) return;

        String direction = facingRight ? "right" : "left";
        String imageKey = direction + "_" + currentAction;
        Image currentImage = playerImages.get(imageKey);

        if (currentImage != null) {
            // 使用图片渲染
            gc.drawImage(currentImage, x, y, width, height);
        } else {
            // 图片加载失败时使用备用颜色渲染
            Color playerColor = "warrior".equals(characterType) ? Color.BLUE : Color.GREEN;
            gc.setFill(playerColor);
            gc.fillRect(x, y, width, height);
            // 显示图片加载失败信息
            gc.setFill(Color.RED);
            gc.fillText("Image missing: " + imageKey, x, y - 20);
        }

        // 临时调试：显示平台状态
        gc.setFill(Color.YELLOW);
        gc.setFont(javafx.scene.text.Font.font(10));
        gc.fillText("OnPlatform: " + isOnPlatform + " Action: " + currentAction,
                x, y - 30);

        // 保留原有的UI元素（血量、名字等）
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(12));
        gc.fillText(name + " HP: " + health + "/" + maxHealth, x, y - 10);

        // 跳跃状态指示器
        if (!isOnPlatform) {
            gc.setFill(yVelocity < 0 ? Color.CYAN : Color.ORANGE);
            gc.fillText(yVelocity < 0 ? "↑" : "↓", x + width/2 - 5, y - 20);
        }
    }

    // 其他方法保持不变...

    // 注意：如果 Entity 接口没有定义 jump 方法，请移除 @Override 注解
    // Player.java - 修改jump方法
    // Player.java - 修改jump方法
    public void jump() {
        if (isDestroyed) return;

        System.out.println("jump()被调用 - isOnPlatform: " + isOnPlatform + ", yVelocity: " + yVelocity);

        // 方法1：直接检查是否可以跳跃（不依赖isOnPlatform字段）
        boolean canJump = checkIfCanJump();

        if (canJump) {
            yVelocity = Config.PLAYER_JUMP_FORCE;
            isOnPlatform = false;

            // 强制切换到跳跃动画起始帧
            currentAction = "jump1";
            currentFrameIndex = 0;
            lastAnimationUpdate = System.currentTimeMillis();

            System.out.println("跳跃成功触发! 当前动作: " + currentAction + ", 速度Y: " + yVelocity);
        } else {
            System.out.println("跳跃失败: 不在平台上, yVelocity=" + yVelocity);
        }
    }

    // 添加辅助方法：检查是否可以跳跃
    private boolean checkIfCanJump() {
        // 如果isOnPlatform为true，肯定可以跳
        if (isOnPlatform) {
            return true;
        }

        // 如果速度很小（接近静止），重新检测平台碰撞
        if (Math.abs(yVelocity) < 10) {
            if (platforms != null) {
                for (Platform platform : platforms) {
                    if (checkPlatformCollision(platform, 0)) { // 用0速度检测
                        isOnPlatform = true; // 更新状态
                        return true;
                    }
                }
            }
        }

        return false;
    }


    public void takeDamage(int damage) {
        if (isDestroyed) return;
        this.health = Math.max(0, this.health - damage);
        if (this.health <= 0) setDestroyed(true);
    }

    @Override
    public boolean isCollidingWith(Entity other) {
        if (isDestroyed || other.isDestroyed()) return false;
        return this.getX() < other.getX() + other.getWidth() &&
                this.getX() + this.getWidth() > other.getX() &&
                this.getY() < other.getY() + other.getHeight() &&
                this.getY() + this.getHeight() > other.getY();
    }

    // Entity接口实现保持不变...
    @Override public double getX() { return x; }
    @Override public double getY() { return y; }
    @Override public double getWidth() { return width; }
    @Override public double getHeight() { return height; }
    @Override public CollisionLayer getCollisionLayer() { return CollisionLayer.PLAYER; }
    @Override public boolean isDestroyed() { return isDestroyed; }
    @Override public void setDestroyed(boolean destroyed) { isDestroyed = destroyed; }

    // Getter & Setter保持不变...
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCharacterType() { return characterType; }
    public void setCharacterType(String characterType) { this.characterType = characterType; }
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = Math.max(0, Math.min(health, maxHealth)); }
    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
    public double getYVelocity() { return yVelocity; }
    public void setYVelocity(double yVelocity) { this.yVelocity = yVelocity; }
    public boolean isOnPlatform() { return isOnPlatform; }
    public void setOnPlatform(boolean onPlatform) { isOnPlatform = onPlatform; }
    public boolean isMovingLeft() { return isMovingLeft; }
    public void setMovingLeft(boolean movingLeft) { isMovingLeft = movingLeft; }
    public boolean isMovingRight() { return isMovingRight; }
    public void setMovingRight(boolean movingRight) { isMovingRight = movingRight; }
    public boolean isFacingRight() { return facingRight; }
    public void setFacingRight(boolean facingRight) { this.facingRight = facingRight; }
    public Weapon getCurrentWeapon() { return currentWeapon; }
    public void setCurrentWeapon(Weapon currentWeapon) { this.currentWeapon = currentWeapon; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
}