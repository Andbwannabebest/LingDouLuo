// src/main/java/com/lingdouluo/GameLoop.java
package com.lingdouluo;

import javafx.animation.AnimationTimer;

public class GameLoop extends AnimationTimer {

    private final LingDouLuoGame game;
    private long lastNanoTime;
    private static final double FRAME_TIME = 1_000_000_000.0 / 60.0; // 60 FPS

    public GameLoop(LingDouLuoGame game) {
        this.game = game;
        this.lastNanoTime = System.nanoTime();
    }

    @Override
    public void handle(long currentNanoTime) {
        double deltaTime = (currentNanoTime - lastNanoTime) / FRAME_TIME;
        lastNanoTime = currentNanoTime;

        // 限制deltaTime避免过大跳跃
        if (deltaTime > 2.0) {
            deltaTime = 1.0;
        }

        // 更新游戏状态
        game.update(deltaTime);

        // 渲染游戏
        game.render();
    }

    @Override
    public void start() {
        lastNanoTime = System.nanoTime();
        super.start();
    }
}