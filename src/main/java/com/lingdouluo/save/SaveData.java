// src/main/java/com/lingdouluo/save/SaveData.java
package com.lingdouluo.save;

public class SaveData {

    private int playerHealth;
    private int playerMaxHealth;
    private int playerLives;
    private int playerScore;
    private int currentLevel;
    private double gameTime;
    private long saveTimestamp;

    // 武器信息
    private String currentWeapon;
    private int rifleAmmo;
    private int grenadeAmmo;

    // 游戏进度
    private boolean[] levelCompleted;

    public SaveData() {
        this.playerHealth = 3;
        this.playerMaxHealth = 3;
        this.playerLives = 3;
        this.playerScore = 0;
        this.currentLevel = 0;
        this.gameTime = 0;
        this.saveTimestamp = System.currentTimeMillis();
        this.currentWeapon = "rifle";
        this.rifleAmmo = -1; // 无限
        this.grenadeAmmo = 20;
        this.levelCompleted = new boolean[4]; // 4个关卡
    }

    // Getter和Setter方法
    public int getPlayerHealth() { return playerHealth; }
    public void setPlayerHealth(int playerHealth) { this.playerHealth = playerHealth; }

    public int getPlayerMaxHealth() { return playerMaxHealth; }
    public void setPlayerMaxHealth(int playerMaxHealth) { this.playerMaxHealth = playerMaxHealth; }

    public int getPlayerLives() { return playerLives; }
    public void setPlayerLives(int playerLives) { this.playerLives = playerLives; }

    public int getPlayerScore() { return playerScore; }
    public void setPlayerScore(int playerScore) { this.playerScore = playerScore; }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }

    public double getGameTime() { return gameTime; }
    public void setGameTime(double gameTime) { this.gameTime = gameTime; }

    public long getSaveTimestamp() { return saveTimestamp; }
    public void setSaveTimestamp(long saveTimestamp) { this.saveTimestamp = saveTimestamp; }

    public String getCurrentWeapon() { return currentWeapon; }
    public void setCurrentWeapon(String currentWeapon) { this.currentWeapon = currentWeapon; }

    public int getRifleAmmo() { return rifleAmmo; }
    public void setRifleAmmo(int rifleAmmo) { this.rifleAmmo = rifleAmmo; }

    public int getGrenadeAmmo() { return grenadeAmmo; }
    public void setGrenadeAmmo(int grenadeAmmo) { this.grenadeAmmo = grenadeAmmo; }

    public boolean[] getLevelCompleted() { return levelCompleted; }
    public void setLevelCompleted(boolean[] levelCompleted) { this.levelCompleted = levelCompleted; }

    public boolean isLevelCompleted(int levelIndex) {
        if (levelIndex >= 0 && levelIndex < levelCompleted.length) {
            return levelCompleted[levelIndex];
        }
        return false;
    }

    public void setLevelCompleted(int levelIndex, boolean completed) {
        if (levelIndex >= 0 && levelIndex < levelCompleted.length) {
            levelCompleted[levelIndex] = completed;
        }
    }

    @Override
    public String toString() {
        return String.format("SaveData{level=%d, health=%d, score=%d, time=%.1f}",
                currentLevel, playerHealth, playerScore, gameTime);
    }
}