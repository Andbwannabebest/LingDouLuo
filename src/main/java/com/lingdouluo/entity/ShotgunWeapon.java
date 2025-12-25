// src/main/java/com/lingdouluo/entity/ShotgunWeapon.java
package com.lingdouluo.entity;

import javafx.scene.paint.Color;

public class ShotgunWeapon extends Weapon {

    public ShotgunWeapon(Player owner) {
        super(owner);
        this.damage = 8;
        this.fireRate = 1.5; // 每秒1.5发
        this.maxAmmo = 30;
        this.currentAmmo = maxAmmo;
        this.isAutomatic = false;
        this.supportsCharging = true;
        this.chargedDamageMultiplier = 4;
    }

    @Override
    public void shoot(boolean isFacingRight) {
        if (!canShoot()) return;

        // 普通散弹：3发弹丸
        createShotgunBlast(isFacingRight, 3, 1.0);
        startCooldown();

        // TODO: 播放散弹枪音效
    }

    @Override
    public void shootCharged(boolean isFacingRight) {
        if (!canShoot()) return;

        // 蓄力散弹：7发弹丸，伤害更高
        createShotgunBlast(isFacingRight, 7, chargedDamageMultiplier);
        startCooldown();

        // TODO: 播放蓄力散弹枪音效
    }

    private void createShotgunBlast(boolean isFacingRight, int pelletCount, double damageMultiplier) {
        double baseX = owner.getX() + owner.getWidth() / 2;
        double baseY = owner.getY() + owner.getHeight() / 2;

        for (int i = 0; i < pelletCount; i++) {
            // 计算每发弹丸的偏移角度
            double angleOffset = (Math.random() - 0.5) * 0.5; // ±15度
            double direction = isFacingRight ? 1 : -1;

            // 计算速度
            double speed = 10 + Math.random() * 5;
            double speedX = Math.cos(angleOffset) * speed * direction;
            double speedY = Math.sin(angleOffset) * speed - 2; // 稍微向上

            // 创建弹丸
            Bullet pellet = new Bullet(baseX, baseY, 6, 6);
            pellet.setVelocityX(speedX);
            pellet.setVelocityY(speedY);
            pellet.setDamage((int)(damage * damageMultiplier / pelletCount * 2));
            pellet.setColor(damageMultiplier > 1 ? Color.GOLD : Color.YELLOW);
            pellet.setOwner(owner);

            bullets.add(pellet);
        }
    }
}