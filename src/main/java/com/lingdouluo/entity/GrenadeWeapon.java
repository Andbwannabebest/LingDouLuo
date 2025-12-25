package com.lingdouluo.entity;

import com.lingdouluo.entity.Bullet;
import com.lingdouluo.entity.Player;
import com.lingdouluo.entity.Weapon;

/**
 * 手榴弹武器类，基于基础Weapon类实现，使用普通Bullet类，无自定义GrenadeBullet依赖
 */
public class GrenadeWeapon extends Weapon {

    // 构造器：匹配父类Weapon参数，初始化手榴弹属性
    public GrenadeWeapon() {
        // 参数：武器名称、最大升级等级、初始伤害、射速（冷却时间）、子弹速度、最大弹药
        super("手榴弹", 3, 50, 1.5f, 200, 5);
    }

    /**
     * 发射手榴弹（使用普通Bullet类，模拟手榴弹效果）
     * @param player 持有武器的玩家
     * @return 生成的子弹实例，无法发射则返回null
     */
    @Override
    public Bullet fire(Player player) {
        // 先判断是否满足发射条件（冷却完成 + 有弹药）
        if (!canFire()) {
            return null;
        }

        // 计算子弹初始位置（玩家中心，更贴合手榴弹发射逻辑）
        double bulletX = player.getX() + player.getWidth() / 2;
        double bulletY = player.getY() + player.getHeight() / 2;

        // 根据玩家朝向确定子弹移动方向（1=右，-1=左）
        double direction = player.isFacingRight() ? 1 : -1;

        // 使用普通Bullet类创建手榴弹子弹（复用现有子弹逻辑）
        Bullet grenadeBullet = new Bullet(bulletX, bulletY, direction);

        // 消耗弹药 + 重置冷却时间
        consumeAmmo();
        resetFireTimer();

        return grenadeBullet;
    }

    /**
     * 升级后更新属性（实现父类抽象方法，解决未覆盖抽象方法错误）
     */
    @Override
    protected void updateAttributesAfterUpgrade() {
        // 手榴弹升级成长逻辑：提升伤害、加快射速、增加子弹速度、扩充弹药
        setDamage(getDamage() + 20); // 伤害+20
        setFireRate(getFireRate() - 0.2f); // 射速加快（冷却时间减少0.2秒）
        setBulletSpeed(getBulletSpeed() + 50); // 子弹速度+50
        setMaxAmmo(getMaxAmmo() + 1); // 最大弹药+1
        reload(getMaxAmmo()); // 升级后自动补满弹药
    }
}