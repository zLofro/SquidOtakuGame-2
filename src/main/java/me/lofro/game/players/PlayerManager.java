package me.lofro.game.players;

import me.lofro.game.SquidGame;
import me.lofro.game.data.types.PlayerData;
import me.lofro.game.data.utils.JsonConfig;
import me.lofro.game.global.interfaces.Restorable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import me.lofro.game.players.commands.PlayerManagerCMD;

/**
 * A class to manage the players in the game, their roles & status, and interact
 * with the dataset.
 *
 */
public class PlayerManager extends Restorable<SquidGame> {
    private PlayerData playerData;

    public PlayerManager(final SquidGame instance) {
        super(instance);
        restore(instance.getDManager().pDataConfig());
        instance.registerCommands(instance.getCommandManager(), new PlayerManagerCMD(this));
    }

    public PlayerManager() {
        super();
        this.playerData = new PlayerData();
    }

    @Override
    protected void restore(JsonConfig jsonConfig) {
        this.playerData = SquidGame.gson().fromJson(jsonConfig.getJsonObject(), PlayerData.class);
    }

    @Override
    public void save(JsonConfig jsonConfig) {
        jsonConfig.setJsonObject(SquidGame.gson().toJsonTree(playerData).getAsJsonObject());
        try {
            jsonConfig.save();
        } catch (Exception e) {
            SquidGame.getInstance().getLogger().info(e.getMessage());
        }
    }

    public PlayerData pData() {
        return playerData;
    }

    public boolean isPlayer(Player player) {
        return playerData.getPlayer(player.getName()) != null;
    }

    public boolean isGuard(Player player) {
        return playerData.getGuard(player.getName()) != null;
    }

    public boolean isDead(Player player) {
        return playerData.getPlayer(player.getName()).isDead();
    }

    public void guardMessage(Component text) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (isGuard(player))
                player.sendMessage(text);
        });
    }

    public void adminMessage(Component text) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.isOp())
                player.sendMessage(text);
        });
    }

}
