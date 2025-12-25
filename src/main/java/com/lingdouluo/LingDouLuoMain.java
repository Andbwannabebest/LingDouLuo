// src/main/java/com/lingdouluo/LingDouLuoMain.java
package com.lingdouluo;

import com.lingdouluo.config.Config;
import com.lingdouluo.ui.HUD;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class LingDouLuoMain extends Application {

    private LingDouLuoGame game;
    private GameLoop gameLoop;
    private Canvas gameCanvas;
    private GraphicsContext gc;
    private HUD hud;

    @Override
    public void start(Stage primaryStage) {
        try {
            // 初始化配置
            Config.loadConfig();

            // 创建游戏画布
            gameCanvas = new Canvas(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
            gc = gameCanvas.getGraphicsContext2D();

            // 创建游戏实例
            game = new LingDouLuoGame(gc);

            // 创建HUD
            hud = new HUD(game.getPlayer());

            // 设置游戏循环
            gameLoop = new GameLoop(game);

            // 创建主布局
            BorderPane root = new BorderPane();
            root.setCenter(gameCanvas);
            root.setTop(hud.createHUD());

            // 创建场景
            Scene scene = new Scene(root, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

            // 设置输入处理
            scene.setOnKeyPressed(game.getInputManager()::handleKeyPressed);
            scene.setOnKeyReleased(game.getInputManager()::handleKeyReleased);

            // 配置舞台
            primaryStage.setTitle("灵斗罗 - LingDouLuo");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

            // 启动游戏循环
            gameLoop.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        // 保存游戏进度
        if (game != null) {
            game.saveGame();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}