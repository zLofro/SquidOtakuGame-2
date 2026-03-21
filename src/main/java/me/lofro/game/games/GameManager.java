package me.lofro.game.games;

import com.google.common.collect.ImmutableList;
import me.lofro.game.games.backrooms.BackRoomsManager;
import me.lofro.game.games.backrooms.commands.BackRoomsCMD;
import me.lofro.game.games.backrooms.types.BackRoomsData;
import me.lofro.game.games.commands.GameManagerCMD;
import me.lofro.game.games.glass.GlassGameManager;
import me.lofro.game.games.glass.commands.GlassGameCMD;
import me.lofro.game.games.glass.types.GlassGameData;
import me.lofro.game.games.tntTag.TNTManager;
import me.lofro.game.games.tntTag.commands.TNTCMD;
import org.bukkit.Bukkit;

import lombok.Getter;
import me.lofro.game.SquidGame;
import me.lofro.game.data.types.GameData;
import me.lofro.game.data.utils.JsonConfig;
import me.lofro.game.games.greenlight.GreenLightManager;
import me.lofro.game.games.greenlight.commands.GreenLightCMD;
import me.lofro.game.games.greenlight.types.GreenLightData;
import me.lofro.game.global.interfaces.Restorable;
import me.lofro.game.global.utils.timer.GameTimer;

/**
 * A class to manage all commands, objects, events, & listeners for each game in
 * the plugin.
 *
 */
public class GameManager extends Restorable<SquidGame> {

    private final @Getter SquidGame squidInstance;

    private final @Getter GameTimer timer;

    private GameData gData;

    private final @Getter GreenLightManager greenLightManager;
    private final @Getter TNTManager tntManager;
    private final @Getter BackRoomsManager backRoomsManager;
    private final @Getter GlassGameManager glassGameManager;

    public GameManager(final SquidGame squidInstance) {
        this.squidInstance = squidInstance;
        // Restore data from dManager json files.
        this.restore(squidInstance.getDManager().gDataConfig());
        // Initialize the games.
        var baseWorld = Bukkit.getWorlds().getFirst();

        this.greenLightManager = new GreenLightManager(this, baseWorld);
        this.tntManager = new TNTManager(this);
        this.backRoomsManager = new BackRoomsManager(this, baseWorld);
        this.glassGameManager = new GlassGameManager(this, baseWorld);
        // Initialize the Timer.
        this.timer = new GameTimer();
        // Run the task.
        this.timer.runTaskTimerAsynchronously(squidInstance, 20L, 20L);
        // Register game commands.
        squidInstance.registerCommands(squidInstance.getCommandManager(),
                new GameManagerCMD(this),
                new GreenLightCMD(greenLightManager),
                new TNTCMD(tntManager),
                new BackRoomsCMD(backRoomsManager),
                new GlassGameCMD(glassGameManager)
                );

        // Sets the location command completion.
        SquidGame.getInstance().getCommandManager().getCommandCompletions().registerCompletion(
                "@location", c -> ImmutableList.of("x,y,z"));
    }

    @Override
    protected void restore(JsonConfig jsonConfig) {
        if (jsonConfig.getJsonObject().entrySet().isEmpty()) {
            this.gData = new GameData(new GreenLightData(), new BackRoomsData(), new GlassGameData());
        } else {
            this.gData = SquidGame.gson().fromJson(jsonConfig.getJsonObject(), GameData.class);
        }
    }

    @Override
    public void save(JsonConfig jsonConfig) {
        jsonConfig.setJsonObject(SquidGame.gson().toJsonTree(gData).getAsJsonObject());
        try {
            jsonConfig.save();
        } catch (Exception e) {
            squidInstance.getLogger().info(e.getMessage());
        }
    }

    /**
     * @return the GameData object.
     */
    public GameData gameData() {
        return this.gData;
    }
}
