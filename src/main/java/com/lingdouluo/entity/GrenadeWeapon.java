// src/main/java/com/lingdouluo/entity/GrenadeWeapon.java
package com.lingdouluo.entity;

import javafx.scene.paint.Color;

public class GrenadeWeapon extends Weapon {

    public GrenadeWeapon(Player owner) {
        super(owner);
        this.damage = 30;
        this.fireRate = 2.0; // 每秒2发
        this.maxAmmo = 20;
        this.currentAmmo = maxAmmo;
        this.isAutomatic = false;
    }

    @Override
    public void shoot(boolean isFacingRight) {
        if (!canShoot()) return;

        // 计算榴弹起始位置
        double grenadeX = owner.getX() + owner.getWidth() / 2;
        double grenadeY = owner.getY() + owner.getHeight() / 2;

        // 发射角度和速度
        double speedX = isFacingRight ? 8 : -8;
        double speedY = -5; // 向上发射

        // 创建榴弹
        Bullet grenade = new Bullet(grenadeX, grenadeY, 12, 12);
        grenade.setVelocityX(speedX);
        grenade.setVelocityY(speedY);
        grenade.setDamage(damage);
        grenade.setColor(Color.ORANGERED);
        grenade.setExplosive(true);
        grenade.setExplosionRadius(60);

        bullets.add(grenade);
        startCooldown();

        // TODO: 播放榴弹发射音效
    }
}