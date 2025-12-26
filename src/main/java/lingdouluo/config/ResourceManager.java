// 位置：src/main/java/com/lingdouluo/config/ResourceManager.java
package lingdouluo.config;

import lingdouluo.entity.Player;

public class ResourceManager {
    private static boolean resourcesLoaded = false;

    public static void preloadPlayerImages() {
        if (!resourcesLoaded) {
            // 预加载player1和player2的所有图片
            Player dummyPlayer1 = new Player("player1", "dummy", "warrior");
            Player dummyPlayer2 = new Player("player2", "dummy", "tech");
            resourcesLoaded = true;
        }
    }
}