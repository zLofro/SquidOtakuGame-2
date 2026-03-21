package me.lofro.game.global.utils.datacontainers;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class PlayerIsNotOnlineException extends Exception {
    @Nonnull
    public final Player player;

    PlayerIsNotOnlineException(Player player) {
        super("The player " + player.getName() + " is not currently online.");
        this.player = player;
    }
}
