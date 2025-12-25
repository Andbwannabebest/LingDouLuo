package com.lingdouluo.physics;

/**
 * 碰撞层级枚举，用于区分不同类型实体的碰撞规则
 */
public enum CollisionLayer {
    PLAYER,    // 玩家层级
    ENEMY,     // 敌人层级
    BULLET,    // 子弹层级
    PLATFORM,  // 平台层级
    OTHER      // 其他层级
}