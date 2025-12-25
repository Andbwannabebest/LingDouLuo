// src/main/java/com/lingdouluo/entity/Weapon.java
package com.lingdouluo.entity;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;

public abstract class Weapon {
    protected Player owner;
    protected List<Bullet> bullets;
    protected int damage;
    protected double fireRate; // 每秒发射数
    protected double cooldown;
    protected int maxAmmo;
    protected int currentAmmo;
    protected boolean isAutomatic;

    public Weapon(Player owner) {
        this.owner = owner;
        this.bullets = new ArrayList<>();
        this.damage = 10;
        this.fireRate = 5.0; // 每秒5发
        this.cooldown = 0;
        this.maxAmmo = 100;
        this.currentAmmo = maxAmmo;
        this.isAutomatic = true;
    }

    public abstract void shoot(boolean isFacingRight);

    public void update(double deltaTime) {
        // 更新冷却时间
        if (cooldown > 0) {
            cooldown -= deltaTime;
        }

        // 更新子弹
        List<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet bullet : bullets) {
            bullet.update(deltaTime);
            if (!bullet.isActive()) {
                bulletsToRemove.add(bullet);
            }
        }
        bullets.removeAll(bulletsToRemove);
    }

    public void render(GraphicsContext gc) {
        // 渲染所有活跃的子弹
        for (Bullet bullet : bullets) {
            bullet.render(gc);
        }
    }

    protected boolean canShoot() {
        return cooldown <= 0 && currentAmmo > 0;
    }

    protected void startCooldown() {
        cooldown = 1.0 / fireRate;
        currentAmmo--;
    }

    // Getter和Setter方法
    public int getDamage() { return damage; }
    public double getFireRate() { return fireRate; }
    public int getCurrentAmmo() { return currentAmmo; }
    public int getMaxAmmo() { return maxAmmo; }
    public List<Bullet> getBullets() { return bullets; }

    public void setDamage(int damage) { this.damage = damage; }
    public void setFireRate(double fireRate) { this.fireRate = fireRate; }
    public void addAmmo(int amount) {
        currentAmmo += amount;
        if (currentAmmo > maxAmmo) {
            currentAmmo = maxAmmo;
        }
    }
}