// src/main/java/com/lingdouluo/entity/Player.java
package com.lingdouluo.entity;

import com.lingdouluo.config.Config;
import com.lingdouluo.input.InputManager;
import com.lingdouluo.physics.CollisionResult;
import com.lingdouluo.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Player extends Entity {

    private int health;
    private int maxHealth;
    private int lives;
    private int score;
    private boolean isFacingRight;
    private boolean isJumping;
    private boolean isShooting;
    private boolean canDoubleJump;
    private boolean hasDoubleJumped;

    private Weapon currentWeapon;
    private List<Weapon> weapons;
    private int currentWeaponIndex;

    private InputManager inputManager;

    // 动画相关
    private long lastShootTime;
    private static final long SHOOT_COOLDOWN = 200; // 毫秒
    private Rectangle debugRect;

    public Player(double x, double y) {
        super(x, y, 32, 64); // 玩家尺寸
        this.maxHealth = Config.PLAYER_MAX_HEALTH;
        this.health = maxHealth;
        this.lives = Config.PLAYER_MAX_LIVES;
        this.score = 0;
        this.isFacingRight = true;
        this.isJumping = false;
        this.isShooting = false;
        this.canDoubleJump = true;
        this.hasDoubleJumped = false;

        // 初始化武器
        this.weapons = new ArrayList<>();
        this.weapons.add(new RifleWeapon(this)); // 默认步枪
        this.weapons.add(new GrenadeWeapon(this)); // 榴弹发射器
        this.currentWeaponIndex = 0;
        this.currentWeapon = weapons.get(currentWeaponIndex);

        this.debugRect = new Rectangle(x, y, width, height);
        this.lastShootTime = 0;
    }

    @Override
    public void update(double deltaTime) {
        if (!isActive) return;

        handleInput();
        applyPhysics(deltaTime);
        updateWeapon(deltaTime);
        updateAnimation(deltaTime);

        // 更新调试矩形位置
        debugRect.setX(x);
        debugRect.setY(y);
    }

    private void handleInput() {
        if (inputManager == null) return;

        // 水平移动
        double moveInput = 0;
        if (inputManager.isKeyPressed(Config.KEY_LEFT)) {
            moveInput -= 1;
            isFacingRight = false;
        }
        if (inputManager.isKeyPressed(Config.KEY_RIGHT)) {
            moveInput += 1;
            isFacingRight = true;
        }

        velocityX = moveInput * Config.PLAYER_MOVE_SPEED;

        // 跳跃
        if (inputManager.isKeyPressed(Config.KEY_JUMP)) {
            if (isOnGround) {
                velocityY = Config.PLAYER_JUMP_FORCE;
                isJumping = true;
                isOnGround = false;
                hasDoubleJumped = false;
            } else if (canDoubleJump && !hasDoubleJumped) {
                velocityY = Config.PLAYER_JUMP_FORCE * 0.8; // 二段跳力度较小
                hasDoubleJumped = true;
                isJumping = true;
            }
        } else {
            isJumping = false;
        }

        // 射击
        if (inputManager.isKeyPressed(Config.KEY_SHOOT)) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShootTime > SHOOT_COOLDOWN) {
                shoot();
                lastShootTime = currentTime;
            }
            isShooting = true;
        } else {
            isShooting = false;
        }

        // 切换武器
        if (inputManager.isKeyPressed(Config.KEY_WEAPON_SWITCH)) {
            switchWeapon();
        }
    }

    private void applyPhysics(double deltaTime) {
        // 应用重力
        if (!isOnGround) {
            velocityY += Config.GRAVITY;
        }

        // 限制垂直速度
        if (velocityY > 20) velocityY = 20;

        // 应用速度
        x += velocityX;
        y += velocityY;

        // 边界检查
        if (x < 0) x = 0;
        if (x > Config.WINDOW_WIDTH - width) x = Config.WINDOW_WIDTH - width;
        if (y > Config.WINDOW_HEIGHT - height) {
            y = Config.WINDOW_HEIGHT - height;
            velocityY = 0;
            isOnGround = true;
        }

        // 应用摩擦力
        if (isOnGround) {
            velocityX *= Config.FRICTION;
            if (Math.abs(velocityX) < 0.1) velocityX = 0;
        }
    }

    private void shoot() {
        if (currentWeapon != null) {
            currentWeapon.shoot(isFacingRight);
        }
    }

    private void switchWeapon() {
        currentWeaponIndex = (currentWeaponIndex + 1) % weapons.size();
        currentWeapon = weapons.get(currentWeaponIndex);
        // TODO: 播放切换武器音效
    }

    private void updateWeapon(double deltaTime) {
        if (currentWeapon != null) {
            currentWeapon.update(deltaTime);
        }
    }

    private void updateAnimation(double deltaTime) {
        // TODO: 实现动画更新逻辑
    }

    @Override
    public void render(GraphicsContext gc) {
        // 渲染玩家 - 暂时使用简单矩形
        gc.setFill(Color.rgb(0, 150, 255)); // 蓝色玩家
        gc.fillRect(x, y, width, height);

        // 渲染武器方向指示器
        if (currentWeapon != null) {
            currentWeapon.render(gc);
        }
    }

    @Override
    public void handleCollision(CollisionResult collision) {
        // 处理与其他实体的碰撞
        switch (collision.getType()) {
            case PLATFORM:
                handlePlatformCollision(collision);
                break;
            case ENEMY:
                takeDamage(1);
                break;
            case POWER_UP:
                // TODO: 处理道具拾取
                break;
            case BULLET:
                // 被子弹击中
                if (!collision.getSource().equals(this)) {
                    takeDamage(1);
                }
                break;
        }
    }

    private void handlePlatformCollision(CollisionResult collision) {
        // 平台碰撞处理
        if (collision.getNormalY() < 0) { // 从上方碰撞
            y = collision.getEntityY() - height;
            velocityY = 0;
            isOnGround = true;
            hasDoubleJumped = false;
        } else if (collision.getNormalY() > 0) { // 从下方碰撞
            y = collision.getEntityY() + collision.getEntityHeight();
            velocityY = 0;
        }

        if (collision.getNormalX() != 0) { // 水平碰撞
            velocityX = 0;
        }
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            isActive = false;
            lives--;
            if (lives <= 0) {
                // 游戏结束
                Config.GAME_STATE = GameState.GAME_OVER;
            }
        }

        // TODO: 添加受伤无敌时间
        // TODO: 播放受伤音效
    }

    public void heal(int amount) {
        health += amount;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    public void addScore(int points) {
        score += points;
    }

    // Getter和Setter方法
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getLives() { return lives; }
    public int getScore() { return score; }
    public boolean isFacingRight() { return isFacingRight; }
    public Weapon getCurrentWeapon() { return currentWeapon; }

    public void setInputManager(InputManager inputManager) {
        this.inputManager = inputManager;
    }

    public void setHealth(int health) { this.health = health; }
    public void setLives(int lives) { this.lives = lives; }
    public void setScore(int score) { this.score = score; }

    public Rectangle getDebugRect() {
        return debugRect;
    }
}