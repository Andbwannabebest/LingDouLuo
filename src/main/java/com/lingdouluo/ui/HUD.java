// src/main/java/com/lingdouluo/ui/HUD.java
package com.lingdouluo.ui;

import com.lingdouluo.config.Config;
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

    private Player player;
    private Label healthLabel;
    private Label scoreLabel;
    private Label ammoLabel;
    private Label weaponLabel;
    private Label levelLabel;

    public HUD(Player player) {
        this.player = player;
    }

    public VBox createHUD() {
        VBox hudContainer = new VBox(10);
        hudContainer.setPadding(new Insets(10));
        hudContainer.setAlignment(Pos.TOP_LEFT);
        hudContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        // 健康值显示
        HBox healthBox = new HBox(10);
        healthBox.setAlignment(Pos.CENTER_LEFT);

        Label healthTitle = new Label("生命值:");
        healthTitle.setTextFill(Color.WHITE);
        healthTitle.setFont(Font.font("Arial", 16));

        healthLabel = new Label("3/3");
        healthLabel.setTextFill(Color.LIME);
        healthLabel.setFont(Font.font("Arial", 16));

        // 健康条
        Rectangle healthBar = new Rectangle(200, 20);
        healthBar.setFill(Color.RED);
        healthBar.setStroke(Color.BLACK);
        healthBar.setStrokeWidth(1);

        healthBox.getChildren().addAll(healthTitle, healthLabel, healthBar);

        // 分数显示
        HBox scoreBox = new HBox(10);
        scoreBox.setAlignment(Pos.CENTER_LEFT);

        Label scoreTitle = new Label("分数:");
        scoreTitle.setTextFill(Color.WHITE);
        scoreTitle.setFont(Font.font("Arial", 16));

        scoreLabel = new Label("0");
        scoreLabel.setTextFill(Color.YELLOW);
        scoreLabel.setFont(Font.font("Arial", 16));

        scoreBox.getChildren().addAll(scoreTitle, scoreLabel);

        // 武器信息
        HBox weaponBox = new HBox(10);
        weaponBox.setAlignment(Pos.CENTER_LEFT);

        Label weaponTitle = new Label("武器:");
        weaponTitle.setTextFill(Color.WHITE);
        weaponTitle.setFont(Font.font("Arial", 16));

        weaponLabel = new Label("步枪");
        weaponLabel.setTextFill(Color.CYAN);
        weaponLabel.setFont(Font.font("Arial", 16));

        ammoLabel = new Label("∞");
        ammoLabel.setTextFill(Color.ORANGE);
        ammoLabel.setFont(Font.font("Arial", 16));

        weaponBox.getChildren().addAll(weaponTitle, weaponLabel, ammoLabel);

        // 关卡信息
        HBox levelBox = new HBox(10);
        levelBox.setAlignment(Pos.CENTER_LEFT);

        Label levelTitle = new Label("关卡:");
        levelTitle.setTextFill(Color.WHITE);
        levelTitle.setFont(Font.font("Arial", 16));

        levelLabel = new Label("工厂区");
        levelLabel.setTextFill(Color.WHITE);
        levelLabel.setFont(Font.font("Arial", 16));

        levelBox.getChildren().addAll(levelTitle, levelLabel);

        // 将所有组件添加到容器
        hudContainer.getChildren().addAll(healthBox, scoreBox, weaponBox, levelBox);

        return hudContainer;
    }

    public void updateHUD() {
        if (player == null) return;

        // 更新健康值
        healthLabel.setText(player.getHealth() + "/" + player.getMaxHealth());

        // 更新分数
        scoreLabel.setText(String.valueOf(player.getScore()));

        // 更新武器信息
        if (player.getCurrentWeapon() != null) {
            String weaponName = player.getCurrentWeapon().getClass().getSimpleName();
            weaponLabel.setText(weaponName.replace("Weapon", ""));

            int ammo = player.getCurrentWeapon().getCurrentAmmo();
            ammoLabel.setText(ammo == -1 ? "∞" : String.valueOf(ammo));
        }
    }
}