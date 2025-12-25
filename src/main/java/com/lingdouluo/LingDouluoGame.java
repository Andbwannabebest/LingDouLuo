package com.lingdouluo;

import com.lingdouluo.config.Config; // 导入Config类，确保屏幕常量可访问
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class LingDouluoGame extends Application {
    private GameLoop gameLoop;
    private Canvas canvas;
    private GraphicsContext gc;

    @Override
    public void start(Stage primaryStage) {
        // 正确引用Config中的屏幕尺寸（解决SCREEN_WIDTH/SCREEN_HEIGHT找不到错误）
        canvas = new Canvas(Config.SCREEN_WIDTH, Config.SCREEN_HEIGHT);
        gc = canvas.getGraphicsContext2D();

        // 初始化游戏循环
        gameLoop = new GameLoop();

        // 布局
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);

        // 按键监听
        scene.setOnKeyPressed(this::handleKeyPress);
        scene.setOnKeyReleased(this::handleKeyRelease);

        // 启动游戏循环
        startGameLoop();

        // 窗口配置
        primaryStage.setTitle("凌斗罗");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // 游戏主循环
    private void startGameLoop() {
        new Thread(() -> {
            while (true) {
                long startTime = System.nanoTime();

                // 更新游戏状态
                if (!gameLoop.isPaused() && !gameLoop.isGameOver()) {
                    gameLoop.update();
                }

                // 渲染画面（JavaFX UI需在主线程执行）
                javafx.application.Platform.runLater(() -> gameLoop.render(gc));

                // 控制帧率
                long elapsedTime = System.nanoTime() - startTime;
                long sleepTime = (long) (Config.DELTA_TIME * 1000000000) - elapsedTime;
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime / 1000000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    // 按键分发
    private void handleKeyPress(KeyEvent event) {
        gameLoop.handleKeyPress(event);
        // 快捷操作：S键存档，L键读档，P键暂停
        switch (event.getCode()) {
            case S:
                gameLoop.saveGame();
                break;
            case L:
                gameLoop.loadGame();
                break;
            case P:
                gameLoop.setPaused(!gameLoop.isPaused());
                break;
        }
    }

    private void handleKeyRelease(KeyEvent event) {
        gameLoop.handleKeyRelease(event);
    }

    public static void main(String[] args) {
        launch(args);
    }
}