package com.lingdouluo.ui;

import com.lingdouluo.entity.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import com.lingdouluo.config.Config;

public class HUD {
    private Player player;
    private final Font font = Font.font(16);

    public HUD(Player player) {
        this.player = player;
    }

    // 渲染HUD（生命条+当前武器）
    public void render(GraphicsContext gc) {
        // 生命条背景
        gc.setFill(Color.DARKRED);
        gc.fillRect(20, 20, 200, 20);
        // 生命条前景（按当前生命值比例）
        double healthRatio = (double) player.getHealth() / player.getMaxHealth();
        gc.setFill(Color.RED);
        gc.fillRect(20, 20, 200 * healthRatio, 20);
        // 生命条边框
        gc.setStroke(Color.WHITE);
        gc.strokeRect(20, 20, 200, 20);

        // 当前武器显示
        gc.setFont(font);
        gc.setFill(Color.WHITE);
        gc.fillText("当前武器：" + player.getCurrentWeapon().getName(), 20, 60);

        // 操作提示
        // 操作提示（修正SCREEN_HEIGHT的引用）
        gc.fillText("↑↓←→移动 | 空格跳跃 | Z射击 | X切换武器 | C闪避 | ESC存档暂停", 20, Config.SCREEN_HEIGHT - 10);
    }
}