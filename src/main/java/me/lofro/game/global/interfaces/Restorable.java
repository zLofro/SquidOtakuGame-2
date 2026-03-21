package me.lofro.game.global.interfaces;

import org.bukkit.plugin.java.JavaPlugin;

import me.lofro.game.data.utils.JsonConfig;

/**
 * A class that holds common methods for all Restoreable Managers.
 *
 */
public abstract class Restorable<T extends JavaPlugin> extends Instantiable<T> {
    /**
     * An object that extends {@link JavaPlugin}
     */
    protected transient volatile T jPlugin;

    /**
     * Method to contain your restore logic.
     * 
     * @param jsonConfig The {@link JsonConfig} object to read from.
     */
    protected abstract void restore(JsonConfig jsonConfig);

    /**
     * Default method to be implemented by a child class that needs storing of state
     * to json files.
     * 
     * @param jsonConfig The {@link JsonConfig} object to save to.
     */
    public abstract void save(JsonConfig jsonConfig);

    /**
     * Default constructor for bukkit instantiation.
     * 
     * @param plugin The plugin instance.
     */
    public Restorable(T plugin) {
        super(plugin);
    }

    /**
     * Default constructor for testing environments.
     */
    public Restorable() {
        super(null);
    }

}
