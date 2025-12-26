// src/main/java/com/lingdouluo/entity/ShootingEnemy.java
package com.lingdouluo.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class ShootingEnemy extends Enemy {

    private Player target;
    private long lastShotTime;
    private static final long SHOOT_INTERVAL = 1500; // 毫秒
    private List<Bullet> bullets;

    public ShootingEnemy(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.health = 20;
        this.maxHealth = 20;
        this.damage = 15;
        this.scoreValue = 150;
        this.lastShotTime = 0;
        this.bullets = new ArrayList<>();
    }

    @Override
    public void update(double deltaTime) {
        if (!isActive) return;

        applyPhysics(deltaTime);

        // 如果有目标，尝试射击
        if (target != null && target.isActive()) {
            tryShoot();
        }

        // 更新子弹
        updateBullets(deltaTime);
    }

    private void tryShoot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime > SHOOT_INTERVAL) {
            shoot();
            lastShotTime = currentTime;
        }
    }

    private void shoot() {
        if (target == null || !target.isActive()) return;

        // 计算射击方向
        double targetX = target.getX() + target.getWidth() / 2;
        double targetY = target.getY() + target.getHeight() / 2;
        double enemyX = x + width / 2;
        double enemyY = y + height / 2;

        double dx = targetX - enemyX;
        double dy = targetY - enemyY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0 && distance < 400) { // 只在400像素内射击
            double speed = 3.0;
            double speedX = (dx / distance) * speed;
            double speedY = (dy / distance) * speed;

            Bullet bullet = new Bullet(enemyX, enemyY, 8, 8);
            bullet.setVelocityX(speedX);
            bullet.setVelocityY(speedY);
            bullet.setDamage(damage);
            bullet.setColor(Color.RED);

            bullets.add(bullet);
        }
    }

    private void updateBullets(double deltaTime) {
        List<Bullet> bulletsToRemove = new ArrayList<>();

        for (Bullet bullet : bullets) {
            bullet.update(deltaTime);

            if (!bullet.isActive()) {
                bulletsToRemove.add(bullet);
            }
        }

        bullets.removeAll(bulletsToRemove);
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isActive) return;

        // 绘制敌人身体
        gc.setFill(Color.rgb(200, 50, 50)); // 红色射击敌人
        gc.fillRect(x, y, width, height);

        // 绘制炮管
        gc.setFill(Color.rgb(100, 100, 100));
        gc.fillRect(x + width/2 - 5, y - 10, 10, 15);

        // 绘制瞄准镜
        if (target != null && target.isActive()) {
            gc.setFill(Color.rgb(255, 255, 0, 0.2));
            gc.fillOval(x - 50, y - 50, width + 100, height + 100);
        }

        // 绘制子弹
        for (Bullet bullet : bullets) {
            bullet.render(gc);
        }

        // 绘制生命条
        drawHealthBar(gc);
    }

    public void setTarget(Player target) {
        this.target = target;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }
}