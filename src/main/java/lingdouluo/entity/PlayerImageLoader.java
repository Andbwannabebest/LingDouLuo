package lingdouluo.entity; // 必须和存放路径一致

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 玩家动作图片加载工具（仅新增，不修改原有代码）
 */
public class PlayerImageLoader {
    // 缓存：key=玩家标识_方向_动作（如player1_left_initialize），value=对应图片
    private static final Map<String, BufferedImage> IMG_CACHE = new HashMap<>();

    // 初始化加载所有玩家动作图片（游戏启动时调用1次）
    public static void loadAllPlayerImages() {
        // 玩家1：left/right 所有动作
        loadPlayerImages("player1", "left");
        loadPlayerImages("player1", "right");
        // 玩家2：left/right 所有动作（补充player2的left路径后取消注释）
        // loadPlayerImages("player2", "left");
        loadPlayerImages("player2", "right");
    }

    // 加载单个玩家+方向的所有动作图片
    private static void loadPlayerImages(String playerId, String direction) {
        // 所有动作类型（与你的resources文件名完全匹配）
        String[] actions = {"down", "initialize", "jump1", "jump2", "jump3", "run1", "run2", "run3", "up"};
        for (String action : actions) {
            String key = playerId + "_" + direction + "_" + action;
            String resourcePath = "/" + playerId + "/" + direction + "/" + action + ".png";
            try (InputStream is = PlayerImageLoader.class.getResourceAsStream(resourcePath)) {
                if (is == null) {
                    System.err.println("图片资源不存在：" + resourcePath);
                    continue;
                }
                BufferedImage img = ImageIO.read(is);
                IMG_CACHE.put(key, img);
            } catch (IOException e) {
                System.err.println("加载图片失败：" + resourcePath);
                e.printStackTrace();
            }
        }
    }

    // 获取指定玩家+方向+动作的图片（对外调用）
    public static BufferedImage getPlayerImage(String playerId, String direction, String action) {
        return IMG_CACHE.get(playerId + "_" + direction + "_" + action);
    }
}