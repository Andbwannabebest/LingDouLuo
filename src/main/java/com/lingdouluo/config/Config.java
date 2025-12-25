// src/main/java/com/lingdouluo/config/Config.java
package com.lingdouluo.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;
import com.lingdouluo.*;

public class Config {
    // 窗口设置
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;

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

    // 音量设置
    public static double MASTER_VOLUME = 1.0;
    public static double MUSIC_VOLUME = 0.8;
    public static double SFX_VOLUME = 1.0;

    // 控制设置
    public static int KEY_UP = 87;        // W
    public static int KEY_DOWN = 83;      // S
    public static int KEY_LEFT = 65;      // A
    public static int KEY_RIGHT = 68;     // D
    public static int KEY_JUMP = 75;      // K
    public static int KEY_SHOOT = 74;     // J
    public static int KEY_WEAPON_SWITCH = 85; // U
    public static int KEY_PAUSE = 72;     // H
    public static int KEY_BACK = 73;      // I

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

                // 加载键盘设置
                KEY_UP = Integer.parseInt(props.getProperty("key_up", "87"));
                KEY_DOWN = Integer.parseInt(props.getProperty("key_down", "83"));
                KEY_LEFT = Integer.parseInt(props.getProperty("key_left", "65"));
                KEY_RIGHT = Integer.parseInt(props.getProperty("key_right", "68"));
                KEY_JUMP = Integer.parseInt(props.getProperty("key_jump", "75"));
                KEY_SHOOT = Integer.parseInt(props.getProperty("key_shoot", "74"));
                KEY_WEAPON_SWITCH = Integer.parseInt(props.getProperty("key_weapon_switch", "85"));

                // 加载音量设置
                MASTER_VOLUME = Double.parseDouble(props.getProperty("master_volume", "1.0"));
                MUSIC_VOLUME = Double.parseDouble(props.getProperty("music_volume", "0.8"));
                SFX_VOLUME = Double.parseDouble(props.getProperty("sfx_volume", "1.0"));
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
            // 保存键盘设置
            props.setProperty("key_up", String.valueOf(KEY_UP));
            props.setProperty("key_down", String.valueOf(KEY_DOWN));
            props.setProperty("key_left", String.valueOf(KEY_LEFT));
            props.setProperty("key_right", String.valueOf(KEY_RIGHT));
            props.setProperty("key_jump", String.valueOf(KEY_JUMP));
            props.setProperty("key_shoot", String.valueOf(KEY_SHOOT));
            props.setProperty("key_weapon_switch", String.valueOf(KEY_WEAPON_SWITCH));

            // 保存音量设置
            props.setProperty("master_volume", String.valueOf(MASTER_VOLUME));
            props.setProperty("music_volume", String.valueOf(MUSIC_VOLUME));
            props.setProperty("sfx_volume", String.valueOf(SFX_VOLUME));

            // 写入文件
            File configFile = new File(CONFIG_FILE);
            props.store(new FileWriter(configFile), "LingDouLuo Configuration");

        } catch (Exception e) {
            System.err.println("保存配置文件失败: " + e.getMessage());
        }
    }
}