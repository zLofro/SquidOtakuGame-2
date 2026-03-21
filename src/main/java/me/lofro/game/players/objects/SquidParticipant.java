package me.lofro.game.players.objects;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

public class SquidParticipant {

    private @Getter @Setter String name;

    public SquidParticipant(@Nonnull String name) {
        this.name = name;
    }

    /**
     * @return the player associated with this participant if online, otherwise null.
     */
    public Player player() {
        return Bukkit.getPlayer(name);
    }

}
