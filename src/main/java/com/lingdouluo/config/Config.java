// src/main/java/com/lingdouluo/config/Config.java
package com.lingdouluo.config;

import com.lingdouluo.GameState;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

public class Config {
    // 窗口设置
    public static final int WINDOW_WIDTH = 900;
    public static final int WINDOW_HEIGHT = 550;

    // 游戏设置
    public static final double GRAVITY = 0.5;
    public static final double PLAYER_JUMP_FORCE = -12.0;
    public static final double PLAYER_MOVE_SPEED = 5.0;
    public static final int PLAYER_MAX_HEALTH = 3;
    public static final int PLAYER_MAX_LIVES = 3;

    // 物理设置
    public static final double FRICTION = 0.8;
    public static final double AIR_RESISTANCE = 0.95;

    // 游戏状态
    public static GameState GAME_STATE = GameState.MENU;

    // 玩家1控制设置 (WASD + JKUH)
    public static int KEY_P1_UP = 87;        // W
    public static int KEY_P1_DOWN = 83;      // S
    public static int KEY_P1_LEFT = 65;      // A
    public static int KEY_P1_RIGHT = 68;     // D
    public static int KEY_P1_JUMP = 75;      // K
    public static int KEY_P1_SHOOT = 74;     // J
    public static int KEY_P1_WEAPON_SWITCH = 85; // U
    public static int KEY_P1_PAUSE = 72;     // H

    // 玩家2控制设置 (方向键 + 数字键)
    public static int KEY_P2_UP = 38;        // ↑
    public static int KEY_P2_DOWN = 40;      // ↓
    public static int KEY_P2_LEFT = 37;      // ←
    public static int KEY_P2_RIGHT = 39;     // →
    public static int KEY_P2_JUMP = 51;      // 3
    public static int KEY_P2_SHOOT = 50;     // 2
    public static int KEY_P2_WEAPON_SWITCH = 53; // 5
    public static int KEY_P2_PAUSE = 57;     // 9

    // 通用控制
    public static int KEY_BACK = 73;         // I
    public static int KEY_ESCAPE = 27;       // ESC

    // 资源路径
    public static final String ASSETS_PATH = "assets/";
    public static final String IMAGES_PATH = ASSETS_PATH + "images/";
    public static final String SOUNDS_PATH = ASSETS_PATH + "sounds/";
    public static final String SAVES_PATH = "saves/";
    public static final String CONFIG_FILE = "config.properties";

    public static void loadConfig() {
        Properties props = new Properties();
        File configFile = new File(CONFIG_FILE);

        try {
            if (configFile.exists()) {
                props.load(new FileReader(configFile));

                // 加载玩家1键盘设置
                KEY_P1_UP = Integer.parseInt(props.getProperty("key_p1_up", "87"));
                KEY_P1_DOWN = Integer.parseInt(props.getProperty("key_p1_down", "83"));
                KEY_P1_LEFT = Integer.parseInt(props.getProperty("key_p1_left", "65"));
                KEY_P1_RIGHT = Integer.parseInt(props.getProperty("key_p1_right", "68"));
                KEY_P1_JUMP = Integer.parseInt(props.getProperty("key_p1_jump", "75"));
                KEY_P1_SHOOT = Integer.parseInt(props.getProperty("key_p1_shoot", "74"));
                KEY_P1_WEAPON_SWITCH = Integer.parseInt(props.getProperty("key_p1_weapon_switch", "85"));
                KEY_P1_PAUSE = Integer.parseInt(props.getProperty("key_p1_pause", "72"));

                // 加载玩家2键盘设置
                KEY_P2_UP = Integer.parseInt(props.getProperty("key_p2_up", "38"));
                KEY_P2_DOWN = Integer.parseInt(props.getProperty("key_p2_down", "40"));
                KEY_P2_LEFT = Integer.parseInt(props.getProperty("key_p2_left", "37"));
                KEY_P2_RIGHT = Integer.parseInt(props.getProperty("key_p2_right", "39"));
                KEY_P2_JUMP = Integer.parseInt(props.getProperty("key_p2_jump", "51"));
                KEY_P2_SHOOT = Integer.parseInt(props.getProperty("key_p2_shoot", "50"));
                KEY_P2_WEAPON_SWITCH = Integer.parseInt(props.getProperty("key_p2_weapon_switch", "53"));
                KEY_P2_PAUSE = Integer.parseInt(props.getProperty("key_p2_pause", "57"));

                // 加载通用控制
                KEY_BACK = Integer.parseInt(props.getProperty("key_back", "73"));
                KEY_ESCAPE = Integer.parseInt(props.getProperty("key_escape", "27"));

            } else {
                // 创建默认配置文件
                saveConfig();
            }
        } catch (Exception e) {
            System.err.println("加载配置文件失败: " + e.getMessage());
        }
    }

    public static void saveConfig() {
        Properties props = new Properties();

        try {
            // 保存玩家1键盘设置
            props.setProperty("key_p1_up", String.valueOf(KEY_P1_UP));
            props.setProperty("key_p1_down", String.valueOf(KEY_P1_DOWN));
            props.setProperty("key_p1_left", String.valueOf(KEY_P1_LEFT));
            props.setProperty("key_p1_right", String.valueOf(KEY_P1_RIGHT));
            props.setProperty("key_p1_jump", String.valueOf(KEY_P1_JUMP));
            props.setProperty("key_p1_shoot", String.valueOf(KEY_P1_SHOOT));
            props.setProperty("key_p1_weapon_switch", String.valueOf(KEY_P1_WEAPON_SWITCH));
            props.setProperty("key_p1_pause", String.valueOf(KEY_P1_PAUSE));

            // 保存玩家2键盘设置
            props.setProperty("key_p2_up", String.valueOf(KEY_P2_UP));
            props.setProperty("key_p2_down", String.valueOf(KEY_P2_DOWN));
            props.setProperty("key_p2_left", String.valueOf(KEY_P2_LEFT));
            props.setProperty("key_p2_right", String.valueOf(KEY_P2_RIGHT));
            props.setProperty("key_p2_jump", String.valueOf(KEY_P2_JUMP));
            props.setProperty("key_p2_shoot", String.valueOf(KEY_P2_SHOOT));
            props.setProperty("key_p2_weapon_switch", String.valueOf(KEY_P2_WEAPON_SWITCH));
            props.setProperty("key_p2_pause", String.valueOf(KEY_P2_PAUSE));

            // 保存通用控制
            props.setProperty("key_back", String.valueOf(KEY_BACK));
            props.setProperty("key_escape", String.valueOf(KEY_ESCAPE));

            // 写入文件
            File configFile = new File(CONFIG_FILE);
            props.store(new FileWriter(configFile), "LingDouLuo Configuration");

        } catch (Exception e) {
            System.err.println("保存配置文件失败: " + e.getMessage());
        }
    }
}