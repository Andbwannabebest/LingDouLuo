// src/main/java/com/lingdouluo/entity/RifleWeapon.java
package com.lingdouluo.entity;

import javafx.scene.paint.Color;

public class RifleWeapon extends Weapon {

    public RifleWeapon(Player owner) {
        super(owner);
        this.damage = 10;
        this.fireRate = 15.0; // 每秒15发，支持快速连续射击
        this.maxAmmo = -1; // 无限弹药
        this.currentAmmo = -1; // 无限弹药
        this.isAutomatic = true;
        this.supportsCharging = false; // 步枪不支持蓄力
    }

    @Override
    public void shoot(boolean isFacingRight) {
        if (!canShoot()) return;

        // 计算子弹起始位置
        double bulletX = owner.getX() + owner.getWidth() / 2;
        double bulletY = owner.getY() + owner.getHeight() / 2;

        // 子弹方向
        double bulletSpeedX = isFacingRight ? 12 : -12;

        // 创建子弹
        Bullet bullet = new Bullet(bulletX, bulletY, 8, 8);
        bullet.setVelocityX(bulletSpeedX);
        bullet.setDamage(damage);
        bullet.setColor(Color.LIGHTBLUE);
        bullet.setOwner(owner);

        bullets.add(bullet);
        startCooldown();

        // TODO: 播放射击音效
    }
}