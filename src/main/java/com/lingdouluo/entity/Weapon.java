package com.lingdouluo.entity;

/**
 * 武器抽象父类，统一管理所有武器属性与行为
 */
public abstract class Weapon {
    // 武器基础属性
    private String weaponName;       // 武器名称
    private int maxUpgradeLevel;     // 最大升级等级
    private int currentLevel = 1;    // 当前等级
    private int damage;              // 伤害值
    private float fireRate;          // 射速（冷却时间，秒/发）
    private float bulletSpeed;       // 子弹速度（float类型，避免转换损失）
    private int maxAmmo;             // 最大弹药量
    private int currentAmmo;         // 当前弹药量
    private long lastFireTime;       // 上次发射时间（毫秒）

    // 构造器（匹配子类调用，子弹速度改为float）
    public Weapon(String weaponName, int maxUpgradeLevel, int damage, float fireRate, float bulletSpeed, int maxAmmo) {
        this.weaponName = weaponName;
        this.maxUpgradeLevel = maxUpgradeLevel;
        this.damage = damage;
        this.fireRate = fireRate;
        this.bulletSpeed = bulletSpeed;
        this.maxAmmo = maxAmmo;
        this.currentAmmo = maxAmmo;     // 初始弹药满额
        this.lastFireTime = 0;
    }

    // ==================== 通用方法 ====================
    /**
     * 判断是否可以发射（冷却完成 + 有弹药）
     */
    public boolean canFire() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastFireTime) >= (fireRate * 1000) && currentAmmo > 0;
    }

    /**
     * 发射方法（抽象方法，子类必须实现）
     */
    public abstract Bullet fire(Player player);

    /**
     * 升级武器（未达到最大等级时）
     */
    public void upgrade() {
        if (currentLevel >= maxUpgradeLevel) {
            return;
        }
        currentLevel++;
        updateAttributesAfterUpgrade();
    }

    /**
     * 升级后更新属性（抽象方法，子类实现具体逻辑）
     */
    protected abstract void updateAttributesAfterUpgrade();

    // ==================== 弹药与冷却管理 ====================
    /**
     * 消耗弹药
     */
    public void consumeAmmo() {
        if (currentAmmo > 0) {
            currentAmmo--;
        }
    }

    /**
     * 重置发射冷却时间
     */
    public void resetFireTimer() {
        this.lastFireTime = System.currentTimeMillis();
    }

    /**
     * 补充弹药（不超过最大弹药量）
     */
    public void reload(int ammoAmount) {
        currentAmmo = Math.min(currentAmmo + ammoAmount, maxAmmo);
    }

    // 补充：setAmmo方法（兼容子类调用）
    public void setAmmo(int ammo) {
        this.currentAmmo = Math.max(0, Math.min(ammo, maxAmmo)); // 限制弹药范围
    }

    // ==================== 新增：兼容getName()调用（解决符号缺失） ====================
    public String getName() {
        return this.weaponName;
    }

    // ==================== Getter & Setter ====================
    public String getWeaponName() {
        return weaponName;
    }

    public int getMaxUpgradeLevel() {
        return maxUpgradeLevel;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public float getFireRate() {
        return fireRate;
    }

    public void setFireRate(float fireRate) {
        this.fireRate = fireRate;
    }

    public float getBulletSpeed() {
        return bulletSpeed;
    }

    public void setBulletSpeed(float bulletSpeed) {
        this.bulletSpeed = bulletSpeed;
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public void setMaxAmmo(int maxAmmo) {
        this.maxAmmo = maxAmmo;
    }

    public int getCurrentAmmo() {
        return currentAmmo;
    }

    public void setCurrentAmmo(int currentAmmo) {
        this.currentAmmo = currentAmmo;
    }

    public long getLastFireTime() {
        return lastFireTime;
    }

    public void setLastFireTime(long lastFireTime) {
        this.lastFireTime = lastFireTime;
    }
}