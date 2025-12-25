// src/main/java/com/lingdouluo/input/InputManager.java
package com.lingdouluo.input;

import java.util.HashSet;
import java.util.Set;

public class InputManager {

    private Set<Integer> pressedKeys;
    private Set<Integer> justPressedKeys;

    // 鼠标/手柄状态（未来扩展）
    private double mouseX;
    private double mouseY;
    private boolean mousePressed;

    public InputManager() {
        pressedKeys = new HashSet<>();
        justPressedKeys = new HashSet<>();
        mouseX = 0;
        mouseY = 0;
        mousePressed = false;
    }

    public void handleKeyPressed(javafx.scene.input.KeyEvent event) {
        int keyCode = event.getCode().getCode();
        pressedKeys.add(keyCode);
        justPressedKeys.add(keyCode);
    }

    public void handleKeyReleased(javafx.scene.input.KeyEvent event) {
        int keyCode = event.getCode().getCode();
        pressedKeys.remove(keyCode);
    }

    public void handleMousePressed(javafx.scene.input.MouseEvent event) {
        mouseX = event.getX();
        mouseY = event.getY();
        mousePressed = true;
    }

    public void handleMouseReleased(javafx.scene.input.MouseEvent event) {
        mouseX = event.getX();
        mouseY = event.getY();
        mousePressed = false;
    }

    public void handleMouseMoved(javafx.scene.input.MouseEvent event) {
        mouseX = event.getX();
        mouseY = event.getY();
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

    // 鼠标输入检查
    public double getMouseX() { return mouseX; }
    public double getMouseY() { return mouseY; }
    public boolean isMousePressed() { return mousePressed; }

    // 组合键检查（未来扩展）
    public boolean isShiftPressed() {
        return isKeyPressed(16); // Shift键
    }

    public boolean isControlPressed() {
        return isKeyPressed(17); // Ctrl键
    }

    public void clear() {
        pressedKeys.clear();
        justPressedKeys.clear();
        mousePressed = false;
    }
}