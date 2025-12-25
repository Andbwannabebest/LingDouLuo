package com.lingdouluo.save;

/**
 * 存档数据封装类，存储游戏核心状态
 */
public class SaveData {
    // 玩家1状态
    private double player1X;
    private double player1Y;
    private int player1Health;
    private String player1Name;
    private String player1Char;
    // 玩家2状态
    private double player2X;
    private double player2Y;
    private int player2Health;
    private String player2Name;
    private String player2Char;
    // 游戏全局状态
    private String currentLevel;
    private long score;
    private long playTime;

    // 空构造器（供序列化/反序列化使用）
    public SaveData() {}

    // 全参构造器
    public SaveData(double player1X, double player1Y, int player1Health, String player1Name, String player1Char,
                    double player2X, double player2Y, int player2Health, String player2Name, String player2Char,
                    String currentLevel, long score, long playTime) {
        this.player1X = player1X;
        this.player1Y = player1Y;
        this.player1Health = player1Health;
        this.player1Name = player1Name;
        this.player1Char = player1Char;
        this.player2X = player2X;
        this.player2Y = player2Y;
        this.player2Health = player2Health;
        this.player2Name = player2Name;
        this.player2Char = player2Char;
        this.currentLevel = currentLevel;
        this.score = score;
        this.playTime = playTime;
    }

    // ==================== Getter & Setter ====================
    public double getPlayer1X() { return player1X; }
    public void setPlayer1X(double player1X) { this.player1X = player1X; }
    public double getPlayer1Y() { return player1Y; }
    public void setPlayer1Y(double player1Y) { this.player1Y = player1Y; }
    public int getPlayer1Health() { return player1Health; }
    public void setPlayer1Health(int player1Health) { this.player1Health = player1Health; }
    public String getPlayer1Name() { return player1Name; }
    public void setPlayer1Name(String player1Name) { this.player1Name = player1Name; }
    public String getPlayer1Char() { return player1Char; }
    public void setPlayer1Char(String player1Char) { this.player1Char = player1Char; }

    public double getPlayer2X() { return player2X; }
    public void setPlayer2X(double player2X) { this.player2X = player2X; }
    public double getPlayer2Y() { return player2Y; }
    public void setPlayer2Y(double player2Y) { this.player2Y = player2Y; }
    public int getPlayer2Health() { return player2Health; }
    public void setPlayer2Health(int player2Health) { this.player2Health = player2Health; }
    public String getPlayer2Name() { return player2Name; }
    public void setPlayer2Name(String player2Name) { this.player2Name = player2Name; }
    public String getPlayer2Char() { return player2Char; }
    public void setPlayer2Char(String player2Char) { this.player2Char = player2Char; }

    public String getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(String currentLevel) { this.currentLevel = currentLevel; }
    public long getScore() { return score; }
    public void setScore(long score) { this.score = score; }
    public long getPlayTime() { return playTime; }
    public void setPlayTime(long playTime) { this.playTime = playTime; }
}