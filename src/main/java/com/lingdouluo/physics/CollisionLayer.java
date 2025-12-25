// src/main/java/com/lingdouluo/physics/CollisionLayer.java
package com.lingdouluo.physics;

public enum CollisionLayer {
    PLAYER(1),
    ENEMY(2),
    BULLET(4),
    PLATFORM(8),
    POWER_UP(16),
    TRIGGER(32),
    ALL(63);

    private final int mask;

    CollisionLayer(int mask) {
        this.mask = mask;
    }

    public int getMask() {
        return mask;
    }

    public boolean canCollideWith(CollisionLayer other) {
        return (this.mask & other.mask) != 0;
    }
}