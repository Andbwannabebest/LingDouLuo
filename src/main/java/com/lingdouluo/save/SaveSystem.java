// src/main/java/com/lingdouluo/save/SaveSystem.java
package com.lingdouluo.save;

import com.lingdouluo.config.Config;
import com.lingdouluo.entity.Player;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SaveSystem {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String SAVE_FILE = Config.SAVES_PATH + "save1.json";

    public static void saveGame(Player player, int currentLevel, double gameTime) {
        SaveData saveData = new SaveData();

        saveData.setPlayerHealth(player.getHealth());
        saveData.setPlayerLives(player.getLives());
        saveData.setPlayerScore(player.getScore());
        saveData.setCurrentLevel(currentLevel);
        saveData.setGameTime(gameTime);
        saveData.setSaveTimestamp(System.currentTimeMillis());

        try {
            // 确保保存目录存在
            Files.createDirectories(Paths.get(Config.SAVES_PATH));

            // 写入文件
            FileWriter writer = new FileWriter(SAVE_FILE);
            gson.toJson(saveData, writer);
            writer.flush();
            writer.close();

            System.out.println("游戏已保存: " + SAVE_FILE);
        } catch (IOException e) {
            System.err.println("保存游戏失败: " + e.getMessage());
        }
    }

    public static SaveData loadGame() {
        File saveFile = new File(SAVE_FILE);

        if (!saveFile.exists()) {
            System.out.println("存档文件不存在: " + SAVE_FILE);
            return null;
        }

        try {
            FileReader reader = new FileReader(SAVE_FILE);
            SaveData saveData = gson.fromJson(reader, SaveData.class);
            reader.close();

            System.out.println("游戏已加载: " + SAVE_FILE);
            return saveData;
        } catch (IOException e) {
            System.err.println("加载游戏失败: " + e.getMessage());
            return null;
        }
    }

    public static boolean saveExists() {
        return new File(SAVE_FILE).exists();
    }

    public static void deleteSave() {
        File saveFile = new File(SAVE_FILE);
        if (saveFile.exists()) {
            if (saveFile.delete()) {
                System.out.println("存档已删除: " + SAVE_FILE);
            } else {
                System.err.println("删除存档失败: " + SAVE_FILE);
            }
        }
    }
}