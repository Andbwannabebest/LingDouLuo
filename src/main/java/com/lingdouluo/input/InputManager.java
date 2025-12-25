// src/main/java/com/lingdouluo/input/InputManager.java
package com.lingdouluo.input;

import com.lingdouluo.config.Config;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.Set;

public class InputManager {

    private Set<Integer> pressedKeys;
    private Set<Integer> justPressedKeys;

    // 玩家输入状态
    private boolean p1Up, p1Down, p1Left, p1Right, p1Jump, p1Shoot, p1WeaponSwitch, p1Pause;
    private boolean p2Up, p2Down, p2Left, p2Right, p2Jump, p2Shoot, p2WeaponSwitch, p2Pause;
    private boolean backPressed, escapePressed;

    public InputManager() {
        pressedKeys = new HashSet<>();
        justPressedKeys = new HashSet<>();
    }

    public void handleKeyPressed(KeyEvent event) {
        int keyCode = event.getCode().getCode();
        pressedKeys.add(keyCode);
        justPressedKeys.add(keyCode);

        // 更新玩家输入状态
        updateInputStates();
    }

    public void handleKeyReleased(KeyEvent event) {
        int keyCode = event.getCode().getCode();
        pressedKeys.remove(keyCode);

        // 更新玩家输入状态
        updateInputStates();
    }

    private void updateInputStates() {
        // 玩家1输入
        p1Up = isKeyPressed(Config.KEY_P1_UP);
        p1Down = isKeyPressed(Config.KEY_P1_DOWN);
        p1Left = isKeyPressed(Config.KEY_P1_LEFT);
        p1Right = isKeyPressed(Config.KEY_P1_RIGHT);
        p1Jump = isKeyPressed(Config.KEY_P1_JUMP);
        p1Shoot = isKeyPressed(Config.KEY_P1_SHOOT);
        p1WeaponSwitch = isKeyPressed(Config.KEY_P1_WEAPON_SWITCH);
        p1Pause = isKeyPressed(Config.KEY_P1_PAUSE);

        // 玩家2输入
        p2Up = isKeyPressed(Config.KEY_P2_UP);
        p2Down = isKeyPressed(Config.KEY_P2_DOWN);
        p2Left = isKeyPressed(Config.KEY_P2_LEFT);
        p2Right = isKeyPressed(Config.KEY_P2_RIGHT);
        p2Jump = isKeyPressed(Config.KEY_P2_JUMP);
        p2Shoot = isKeyPressed(Config.KEY_P2_SHOOT);
        p2WeaponSwitch = isKeyPressed(Config.KEY_P2_WEAPON_SWITCH);
        p2Pause = isKeyPressed(Config.KEY_P2_PAUSE);

        // 通用输入
        backPressed = isKeyPressed(Config.KEY_BACK);
        escapePressed = isKeyPressed(Config.KEY_ESCAPE);
    }

    public void update() {
        // 清除刚按下的键
        justPressedKeys.clear();
    }

    // 键盘输入检查
    public boolean isKeyPressed(int keyCode) {
        return pressedKeys.contains(keyCode);
    }

    public boolean isKeyJustPressed(int keyCode) {
        return justPressedKeys.contains(keyCode);
    }

    // 玩家1输入获取方法
    public boolean isP1Up() { return p1Up; }
    public boolean isP1Down() { return p1Down; }
    public boolean isP1Left() { return p1Left; }
    public boolean isP1Right() { return p1Right; }
    public boolean isP1Jump() { return p1Jump; }
    public boolean isP1Shoot() { return p1Shoot; }
    public boolean isP1WeaponSwitch() { return p1WeaponSwitch; }
    public boolean isP1Pause() { return p1Pause; }

    // 玩家2输入获取方法
    public boolean isP2Up() { return p2Up; }
    public boolean isP2Down() { return p2Down; }
    public boolean isP2Left() { return p2Left; }
    public boolean isP2Right() { return p2Right; }
    public boolean isP2Jump() { return p2Jump; }
    public boolean isP2Shoot() { return p2Shoot; }
    public boolean isP2WeaponSwitch() { return p2WeaponSwitch; }
    public boolean isP2Pause() { return p2Pause; }

    // 通用输入获取方法
    public boolean isBackPressed() { return backPressed; }
    public boolean isEscapePressed() { return escapePressed; }

    public boolean isAnyPausePressed() {
        return isP1Pause() || isP2Pause() || isEscapePressed();
    }

    public void clear() {
        pressedKeys.clear();
        justPressedKeys.clear();
    }
}