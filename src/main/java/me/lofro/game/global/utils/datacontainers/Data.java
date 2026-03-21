package me.lofro.game.global.utils.datacontainers;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;

/**
 * Util class that manages the Persistent Data Containers from Bukkit.org.
 *
 * @author <a href="https://github.com/Clipi-12">Clipi</a>
 */
public class Data {
    public static NamespacedKey key(String key, JavaPlugin javaPlugin) {
        return new NamespacedKey(javaPlugin, key);
    }

    public static PersistentDataContainer getData(@Nonnull PersistentDataHolder dataHolder) throws PlayerIsNotOnlineException {
        if (dataHolder instanceof Player && !((Player) dataHolder).isOnline()) throw new PlayerIsNotOnlineException((Player) dataHolder);
        return dataHolder.getPersistentDataContainer();
    }

    public static <T> void set(@Nonnull PersistentDataContainer data, String key, JavaPlugin javaPlugin, PersistentDataType<T, T> type, T value) {
        data.set(key(key, javaPlugin), type, value);
    }

    public static <T> T get(@Nonnull PersistentDataContainer data, String key, JavaPlugin javaPlugin, PersistentDataType<T, T> type) {
        return data.get(key(key, javaPlugin), type);
    }

    public static <T> boolean has(@Nonnull PersistentDataContainer data, String key, JavaPlugin javaPlugin, PersistentDataType<T, T> type) {
        return data.has(key(key, javaPlugin), type);
    }
}
