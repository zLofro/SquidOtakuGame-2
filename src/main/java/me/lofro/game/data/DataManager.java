package me.lofro.game.data;

import me.lofro.game.SquidGame;
import me.lofro.game.data.enums.SquidDataType;
import me.lofro.game.data.utils.JsonConfig;
import me.lofro.game.data.commands.DataCMD;
import me.lofro.game.global.interfaces.Instantiable;

/**
 * A class to manage, backup, & restore, the state of the application.
 *
 */
public class DataManager extends Instantiable<SquidGame> {
    private final JsonConfig pDataConfig;
    private final JsonConfig gDataConfig;

    /**
     * Default constructor for bukkit instantiation.
     * 
     * @param instance The plugin instance.
     * @throws Exception If any of the config files cannot be created at runtime.
     */
    public DataManager(final SquidGame instance) throws Exception {
        super(instance);
        this.pDataConfig = JsonConfig.cfg("pdata.json", instance);
        this.gDataConfig = JsonConfig.cfg("gdata.json", instance);

        ins().registerCommands(ins().getCommandManager(), new DataCMD(this));
    }

    /**
     * A method that saves the current state of the application to json files.
     */
    public void save() {
        ins().getPManager().save(pDataConfig);
        ins().getGManager().save(gDataConfig);
    }

    /**
     * Function to save an specific data config.
     *
     * @param squidDataType plugin data type.
     */
    public void save(SquidDataType squidDataType) {
        switch (squidDataType) {
            case GAME_DATA -> ins().getGManager().save(gDataConfig);
            case PLAYER_DATA -> ins().getPManager().save(pDataConfig);
        }
    }

    public void printState() {
        ins().getLogger().info(SquidGame.gson().toJson(instance.getGManager().gameData()));
    }

    /**
     * @return the pDataConfig.
     */
    public JsonConfig pDataConfig() {
        return pDataConfig;
    }

    /**
     * @return the gDataConfig.
     */
    public JsonConfig gDataConfig() {
        return gDataConfig;
    }

}
