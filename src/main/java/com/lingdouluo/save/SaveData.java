// src/main/java/com/lingdouluo/save/SaveData.java
package com.lingdouluo.save;

public class SaveData {

    // 玩家1数据
    private int player1Health;
    private int player1MaxHealth;
    private int player1Lives;
    private int player1Score;

    // 玩家2数据
    private int player2Health;
    private int player2MaxHealth;
    private int player2Lives;
    private int player2Score;

    // 游戏进度
    private int currentLevel;
    private double gameTime;
    private long saveTimestamp;

    // 武器信息
    private String player1Weapon;
    private String player2Weapon;

    public SaveData() {
        this.player1Health = 3;
        this.player1MaxHealth = 3;
        this.player1Lives = 3;
        this.player1Score = 0;

        this.player2Health = 3;
        this.player2MaxHealth = 3;
        this.player2Lives = 3;
        this.player2Score = 0;

        this.currentLevel = 0;
        this.gameTime = 0;
        this.saveTimestamp = System.currentTimeMillis();
        this.player1Weapon = "rifle";
        this.player2Weapon = "rifle";
    }

    // Getter和Setter方法
    public int getPlayer1Health() { return player1Health; }
    public void setPlayer1Health(int player1Health) { this.player1Health = player1Health; }

    public int getPlayer1MaxHealth() { return player1MaxHealth; }
    public void setPlayer1MaxHealth(int player1MaxHealth) { this.player1MaxHealth = player1MaxHealth; }

    public int getPlayer1Lives() { return player1Lives; }
    public void setPlayer1Lives(int player1Lives) { this.player1Lives = player1Lives; }

    public int getPlayer1Score() { return player1Score; }
    public void setPlayer1Score(int player1Score) { this.player1Score = player1Score; }

    public int getPlayer2Health() { return player2Health; }
    public void setPlayer2Health(int player2Health) { this.player2Health = player2Health; }

    public int getPlayer2MaxHealth() { return player2MaxHealth; }
    public void setPlayer2MaxHealth(int player2MaxHealth) { this.player2MaxHealth = player2MaxHealth; }

    public int getPlayer2Lives() { return player2Lives; }
    public void setPlayer2Lives(int player2Lives) { this.player2Lives = player2Lives; }

    public int getPlayer2Score() { return player2Score; }
    public void setPlayer2Score(int player2Score) { this.player2Score = player2Score; }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }

    public double getGameTime() { return gameTime; }
    public void setGameTime(double gameTime) { this.gameTime = gameTime; }

    public long getSaveTimestamp() { return saveTimestamp; }
    public void setSaveTimestamp(long saveTimestamp) { this.saveTimestamp = saveTimestamp; }

    public String getPlayer1Weapon() { return player1Weapon; }
    public void setPlayer1Weapon(String player1Weapon) { this.player1Weapon = player1Weapon; }

    public String getPlayer2Weapon() { return player2Weapon; }
    public void setPlayer2Weapon(String player2Weapon) { this.player2Weapon = player2Weapon; }

    @Override
    public String toString() {
        return String.format("SaveData{level=%d, p1Health=%d, p2Health=%d, time=%.1f}",
                currentLevel, player1Health, player2Health, gameTime);
    }
}