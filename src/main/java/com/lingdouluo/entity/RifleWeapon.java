package lingdouluo.entity;

import lingdouluo.entity.Bullet;
import lingdouluo.entity.Player;

/**
 * 步枪武器类，继承Weapon父类，解决所有编译错误
 */
public class RifleWeapon extends Weapon {
    private float range; // 步枪射程（float类型，避免转换错误）

    // 构造器：匹配父类参数，子弹速度为float，无类型转换损失
    public RifleWeapon() {
        // 参数：武器名称、最大升级等级、初始伤害、射速、子弹速度、最大弹药
        super("突击步枪", 5, 30, 0.1f, 800.0f, 30);
        this.range = 1500.0f; // 初始射程
    }

    /**
     * 实现父类抽象方法：步枪发射逻辑
     */
    @Override
    public Bullet fire(Player player) {
        // 先判断是否满足发射条件（冷却+弹药）
        if (!canFire()) {
            return null;
        }

        // 计算子弹初始位置（玩家头部位置，更贴合步枪发射）
        double bulletX = player.getX() + player.getWidth() / 2;
        double bulletY = player.getY() + player.getHeight() / 4;

        // 根据玩家朝向确定子弹方向
        double direction = player.isFacingRight() ? 1 : -1;

        // 创建步枪子弹（复用普通Bullet类）
        Bullet rifleBullet = new Bullet(bulletX, bulletY, direction);

        // 消耗弹药 + 重置冷却时间
        consumeAmmo();
        resetFireTimer();

        return rifleBullet;
    }

    /**
     * 实现父类抽象方法：步枪升级属性更新
     */
    @Override
    protected void updateAttributesAfterUpgrade() {
        // 步枪升级逻辑：提升伤害、加快射速、增加子弹速度和射程
        setDamage(getDamage() + 5); // 伤害+5
        setFireRate(getFireRate() - 0.01f); // 射速加快（冷却时间减少）
        setBulletSpeed(getBulletSpeed() + 50.0f); // 子弹速度+50（float，无转换损失）
        this.range += 200.0f; // 射程+200

        // 升级后补满弹药（使用setAmmo方法，解决找不到符号错误）
        setAmmo(getMaxAmmo());
    }

    /**
     * 步枪手动装填逻辑
     */
    public void reload() {
        setAmmo(getMaxAmmo()); // 补满弹药
        System.out.println("步枪装填完成！当前弹药：" + getCurrentAmmo());
    }

    // ==================== Getter & Setter ====================
    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }
}