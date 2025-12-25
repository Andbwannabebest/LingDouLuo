// src/main/java/com/lingdouluo/entity/RifleWeapon.java
package com.lingdouluo.entity;

import javafx.scene.paint.Color;

public class RifleWeapon extends Weapon {

    public RifleWeapon(Player owner) {
        super(owner);
        this.damage = 10;
        this.fireRate = 10.0; // 每秒10发
        this.maxAmmo = -1; // 无限弹药
        this.currentAmmo = -1; // 无限弹药
        this.isAutomatic = true;
    }

    @Override
    public void shoot(boolean isFacingRight) {
        if (!canShoot()) return;

        // 计算子弹起始位置
        double bulletX = owner.getX() + owner.getWidth() / 2;
        double bulletY = owner.getY() + owner.getHeight() / 2;

        // 子弹方向
        double bulletSpeedX = isFacingRight ? 10 : -10;

        // 创建子弹
        Bullet bullet = new Bullet(bulletX, bulletY, 8, 8);
        bullet.setVelocityX(bulletSpeedX);
        bullet.setDamage(damage);
        bullet.setColor(Color.LIGHTBLUE);

        bullets.add(bullet);
        startCooldown();

        // TODO: 播放射击音效
    }
}