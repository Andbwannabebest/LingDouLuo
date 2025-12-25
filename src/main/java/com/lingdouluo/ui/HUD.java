// src/main/java/com/lingdouluo/ui/HUD.java
package com.lingdouluo.ui;

import com.lingdouluo.entity.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class HUD {

    private Player player1;
    private Player player2;

    // 玩家1的UI组件
    private Label p1HealthLabel;
    private Label p1ScoreLabel;
    private Label p1AmmoLabel;
    private Label p1WeaponLabel;
    private Rectangle p1HealthBar;

    // 玩家2的UI组件
    private Label p2HealthLabel;
    private Label p2ScoreLabel;
    private Label p2AmmoLabel;
    private Label p2WeaponLabel;
    private Rectangle p2HealthBar;

    public HUD(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public VBox createHUD() {
        VBox hudContainer = new VBox(10);
        hudContainer.setPadding(new Insets(10));
        hudContainer.setAlignment(Pos.TOP_LEFT);
        hudContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        // 标题
        Label title = new Label("灵斗罗 - 双人模式");
        title.setTextFill(Color.YELLOW);
        title.setFont(Font.font("Arial", 20));

        // 玩家1状态
        VBox player1Box = createPlayerBox(player1, "玩家1 (蓝色)", Color.CYAN);
        p1HealthLabel = (Label) ((HBox) player1Box.getChildren().get(1)).getChildren().get(1);
        p1ScoreLabel = (Label) ((HBox) player1Box.getChildren().get(2)).getChildren().get(1);
        p1WeaponLabel = (Label) ((HBox) player1Box.getChildren().get(3)).getChildren().get(1);
        p1AmmoLabel = (Label) ((HBox) player1Box.getChildren().get(4)).getChildren().get(1);
        p1HealthBar = (Rectangle) ((HBox) player1Box.getChildren().get(1)).getChildren().get(2);

        // 玩家2状态
        VBox player2Box = createPlayerBox(player2, "玩家2 (粉色)", Color.PINK);
        p2HealthLabel = (Label) ((HBox) player2Box.getChildren().get(1)).getChildren().get(1);
        p2ScoreLabel = (Label) ((HBox) player2Box.getChildren().get(2)).getChildren().get(1);
        p2WeaponLabel = (Label) ((HBox) player2Box.getChildren().get(3)).getChildren().get(1);
        p2AmmoLabel = (Label) ((HBox) player2Box.getChildren().get(4)).getChildren().get(1);
        p2HealthBar = (Rectangle) ((HBox) player2Box.getChildren().get(1)).getChildren().get(2);

        // 将两个玩家状态水平排列
        HBox playersRow = new HBox(20);
        playersRow.getChildren().addAll(player1Box, player2Box);

        hudContainer.getChildren().addAll(title, playersRow);

        // 启动HUD更新线程
        startHUDUpdate();

        return hudContainer;
    }

    private VBox createPlayerBox(Player player, String title, Color color) {
        VBox playerBox = new VBox(5);
        playerBox.setStyle(String.format("-fx-border-color: %s; -fx-border-width: 2; -fx-padding: 5;",
                color.toString().replace("0x", "#")));

        // 玩家标题
        Label playerTitle = new Label(title);
        playerTitle.setTextFill(color);
        playerTitle.setFont(Font.font("Arial", 14));

        // 生命值
        HBox healthBox = new HBox(10);
        healthBox.setAlignment(Pos.CENTER_LEFT);

        Label healthTitle = new Label("生命:");
        healthTitle.setTextFill(Color.WHITE);

        Label healthValue = new Label(player.getHealth() + "/" + player.getMaxHealth());
        healthValue.setTextFill(Color.LIME);

        Rectangle healthBar = new Rectangle(100, 10);
        healthBar.setFill(Color.RED);
        healthBar.setStroke(Color.BLACK);
        healthBar.setStrokeWidth(1);

        healthBox.getChildren().addAll(healthTitle, healthValue, healthBar);

        // 分数
        HBox scoreBox = new HBox(10);
        scoreBox.setAlignment(Pos.CENTER_LEFT);

        Label scoreTitle = new Label("分数:");
        scoreTitle.setTextFill(Color.WHITE);

        Label scoreValue = new Label(String.valueOf(player.getScore()));
        scoreValue.setTextFill(Color.YELLOW);

        scoreBox.getChildren().addAll(scoreTitle, scoreValue);

        // 武器
        HBox weaponBox = new HBox(10);
        weaponBox.setAlignment(Pos.CENTER_LEFT);

        Label weaponTitle = new Label("武器:");
        weaponTitle.setTextFill(Color.WHITE);

        Label weaponName = new Label(getWeaponName(player));
        weaponName.setTextFill(Color.CYAN);

        weaponBox.getChildren().addAll(weaponTitle, weaponName);

        // 弹药
        HBox ammoBox = new HBox(10);
        ammoBox.setAlignment(Pos.CENTER_LEFT);

        Label ammoTitle = new Label("弹药:");
        ammoTitle.setTextFill(Color.WHITE);

        Label ammoValue = new Label(getAmmoText(player));
        ammoValue.setTextFill(Color.ORANGE);

        ammoBox.getChildren().addAll(ammoTitle, ammoValue);

        playerBox.getChildren().addAll(playerTitle, healthBox, scoreBox, weaponBox, ammoBox);

        return playerBox;
    }

    private String getWeaponName(Player player) {
        if (player.getCurrentWeapon() != null) {
            String className = player.getCurrentWeapon().getClass().getSimpleName();
            if (className.contains("Rifle")) return "步枪";
            if (className.contains("Grenade")) return "榴弹";
            return className.replace("Weapon", "");
        }
        return "无";
    }

    private String getAmmoText(Player player) {
        if (player.getCurrentWeapon() != null) {
            int ammo = player.getCurrentWeapon().getCurrentAmmo();
            return ammo == -1 ? "∞" : String.valueOf(ammo);
        }
        return "0";
    }

    private void startHUDUpdate() {
        Thread updateThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100); // 每100毫秒更新一次
                    updateHUD();
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        updateThread.setDaemon(true);
        updateThread.start();
    }

    private void updateHUD() {
        if (player1 == null || player2 == null) return;

        // 在JavaFX应用线程中更新UI
        javafx.application.Platform.runLater(() -> {
            // 更新玩家1
            updatePlayerUI(player1, p1HealthLabel, p1ScoreLabel, p1WeaponLabel, p1AmmoLabel, p1HealthBar);

            // 更新玩家2
            updatePlayerUI(player2, p2HealthLabel, p2ScoreLabel, p2WeaponLabel, p2AmmoLabel, p2HealthBar);
        });
    }

    private void updatePlayerUI(Player player, Label healthLabel, Label scoreLabel,
                                Label weaponLabel, Label ammoLabel, Rectangle healthBar) {
        if (!player.isActive()) return;

        // 更新生命值
        int health = player.getHealth();
        int maxHealth = player.getMaxHealth();
        healthLabel.setText(health + "/" + maxHealth);

        // 更新生命条
        double healthPercent = (double) health / maxHealth;
        healthBar.setWidth(100 * healthPercent);

        // 根据生命值改变颜色
        if (healthPercent > 0.6) {
            healthBar.setFill(Color.LIME);
            healthLabel.setTextFill(Color.LIME);
        } else if (healthPercent > 0.3) {
            healthBar.setFill(Color.YELLOW);
            healthLabel.setTextFill(Color.YELLOW);
        } else {
            healthBar.setFill(Color.RED);
            healthLabel.setTextFill(Color.RED);
        }

        // 更新分数
        scoreLabel.setText(String.valueOf(player.getScore()));

        // 更新武器信息
        weaponLabel.setText(getWeaponName(player));
        ammoLabel.setText(getAmmoText(player));
    }
}