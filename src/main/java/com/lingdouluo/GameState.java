// src/main/java/com/lingdouluo/GameState.java
package com.lingdouluo;

public enum GameState {
    MENU,           // 主菜单
    PLAYING,        // 游戏中
    PAUSED,         // 暂停
    GAME_OVER,      // 游戏结束
    LEVEL_COMPLETE, // 关卡完成
    BOSS_FIGHT,     // Boss战
    CUTSCENE        // 过场动画
}