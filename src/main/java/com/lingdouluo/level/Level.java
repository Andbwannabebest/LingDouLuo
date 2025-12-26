package lingdouluo.level;

import lingdouluo.config.Config;
import java.util.ArrayList;
import java.util.List;

public class Level {
    private String levelName;
    private List<Platform> platforms;
    private int enemyCount;

    // 构造器
    public Level(String levelName) {
        this.levelName = levelName;
        this.platforms = new ArrayList<>();
        this.enemyCount = Config.LEVEL_ENEMY_COUNT;
        initPlatforms();
    }

    // 初始化平台（调整平台高度，使其更容易跳上去）
    private void initPlatforms() {
        if ("factory".equals(levelName.toLowerCase())) {
            // 降低平台高度，使玩家更容易跳上去
            platforms.add(new Platform(300, 900, 200, 20, false));  // 地面附近
            platforms.add(new Platform(600, 700, 150, 20, true));   // 中等高度
            platforms.add(new Platform(900, 500, 200, 20, false));  // 较高但可跳跃到达
            platforms.add(new Platform(1200, 800, 150, 20, true)); // 回到较低高度

            // 添加更多平台增加游戏性
            platforms.add(new Platform(400, 600, 100, 20, false));
            platforms.add(new Platform(1100, 400, 100, 20, true));
        } else {
            // 默认关卡平台配置
            platforms.add(new Platform(200, 800, 250, 20, false));
            platforms.add(new Platform(500, 600, 200, 20, false));
            platforms.add(new Platform(800, 400, 250, 20, false));
            platforms.add(new Platform(1100, 600, 200, 20, false));
            platforms.add(new Platform(1400, 800, 250, 20, false));
        }
    }

    // 修正：改为public访问权限
    public void updatePlatforms() {
        // 静态平台无需更新，空实现（可扩展移动平台逻辑）
    }

    // ==================== 内部类：Platform ====================
    public class Platform {
        private float x;
        private float y;
        private float width;
        private float height;
        private boolean isSolid;

        public Platform(float x, float y, float width, float height, boolean isSolid) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.isSolid = isSolid;
        }

        // Getter & Setter
        public float getX() { return x; }
        public float getY() { return y; }
        public float getWidth() { return width; }
        public float getHeight() { return height; }
        public boolean isSolid() { return isSolid; }
    }

    // ==================== Getter & Setter ====================
    public String getLevelName() { return levelName; }
    public List<Platform> getPlatforms() { return platforms; }
    public int getEnemyCount() { return enemyCount; }
}