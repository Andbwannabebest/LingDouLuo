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
        this.supportsCharging = true; // 榴弹支持蓄力
        this.chargedDamageMultiplier = 5;
    }

    @Override
    public void shoot(boolean isFacingRight) {
        if (!canShoot()) return;

        // 普通榴弹
        createGrenade(isFacingRight, 1.0);
        startCooldown();

        // TODO: 播放榴弹发射音效
    }

    @Override
    public void shootCharged(boolean isFacingRight) {
        if (!canShoot()) return;

        // 蓄力榴弹
        createGrenade(isFacingRight, chargedDamageMultiplier);
        startCooldown();

        // TODO: 播放蓄力榴弹发射音效
    }

    private void createGrenade(boolean isFacingRight, double damageMultiplier) {
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
        grenade.setDamage((int)(damage * damageMultiplier));
        grenade.setColor(damageMultiplier > 1 ? Color.ORANGERED : Color.ORANGE);
        grenade.setExplosive(true);
        grenade.setExplosionRadius((int)(60 * damageMultiplier));
        grenade.setOwner(owner);

        bullets.add(grenade);
    }
}