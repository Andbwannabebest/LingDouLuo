// src/main/java/com/lingdouluo/entity/Player.java
package com.lingdouluo.entity;

import com.lingdouluo.config.Config;
import com.lingdouluo.input.InputManager;
import com.lingdouluo.physics.CollisionResult;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Player extends Entity {

    private int playerId; // 1或2，标识玩家
    private int health;
    private int maxHealth;
    private int lives;
    private int score;
    private boolean isFacingRight;
    private boolean isJumping;
    private boolean isShooting;
    private boolean canDoubleJump;
    private boolean hasDoubleJumped;
    private long lastGroundTime; // 上次在地面的时间
    private long respawnTime; // 复活时间
    private long invincibleTime; // 无敌时间
    private boolean isInvincible; // 是否无敌

    private Weapon currentWeapon;
    private List<Weapon> weapons;
    private int currentWeaponIndex;

    private InputManager inputManager;

    // 动画相关
    private long lastShootTime;
    private long shootStartTime; // 开始射击的时间，用于蓄力
    private boolean isCharging; // 是否正在蓄力
    private static final long SHOOT_COOLDOWN = 200; // 毫秒（增加射击间隔）
    private static final long CHARGE_TIME = 1000; // 蓄力时间（毫秒）
    private static final long INVINCIBLE_DURATION = 2000; // 无敌时间2秒

    // 武器携带限制
    private static final int MAX_WEAPONS = 3;

    public Player(double x, double y, int playerId) {
        super(x, y, 32, 64); // 玩家尺寸
        this.playerId = playerId;
        this.maxHealth = Config.PLAYER_MAX_HEALTH;
        this.health = maxHealth;
        this.lives = Config.PLAYER_MAX_LIVES;
        this.score = 0;
        this.isFacingRight = true;
        this.isJumping = false;
        this.isShooting = false;
        this.canDoubleJump = true;
        this.hasDoubleJumped = false;
        this.isCharging = false;
        this.lastGroundTime = System.currentTimeMillis();
        this.respawnTime = 0;
        this.invincibleTime = 0;
        this.isInvincible = false;

        // 初始化武器
        this.weapons = new ArrayList<>();
        this.weapons.add(new RifleWeapon(this)); // 默认步枪
        this.currentWeaponIndex = 0;
        this.currentWeapon = weapons.get(currentWeaponIndex);

        this.lastShootTime = 0;
        this.shootStartTime = 0;
    }

    @Override
    public void update(double deltaTime) {
        // 更新无敌状态
        updateInvincibleState();

        if (!isActive) {
            // 检查复活
            if (respawnTime > 0 && System.currentTimeMillis() - respawnTime > 2000) {
                respawn();
            }
            return;
        }

        handleInput();
        applyPhysics(deltaTime);
        updateWeapon(deltaTime);

        // 处理蓄力射击
        if (isCharging) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - shootStartTime >= CHARGE_TIME) {
                // 蓄力完成，发射蓄力攻击
                shoot(true);
                isCharging = false;
                shootStartTime = 0;
            }
        }
    }

    private void updateInvincibleState() {
        if (isInvincible) {
            if (System.currentTimeMillis() - invincibleTime > INVINCIBLE_DURATION) {
                isInvincible = false;
                invincibleTime = 0;
            }
        }
    }

    private void handleInput() {
        if (inputManager == null) return;

        // 根据玩家ID获取输入
        boolean leftPressed, rightPressed, upPressed, downPressed, jumpPressed, shootPressed, weaponSwitchPressed;

        if (playerId == 1) {
            leftPressed = inputManager.isP1Left();
            rightPressed = inputManager.isP1Right();
            upPressed = inputManager.isP1Up();
            downPressed = inputManager.isP1Down();
            jumpPressed = inputManager.isP1Jump();
            shootPressed = inputManager.isP1Shoot();
            weaponSwitchPressed = inputManager.isP1WeaponSwitch();
        } else {
            leftPressed = inputManager.isP2Left();
            rightPressed = inputManager.isP2Right();
            upPressed = inputManager.isP2Up();
            downPressed = inputManager.isP2Down();
            jumpPressed = inputManager.isP2Jump();
            shootPressed = inputManager.isP2Shoot();
            weaponSwitchPressed = inputManager.isP2WeaponSwitch();
        }

        // 水平移动 - 直接控制速度，不使用加速度
        double moveInput = 0;
        if (leftPressed) {
            moveInput -= 1;
            isFacingRight = false;
        }
        if (rightPressed) {
            moveInput += 1;
            isFacingRight = true;
        }

        // 直接设置速度，不使用加速度
        if (moveInput != 0) {
            velocityX = moveInput * Config.PLAYER_MOVE_SPEED;
        } else {
            // 没有输入时，立即停止水平移动（魂斗罗风格）
            velocityX = 0;
        }

        // 跳跃（支持二段跳）
        if (jumpPressed && !isJumping) {
            if (isOnGround) {
                velocityY = Config.PLAYER_JUMP_FORCE;
                isJumping = true;
                isOnGround = false;
                hasDoubleJumped = false;
            } else if (canDoubleJump && !hasDoubleJumped) {
                // 二段跳
                velocityY = Config.PLAYER_JUMP_FORCE * 0.7; // 二段跳力度较小
                hasDoubleJumped = true;
                isJumping = true;
            }
        }

        // 松开跳跃键时，如果还在上升，减少上升速度（更好的跳跃控制）
        if (!jumpPressed && velocityY < 0) {
            velocityY *= 0.8;
        }

        // 射击（支持长按连续射击和蓄力）
        if (shootPressed) {
            isShooting = true;

            if (currentWeapon != null && currentWeapon.supportsCharging()) {
                // 支持蓄力的武器
                if (!isCharging) {
                    isCharging = true;
                    shootStartTime = System.currentTimeMillis();
                }
            } else {
                // 不支持蓄力的武器，连续射击
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastShootTime > SHOOT_COOLDOWN) {
                    shoot(false);
                    lastShootTime = currentTime;
                }
            }
        } else {
            isShooting = false;
            if (isCharging) {
                // 如果正在蓄力但松开了射击键，取消蓄力并发射普通攻击
                shoot(false);
                isCharging = false;
                shootStartTime = 0;
            }
        }

        // 切换武器 - 使用isKeyJustPressed防止连续切换
        if (weaponSwitchPressed) {
            switchWeapon();
        }
    }

    private void applyPhysics(double deltaTime) {
        // 应用重力
        if (!isOnGround) {
            velocityY += Config.GRAVITY;
        } else {
            lastGroundTime = System.currentTimeMillis();
        }

        // 限制垂直速度
        if (velocityY > 15) velocityY = 15;
        if (velocityY < -15) velocityY = -15;

        // 应用速度 - 简化计算，避免乱飘
        x += velocityX * deltaTime * 60;
        y += velocityY * deltaTime * 60;

        // 边界检查 - 魂斗罗风格：不掉落死亡，只在屏幕内移动
        if (x < 0) {
            x = 0;
            velocityX = 0;
        }
        if (x > Config.WINDOW_WIDTH - width) {
            x = Config.WINDOW_WIDTH - width;
            velocityX = 0;
        }

        // 垂直边界 - 完全取消掉落死亡，只在屏幕内
        if (y < 0) {
            y = 0;
            velocityY = 0;
        }
        if (y > Config.WINDOW_HEIGHT - height) {
            y = Config.WINDOW_HEIGHT - height;
            velocityY = 0;
            isOnGround = true;
            isJumping = false;
            hasDoubleJumped = false;
        }

        // 简单的摩擦力 - 只在落地时应用
        if (isOnGround && Math.abs(velocityX) > 0) {
            velocityX *= Config.FRICTION;
            if (Math.abs(velocityX) < 0.1) {
                velocityX = 0;
            }
        }
    }

    private void shoot(boolean isCharged) {
        if (currentWeapon != null) {
            if (isCharged) {
                currentWeapon.shootCharged(isFacingRight);
            } else {
                currentWeapon.shoot(isFacingRight);
            }
        }
    }

    private void switchWeapon() {
        if (weapons.isEmpty()) return;

        currentWeaponIndex = (currentWeaponIndex + 1) % weapons.size();
        currentWeapon = weapons.get(currentWeaponIndex);

        // 播放切换武器音效
        // TODO: SoundUtils.playSound("audios/weapon_switch.wav");
    }

    public void addWeapon(Weapon weapon) {
        if (weapons.size() < MAX_WEAPONS) {
            weapons.add(weapon);
        }
    }

    private void updateWeapon(double deltaTime) {
        if (currentWeapon != null) {
            currentWeapon.update(deltaTime);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isActive) {
            // 显示死亡/复活效果
            if (respawnTime > 0) {
                long timeSinceDeath = System.currentTimeMillis() - respawnTime;
                if (timeSinceDeath < 2000) {
                    // 闪烁效果
                    if ((timeSinceDeath / 200) % 2 == 0) {
                        renderPlayerBody(gc);
                    }
                }
            }
            return;
        }

        // 无敌状态闪烁
        if (isInvincible) {
            if ((System.currentTimeMillis() / 100) % 2 == 0) {
                renderPlayerBody(gc);
            }
        } else {
            renderPlayerBody(gc);
        }
    }

    private void renderPlayerBody(GraphicsContext gc) {
        // 根据玩家ID选择颜色
        Color playerColor;
        if (playerId == 1) {
            playerColor = Color.rgb(0, 150, 255); // 蓝色玩家1
        } else {
            playerColor = Color.rgb(255, 105, 180); // 粉色玩家2
        }

        // 渲染玩家身体
        gc.setFill(playerColor);
        gc.fillRect(x, y, width, height);

        // 渲染玩家标识
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 12));
        gc.fillText("P" + playerId, x + width/2 - 5, y - 5);

        // 渲染武器方向指示器
        if (isFacingRight) {
            gc.setFill(Color.YELLOW);
            gc.fillRect(x + width, y + height/2 - 2, 10, 4);
        } else {
            gc.setFill(Color.YELLOW);
            gc.fillRect(x - 10, y + height/2 - 2, 10, 4);
        }

        // 如果正在射击，显示射击效果
        if (isShooting) {
            if (isCharging) {
                // 显示蓄力效果
                long currentTime = System.currentTimeMillis();
                float chargePercent = Math.min(1.0f, (float)(currentTime - shootStartTime) / CHARGE_TIME);

                gc.setFill(Color.rgb(255, (int)(255 * (1 - chargePercent)), 0, 0.7));
                int chargeSize = (int)(width * (1 + chargePercent));
                gc.fillOval(x - (chargeSize - width)/2, y - (chargeSize - height)/2, chargeSize, chargeSize);
            } else {
                // 普通射击效果
                gc.setFill(Color.ORANGE);
                if (isFacingRight) {
                    gc.fillOval(x + width, y + height/2 - 5, 15, 10);
                } else {
                    gc.fillOval(x - 15, y + height/2 - 5, 15, 10);
                }
            }
        }

        // 渲染当前武器的子弹
        if (currentWeapon != null) {
            currentWeapon.render(gc);
        }
    }

    @Override
    public void handleCollision(CollisionResult collision) {
        if (isInvincible) return;

        // 处理与其他实体的碰撞
        switch (collision.getType()) {
            case PLATFORM:
                handlePlatformCollision(collision);
                break;
            case ENEMY:
                takeDamage(collision.getDamage());
                break;
            case POWER_UP:
                // TODO: 处理道具拾取
                break;
            case BULLET:
                // 被子弹击中
                if (!collision.getSource().equals(this)) {
                    takeDamage(collision.getDamage());
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
            isJumping = false;
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
        if (isInvincible) return;

        health -= damage;
        if (health <= 0) {
            health = 0;
            die();
        } else {
            // 受伤后进入无敌状态
            isInvincible = true;
            invincibleTime = System.currentTimeMillis();
        }

        // TODO: 播放受伤音效
    }

    private void die() {
        isActive = false;
        lives--;
        respawnTime = System.currentTimeMillis();

        if (lives <= 0) {
            // 游戏结束
            respawnTime = 0;
        }
    }

    private void respawn() {
        if (lives > 0) {
            isActive = true;
            health = maxHealth;
            // 重置到安全位置
            x = 100;
            y = 500;
            velocityX = 0;
            velocityY = 0;
            respawnTime = 0;
            // 复活后无敌2秒
            isInvincible = true;
            invincibleTime = System.currentTimeMillis();
        }
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
    public int getPlayerId() { return playerId; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getLives() { return lives; }
    public int getScore() { return score; }
    public boolean isFacingRight() { return isFacingRight; }
    public Weapon getCurrentWeapon() { return currentWeapon; }
    public List<Weapon> getWeapons() { return weapons; }
    public boolean isCharging() { return isCharging; }
    public boolean isInvincible() { return isInvincible; }

    public void setInputManager(InputManager inputManager) {
        this.inputManager = inputManager;
    }

    public void setHealth(int health) { this.health = health; }
    public void setLives(int lives) { this.lives = lives; }
    public void setScore(int score) { this.score = score; }
}