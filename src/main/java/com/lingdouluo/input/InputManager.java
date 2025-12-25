package com.lingdouluo.input;

import javafx.scene.input.KeyCode;
import java.util.HashSet;
import java.util.Set;

/**
 * 输入管理单例类，统一监听和管理键盘按键状态
 */
public class InputManager {
    // 单例实例
    private static InputManager instance;
    // 存储当前按下的按键
    private Set<KeyCode> pressedKeys;

    private InputManager() {
        pressedKeys = new HashSet<>();
    }

    /**
     * 获取单例对象
     */
    public static InputManager getInstance() {
        if (instance == null) {
            instance = new InputManager();
        }
        return instance;
    }

    /**
     * 按键按下时调用
     */
    public void keyPressed(KeyCode keyCode) {
        pressedKeys.add(keyCode);
    }

    /**
     * 按键释放时调用
     */
    public void keyReleased(KeyCode keyCode) {
        pressedKeys.remove(keyCode);
    }

    /**
     * 判断某个按键是否正在被按下
     */
    public boolean isKeyPressed(KeyCode keyCode) {
        return pressedKeys.contains(keyCode);
    }

    /**
     * 清空所有按键状态（如游戏暂停/结束时）
     */
    public void clearPressedKeys() {
        pressedKeys.clear();
    }
}