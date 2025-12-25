package com.lingdouluo.entity;

import com.lingdouluo.config.Config;
import com.lingdouluo.physics.CollisionLayer;
import com.lingdouluo.GameLoop;
//import com.lingdouluo.weapon.GrenadeWeapon; // 新增：导入GrenadeWeapon类
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class Player implements Entity {
    // 成员变量
    private String name;
    private String characterType;
    private double x;
    private double y;
    private double width;
    private double height;
    private int health;
    private int maxHealth; // 最大生命值
    private double yVelocity; // 下落速度
    private boolean isDestroyed;
    private boolean isOnPlatform; // 是否在平台上
    // 按键状态（用于持续移动）
    private boolean isLeftPressed;
    private boolean isRightPressed;
    // 玩家朝向（默认朝右）
    private boolean facingRight = true;
    // 当前持有武器
    private Weapon currentWeapon;

    // 构造器
    public Player(String name, String characterType) {
        this.name = name;
        this.characterType = characterType;
        this.x = Config.PLAYER_INIT_X;
        this.y = Config.PLAYER_INIT_Y;
        this.width = Config.PLAYER_WIDTH;
        this.height = Config.PLAYER_HEIGHT;
        this.maxHealth = Config.PLAYER_INIT_HEALTH; // 初始化最大生命值
        this.health = this.maxHealth; // 当前生命值默认等于最大生命值
        this.yVelocity = 0;
        this.isDestroyed = false;
        this.isOnPlatform = false;
        this.isLeftPressed = false;
        this.isRightPressed = false;
        // 初始给玩家分配一把步枪
        this.currentWeapon = new RifleWeapon();
    }

    // ==================== 按键处理方法 ====================
    public void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        switch (code) {
            case A:
            case LEFT:
                isLeftPressed = true;
                break;
            case D:
            case RIGHT:
                isRightPressed = true;
                break;
            case W:
            case UP:
            case SPACE:
                jump(); // 触发跳跃
                break;
            case F:
                // 发射当前武器
                fireCurrentWeapon();
                break;
            case R:
                // 装填当前武器
                reloadCurrentWeapon();
                break;
            case G:
                // 切换到手榴弹武器
                switchToGrenadeWeapon();
                break;
        }
    }

    public void handleKeyRelease(KeyEvent event) {
        KeyCode code = event.getCode();
        switch (code) {
            case A:
            case LEFT:
                isLeftPressed = false;
                break;
            case D:
            case RIGHT:
                isRightPressed = false;
                break;
        }
    }

    // 跳跃逻辑
    private void jump() {
        if (isOnPlatform && !isDestroyed) {
            yVelocity = Config.PLAYER_JUMP_FORCE;
            isOnPlatform = false;
        }
    }

    // 发射当前持有武器
    private void fireCurrentWeapon() {
        if (currentWeapon == null || isDestroyed) {
            return;
        }
        Bullet bullet = currentWeapon.fire(this);
        // 若子弹生成成功，可通过GameLoop添加到游戏中（此处预留扩展）
    }

    // 装填当前持有武器
    private void reloadCurrentWeapon() {
        if (currentWeapon == null || isDestroyed) {
            return;
        }
        if (currentWeapon instanceof RifleWeapon) {
            ((RifleWeapon) currentWeapon).reload();
        } else if (currentWeapon instanceof GrenadeWeapon) {
            currentWeapon.reload(currentWeapon.getMaxAmmo());
        }
    }

    // 新增：切换到手榴弹武器
    private void switchToGrenadeWeapon() {
        this.currentWeapon = new GrenadeWeapon();
        System.out.println("已切换为：" + currentWeapon.getName());
    }

    // ==================== Entity接口实现 ====================
    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public CollisionLayer getCollisionLayer() {
        return CollisionLayer.PLAYER;
    }

    @Override
    public void update(double deltaTime) {
        if (isDestroyed) return;

        // 处理左右移动
        double moveSpeed = 0;
        if (isLeftPressed) moveSpeed -= Config.PLAYER_MOVE_SPEED;
        if (isRightPressed) moveSpeed += Config.PLAYER_MOVE_SPEED;
        x += moveSpeed * deltaTime;

        // 根据移动方向更新朝向
        if (moveSpeed > 0) {
            facingRight = true; // 向右移动，朝右
        } else if (moveSpeed < 0) {
            facingRight = false; // 向左移动，朝左
        }

        // 重力逻辑
        if (!isOnPlatform) {
            yVelocity += Config.PLAYER_GRAVITY * deltaTime;
            y += yVelocity * deltaTime;
        } else {
            isOnPlatform = false; // 重置平台状态
        }

        // 边界检测
        if (y + height > Config.SCREEN_HEIGHT) {
            y = Config.SCREEN_HEIGHT - height;
            yVelocity = 0;
            isOnPlatform = true;
        }
        if (x < 0) x = 0;
        if (x + width > Config.SCREEN_WIDTH) x = Config.SCREEN_WIDTH - width;
    }

    // 重载update方法（兼容需传入GameLoop的场景）
    public void update(double deltaTime, GameLoop gameLoop) {
        this.update(deltaTime);
        // 如需通过GameLoop发射子弹等逻辑，可在此扩展
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isDestroyed) return;

        // 按角色类型渲染颜色
        Color playerColor = "warrior".equals(characterType) ? Color.BLUE : Color.GREEN;
        gc.setFill(playerColor);
        gc.fillRect(x, y, width, height);

        // 渲染血量（显示当前/最大生命值）
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(12));
        gc.fillText(name + " HP: " + health + "/" + maxHealth, x, y - 10);

        // 渲染当前武器信息（兼容getName()方法）
        if (currentWeapon != null) {
            gc.fillText("武器: " + currentWeapon.getName() + " 弹药: " + currentWeapon.getCurrentAmmo() + "/" + currentWeapon.getMaxAmmo(), x, y - 30);
        }
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

    @Override
    public void setDestroyed(boolean destroyed) {
        isDestroyed = destroyed;
    }

    // ==================== 自定义方法 ====================
    public void takeDamage(int damage) {
        this.health -= damage;
        if (health <= 0) {
            health = 0;
            setDestroyed(true);
        }
    }

    // 恢复生命值（不超过最大生命值）
    public void heal(int healAmount) {
        this.health = Math.min(health + healAmount, maxHealth);
    }

    // ==================== 缺失方法补充 ====================
    // 获取最大生命值
    public int getMaxHealth() {
        return maxHealth;
    }

    // 获取当前持有武器
    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    // ==================== Getter & Setter ====================
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharacterType() {
        return characterType;
    }

    public void setCharacterType(String characterType) {
        this.characterType = characterType;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(health, maxHealth));
        if (this.health <= 0) {
            setDestroyed(true);
        }
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        // 最大生命值变更后，当前生命值不超过新的最大值
        this.health = Math.min(this.health, maxHealth);
    }

    public double getYVelocity() {
        return yVelocity;
    }

    public void setYVelocity(double yVelocity) {
        this.yVelocity = yVelocity;
    }

    public boolean isOnPlatform() {
        return isOnPlatform;
    }

    public void setOnPlatform(boolean onPlatform) {
        isOnPlatform = onPlatform;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    public void setCurrentWeapon(Weapon currentWeapon) {
        this.currentWeapon = currentWeapon;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}