package lingdouluo.save;

import lingdouluo.config.Config;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 游戏存档系统，负责存档的保存与加载
 */
public class SaveSystem {

    /**
     * 保存游戏存档
     * @param saveData 存档数据对象
     * @return 是否保存成功
     */
    public static boolean saveGame(SaveData saveData) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(Config.SAVE_PATH))) {
            oos.writeObject(saveData);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 加载游戏存档
     * @return 存档数据对象（null表示加载失败）
     */
    public static SaveData loadGame() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(Config.SAVE_PATH))) {
            return (SaveData) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}