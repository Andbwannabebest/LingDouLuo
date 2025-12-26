package lingdouluo.config;

/**
 * 游戏全局配置常量，统一管理所有硬编码参数
 * 修正：调整跳跃和重力参数，修复碰撞问题
 */
public class Config {
    // ===================== 屏幕配置 =====================
    public static final int SCREEN_WIDTH = 1920;       // 游戏窗口宽度（像素）
    public static final int SCREEN_HEIGHT = 1080;      // 游戏窗口高度（像素）
    public static final int TARGET_FPS = 60;           // 目标帧率
    public static final double DELTA_TIME = 1.0 / TARGET_FPS; // 每帧耗时（秒）

    // ===================== 玩家配置 =====================
    public static final int PLAYER_INIT_HEALTH = 100;  // 玩家初始生命值
    public static final double PLAYER_MOVE_SPEED = 300; // 玩家移动速度（像素/秒）
    public static final double PLAYER_JUMP_FORCE = -650; // 增加跳跃力（向上为负）
    public static final double PLAYER_GRAVITY = 1800;  // 增加重力加速度
    public static final int PLAYER_INVINCIBLE_DURATION = 2; // 玩家无敌时间（秒）
    public static final double PLAYER_WIDTH = 40;      // 玩家碰撞盒宽度（像素）
    public static final double PLAYER_HEIGHT = 60;     // 玩家碰撞盒高度（像素）
    public static final double PLAYER_INIT_X = 100;    // 玩家初始X坐标
    public static final double PLAYER_INIT_Y = 300;    // 玩家初始Y坐标

    // ===================== 敌人配置 =====================
    public static final int ENEMY_INIT_HEALTH = 50;    // 敌人初始生命值
    public static final double ENEMY_MOVE_SPEED = 200; // 增加敌人移动速度
    public static final int ENEMY_ATTACK_DAMAGE = 10;  // 敌人攻击伤害值
    public static final double ENEMY_WIDTH = 50;       // 敌人碰撞盒宽度
    public static final double ENEMY_HEIGHT = 50;      // 敌人碰撞盒高度

    // ===================== 子弹配置 =====================
    public static final double BULLET_SPEED = 800;     // 子弹飞行速度（像素/秒）
    public static final int BULLET_DAMAGE = 20;        // 子弹造成的伤害值
    public static final double BULLET_WIDTH = 10;      // 子弹碰撞盒宽度（像素）
    public static final double BULLET_HEIGHT = 5;      // 子弹碰撞盒高度（像素）
    public static final double BULLET_MAX_RANGE = 1500; // 增加子弹最大射程

    // ===================== 存档配置 =====================
    public static final String SAVE_PATH = "game_save.dat"; // 存档文件路径
    public static final String ENCRYPT_KEY = "LingDouluo_2025_"; // AES加密密钥（16位）

    // ===================== 关卡配置 =====================
    public static final String DEFAULT_LEVEL = "factory"; // 默认加载关卡
    public static final int LEVEL_ENEMY_COUNT = 8;        // 默认关卡敌人数量
    public static final int PLATFORM_DEFAULT_WIDTH = 200; // 平台默认宽度（像素）
    public static final int PLATFORM_DEFAULT_HEIGHT = 20; // 平台默认高度（像素）

    // ===================== 碰撞配置 =====================
    public static final double COLLISION_THRESHOLD = 5.0; // 碰撞检测阈值
}